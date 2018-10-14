package com.dewey.rpc.client;


import com.dewey.rpc.common.base.Request;
import com.dewey.rpc.common.base.Response;
import com.dewey.rpc.registry.Discovery;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 *Rpc代理
 *
 * @author dewey
 * @date 2018/10/14 12:42
 */
public class RpcProxy {

    private static final Logger logger = Logger.getLogger(RpcProxy.class);

    private String serviceAddress;

    private Discovery discovery;

    public RpcProxy(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public RpcProxy(Discovery discovery) {
        this.discovery = discovery;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(final Class<?> interfaceClass){
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Request request = new Request();
                request.setRequestId(UUID.randomUUID().toString());
                request.setInterfaceName(method.getDeclaringClass().getName());
                request.setRequestId(method.getName());
                request.setParaTypes(method.getParameterTypes());
                request.setParas(method.getParameters());

                if (discovery != null) {
                    String serviceName = interfaceClass.getName();

                    serviceAddress = discovery.discovery(serviceName);
                    logger.debug(String.format("服务调用：服务名 %s，服务地址 %s",serviceName,serviceAddress));
                }

                if(StringUtils.isEmpty(serviceAddress)){
                    throw new RuntimeException("未发现相应服务");
                }

                String[] arr = serviceAddress.split(":");
                String host = arr[0];
                int port = Integer.parseInt(arr[1]);

                RpcClient rpcClient = new RpcClient(host,port);

                Response response = rpcClient.send(request);

                if(response == null){
                    throw new RuntimeException("返回为空");
                }

                if(response.getException()!=null){
                    throw response.getException();
                }else {
                    return response.getResult();
                }
            }
        });
    }
}
