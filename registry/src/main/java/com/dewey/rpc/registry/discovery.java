package com.dewey.rpc.registry;

/**
 * @author deweyding
 */
public interface discovery {
    /**
     * 根据服务名称查询服务地址
     * @param serviceName
     * @return
     */
    String discovery(String serviceName);
}
