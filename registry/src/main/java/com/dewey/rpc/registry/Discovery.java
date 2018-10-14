package com.dewey.rpc.registry;

/**
 * @author dewey
 * @date 2018/9/1 23:36
 */
public interface Discovery {
    /**
     * 根据服务名称查询服务地址
     * @param serviceName
     * @return
     */
    String discovery(String serviceName);
}
