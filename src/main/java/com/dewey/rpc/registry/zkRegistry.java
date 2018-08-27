package com.dewey.rpc.registry;

import org.apache.log4j.Logger;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;


@PropertySource("rpc.properties")
public class zkRegistry {

    private static final Logger logger = Logger.getLogger(zkRegistry.class);

    @Value("$zk_address")
    private String zkAddress;

    private ZkBase zkBase = new ZkBase(zkAddress);


    public void register(String data){
        if(data!=null){
            ZooKeeper zk = zkBase.connectZkServer();
            if(zk != null){

            }
        }
    }

}
