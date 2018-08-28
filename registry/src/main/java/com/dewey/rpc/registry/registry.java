package com.dewey.rpc.registry;

/**
 * @author deweyding
 * @
 */
public interface registry {
    /**
     * 服务注册
     * @param serviceName
     * @param serviceAddress
     */
    void register(String serviceName,String serviceAddress);
}
