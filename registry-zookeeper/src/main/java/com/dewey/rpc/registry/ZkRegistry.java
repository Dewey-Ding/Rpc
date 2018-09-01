package com.dewey.rpc.registry;

import com.dewey.rpc.common.Constants;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * @author dewey
 * @date 2018/9/1 23:36
 * 服务注册
 */
public class ZkRegistry implements Registry {

    private static final Logger logger = Logger.getLogger(ZkRegistry.class);

    private String zkAddress;

    private ZkBase zkBase;

    private ZooKeeper zk;

    public ZkRegistry(String zkAddress){
        this.zkAddress = zkAddress;
        zkBase = new ZkBase();
        zk = zkBase.connectZkServer(zkAddress);
    }

    /**
     * 服务注册
     * @param path 注册路径
     * @param data 注册数据
     */
    @Override
    public void register(String path,String data){
        if(path!=null&&data!=null){
            if(zk != null){
                createNode(zk,path,data);
            }
        }
    }

    /**
     * 创建zk节点
     * @param zk
     * @param serviceName 服务名称
     * @param serviceAddress 服务地址
     */
    private void createNode(ZooKeeper zk,String serviceName,String serviceAddress){
        if(zk==null||serviceName==null||serviceAddress==null){
            logger.error(String.format("信息不全,无法创建节点(0代表为null)：zk = %d,path = %d,data = %d",zk==null?0:1,serviceName==null?0:1,serviceName==null?0:1));
            return ;
        }
        createRegistryNode(zk);
        try{
            String servicePath = Constants.ZK_SERVICE_PATH+"/"+serviceName;
            Stat stat = zk.exists(servicePath,false);
            if(stat==null) {
                String pathReturn = zk.create(servicePath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                logger.info(String.format(String.format("创建服务名节点,servicePath = %s", pathReturn)));
            }
            String addressPath = servicePath + "/" + serviceAddress + "_";
            zk.create(addressPath,serviceAddress.getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
            logger.info(String.format("服务临时节点创建成功，servicePath：%s  address:%s",addressPath,serviceAddress));
        } catch (InterruptedException|KeeperException e) {
            logger.error("创建节点错误",e);
        }
    }

    /**
     * 如果根节点不存在则创建
     * @param zk
     */
    public void createRegistryNode(ZooKeeper zk){
        try {
            Stat statRoot = zk.exists(Constants.ZK_REGISTRY_PATH,false);
            if(statRoot==null){
                zk.create(Constants.ZK_REGISTRY_PATH,new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                logger.debug("创建根节点成功");
            }
            Stat statService = zk.exists(Constants.ZK_SERVICE_PATH,false);
            if(statService==null){
                zk.create(Constants.ZK_SERVICE_PATH,new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                logger.debug("创建服务节点成功");
            }
        } catch (InterruptedException|KeeperException e) {
            logger.error("创建根节点错误",e);
        }
    }

}
