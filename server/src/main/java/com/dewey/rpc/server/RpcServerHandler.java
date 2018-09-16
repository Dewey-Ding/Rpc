package com.dewey.rpc.server;

import com.dewey.rpc.common.base.Request;
import com.dewey.rpc.common.base.Response;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.log4j.Logger;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 请求处理
 * @author dewey
 * @date 2018/9/14 20:05
 */
public class RpcServerHandler extends SimpleChannelInboundHandler<Request> {

    private Logger logger = Logger.getLogger(RpcServerHandler.class);

    private Map<String,Object> handlerMapping;

    /**
     * 业务执行线程池
     */
    private ThreadPoolExecutor threadPoolExecutor;

    public RpcServerHandler(Map<String,Object> handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request msg) throws Exception {
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Response response = new Response();
                response.setRequesId(msg.getRequestId());
                try{
                    Object result = handler(msg);
                    response.setResult(result);
                }catch (Exception e){
                    response.setException(e);
                }
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            }
        });
    }

    /**
     * 调用对应的方法执行业务逻辑
     * @param request
     * @return
     */
    public Object handler(Request request) throws Exception{
        String serviceName = request.getInterfaceName();

        Object serviceBean = handlerMapping.get(serviceName);
        if(serviceBean==null){
            throw new RuntimeException(String.format("没有对应的服务：%s",serviceName));
        }

        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParaTypes();
        Object[] parameters = request.getParas();

        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
        return serviceFastMethod.invoke(serviceBean, parameters);
    }
}
