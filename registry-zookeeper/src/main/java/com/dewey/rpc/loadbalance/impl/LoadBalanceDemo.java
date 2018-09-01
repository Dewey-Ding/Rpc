package com.dewey.rpc.loadbalance.impl;

import java.util.List;
import java.util.Random;

/**
 * @author dewey
 * @date 2018/9/1 23:36
 */
public class LoadBalanceDemo implements com.dewey.rpc.loadbalance.LoadBalance{

    @Override
    public String randomLoad(List<String> addressList){
        int count = addressList.size();
        Random random = new Random();
        int result = random.nextInt(count);
        return addressList.get(result);
    }

}
