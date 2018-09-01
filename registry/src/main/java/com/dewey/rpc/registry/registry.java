package com.dewey.rpc.registry;

/**
 * @author dewey
 * @date 2018/9/1 23:36
 */
public interface Registry {
    /**
     * 服务注册
     * @param serviceName
     * @param serviceAddress
     */
    void register(String serviceName,String serviceAddress);
}
