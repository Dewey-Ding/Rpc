package com.dewey.rpc.registry;

import com.dewey.rpc.common.Constants;
import com.dewey.rpc.loadbalance.LoadBalance;
import com.dewey.rpc.loadbalance.impl.LoadBalanceDemo;
import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author dewey
 * @date 2018/9/1 23:36
 * 服务发现
 */
public class ZkDiscovery implements Discovery {

    private static final Logger logger = Logger.getLogger(ZkDiscovery.class);

    private String zkAddress;

    private ZooKeeper zk;

    private ZkBase zkBase;

    private LoadBalance loadBalance;

    public ZkDiscovery(String zkAddress){
        this.zkAddress = zkAddress;
        zkBase = new ZkBase();
        zk = zkBase.connectZkServer(zkAddress);
    }
    @Override
    public String discovery(String serviceName) {
        if(serviceName==null){
            logger.info("查找服务名为空");
            return "";
        }
        loadBalance = new LoadBalanceDemo();
        String servicePath = Constants.ZK_SERVICE_PATH+"/"+serviceName;
        try {
            Stat stat = zk.exists(servicePath,false);
            if(stat==null){
                throw new RuntimeException(String.format("找不到对应的服务:%s ",servicePath));
            }
            List<String> addressList = zk.getChildren(servicePath,null,null);
            if(CollectionUtils.isEmpty(addressList)){
                throw new RuntimeException(String.format("找不到对应的服务:%s",servicePath));
            }
            String address;
            if(addressList.size()==1){
                address = addressList.get(0);
                logger.info(String.format("找到服务节点:%s",address));
            }else{
                address = loadBalance.randomLoad(addressList);
                logger.info(String.format("找到服务节点:%s",address));
            }
            String[] strs = address.split("_");
            if(strs.length == 0){
                logger.info(String.format("address解析出错：%s",address));
                return null;
            }
            String host = address.split("_")[0];
            return host;
        } catch (InterruptedException|KeeperException e) {
            logger.error("查找对应服务失败",e);
        }finally {
            zkBase.disConnect(zk);
        }
        return "";
    }
}
