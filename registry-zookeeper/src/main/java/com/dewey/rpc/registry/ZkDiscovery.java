package com.dewey.rpc.registry;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 服务发现
 * @author deweyding
 */
public class ZkDiscovery implements discovery{

    private static final Logger logger = Logger.getLogger(ZkDiscovery.class);

    private String zkAddress;

    private ZooKeeper zk;

    private ZkBase zkBase;

    public ZkDiscovery(String zkAddress){
        this.zkAddress = zkAddress;
        zkBase = new ZkBase();
    }
    @Override
    public String discovery(String serviceName) {
        if(serviceName==null){
            logger.info("查找服务名为空");
            return "";
        }
        ZooKeeper zk = zkBase.connectZkServer(zkAddress);
        String servicePath = Constants.ZK_DATA_PATH+"/"+serviceName;
        try {
            Stat stat = zk.exists(servicePath,false);
            if(stat==null){
                throw new RuntimeException(String.format("找不到对应的服务:%s ",servicePath));
            }
            List<String> addressList = zk.getChildren(servicePath,false);
            if(CollectionUtils.isEmpty(addressList)){
                throw new RuntimeException(String.format("找不到对应的服务:%s",servicePath));
            }
            String address;
            if(addressList.size()==1){
                address = addressList.get(0);
                logger.info(String.format("找到服务节点:%s",address));
            }else{
                address = LoadBalance.;
                logger.info(String.format("找到服务节点:%s",address));
            }
            String addressPath = servicePath+"/"+address;
            return zk.
        } catch (InterruptedException|KeeperException e) {
            logger.error("查找对应服务失败",e);
        }finally {
            zkBase.disConnect(zk);
        }
    }
}
