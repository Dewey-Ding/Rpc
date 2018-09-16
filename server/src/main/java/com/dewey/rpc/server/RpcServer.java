package com.dewey.rpc.server;

import com.dewey.rpc.common.base.Request;
import com.dewey.rpc.common.base.Response;
import com.dewey.rpc.common.codec.RpcDecoder;
import com.dewey.rpc.common.codec.RpcEncoder;
import com.dewey.rpc.registry.ZkRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author dewey
 * @date 2018/9/2 23:07
 */
public class RpcServer implements ApplicationContextAware,InitializingBean {

    private static final Logger logger = Logger.getLogger(RpcServer.class);

    /**
     * 存储映射
     */
    private Map<String,Object> handlerMapping = new HashMap<>();

    /**
     * 用于接收连接
     */
    private EventLoopGroup bossGroup = null;

    /**
     * 用于处理已经接收的连接
     */
    private EventLoopGroup workerGroup = null;

    /**
     * 服务地址 ip:port
     */
    private String serviceAddress;

    /**
     * zk注册中心
     */
    private ZkRegistry zkRegistry;

    public RpcServer(String serviceAddress, ZkRegistry zkRegistry) {
        this.serviceAddress = serviceAddress;
        this.zkRegistry = zkRegistry;
    }

    /**
     * 获取容器中所有配置了RpcService注解的bean
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String,Object> serviceBeanMapping = applicationContext.getBeansWithAnnotation(RpcService.class);
        if(!CollectionUtils.isEmpty(serviceBeanMapping)){
            for(Object one:serviceBeanMapping.values()){
                String serviceName = one.getClass().getAnnotation(RpcService.class).value().getName();
                handlerMapping.put(serviceName,one);
                logger.info(String.format("load service success serviceName = %s",serviceName));
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if(bossGroup==null&&workerGroup==null) {
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
        }
        try{
            //引导服务器
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new RpcDecoder(Request.class));
                            pipeline.addLast(new RpcEncoder(Response.class));
                            pipeline.addLast(new RpcServerHandler(handlerMapping));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG,1024)
                    .childOption(ChannelOption.SO_KEEPALIVE,true);

            String[] var1 = serviceAddress.split(":");
            String host = var1[1];
            String port = var1[2];
            ChannelFuture channelFuture = bootstrap.bind(host,Integer.valueOf(port)).sync();
            logger.info("netty server启动成功");

            this.registerAllService();

            channelFuture.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * 将所有服务注册到zookeeper
     */
    public void registerAllService(){
        if(handlerMapping==null||zkRegistry==null){
            logger.info("服务注册失败");
        }
        for (String serviceName:handlerMapping.keySet()) {
            zkRegistry.register(serviceName,serviceAddress);
            logger.info(String.format("服务%s注册成功",serviceName));
        }
    }
}