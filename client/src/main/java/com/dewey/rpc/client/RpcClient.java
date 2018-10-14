package com.dewey.rpc.client;

import com.dewey.rpc.common.base.Request;
import com.dewey.rpc.common.base.Response;
import com.dewey.rpc.common.codec.RpcDecoder;
import com.dewey.rpc.common.codec.RpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.log4j.Logger;

/**
 *Rpc客户端
 *
 * @author dewey
 * @date 2018/10/14 11:20
 */
public class RpcClient extends SimpleChannelInboundHandler<Response> {

    private static Logger logger = Logger.getLogger(RpcClient.class);

    private final String host;

    private final int port;

    private Response response;

    public RpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Response response) throws Exception {
        this.response = response;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("API调用错误：",cause);
        ctx.close();
    }

    public Response send(Request request) throws Exception{
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            //netty客户端引导启动
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline channelPipeline = socketChannel.pipeline();
                    channelPipeline.addLast(new RpcEncoder(Request.class));
                    channelPipeline.addLast(new RpcDecoder(Response.class));
                    channelPipeline.addLast(RpcClient.this);
                }
            });
            bootstrap.option(ChannelOption.TCP_NODELAY,true);
            ChannelFuture channelFuture = bootstrap.connect(host,port).sync();
            Channel channel = channelFuture.channel();
            channel.writeAndFlush(request).sync();
            channel.closeFuture().sync();

            return response;

        }finally {
            group.shutdownGracefully();

        }
    }
}
