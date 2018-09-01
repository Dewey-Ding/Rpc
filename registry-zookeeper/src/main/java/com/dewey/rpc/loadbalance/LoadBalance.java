package com.dewey.rpc.loadbalance;

import java.util.List;

/**
 * @author dewey
 * @date 2018/9/1 23:36
 * 负载均衡接口
 */
public interface LoadBalance {
    /**
     * 随机负载
     * @param addressList
     * @return
     */
    String randomLoad(List<String> addressList);
}
