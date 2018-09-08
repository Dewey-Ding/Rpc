package com.dewey.rpc.server;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dewey
 * @date 2018/9/2 23:07
 */
public class RpcServer implements ApplicationContextAware,InitializingBean {

    private static final Logger logger = Logger.getLogger(RpcServer.class);

    private Map<String,Object> handerMapping = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String,Object> serviceBeanMapping = applicationContext.getBeansWithAnnotation(RpcService.class);
        if(!CollectionUtils.isEmpty(serviceBeanMapping)){
            for(Object one:serviceBeanMapping.values()){
                String serviceName = one.getClass().getAnnotation(RpcService.class).value().getName();
                handerMapping.put(serviceName,one);
                logger.info(String.format("load service success serviceName = %s",serviceName));
            }
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}