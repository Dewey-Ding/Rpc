package com.dewey.rpc.registry;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * 服务注册
 * @author deweyding
 */
public class ZkRegistry implements registry{

    private static final Logger logger = Logger.getLogger(ZkRegistry.class);

    private String zkAddress;

    private ZkBase zkBase;

    public ZkRegistry(String zkAddress){
        this.zkAddress = zkAddress;
        zkBase = new ZkBase();
    }

    /**
     * 服务注册
     * @param path 注册路径
     * @param data 注册数据
     */
    @Override
    public void register(String path,String data){
        if(path!=null&&data!=null){
            ZooKeeper zk = zkBase.connectZkServer(zkAddress);
            if(zk != null){
                createNode(zk,path,data);
            }
        }
    }

    /**
     * 创建zk节点
     * @param zk
     * @param path 节点路径
     * @param data 节点数据
     */
    public void createNode(ZooKeeper zk,String path,String data){
        if(zk==null||path==null||data==null){
            logger.error(String.format("信息不全,无法创建节点(0代表为null)：zk = %d,path = %d,data = %d",zk==null?0:1,path==null?0:1,data==null?0:1));
            return ;
        }
        createRegistryNode(zk);
        try{
            String servicePath = Constants.ZK_DATA_PATH+"/"+path;
            String pathReturn = zk.create(servicePath,data.getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
            logger.info(String.format("create zookeeper node path = %s,data = %s",pathReturn,data));
        } catch (InterruptedException|KeeperException e) {
            logger.error("创建节点错误",e);
        }finally {
            zkBase.disConnect(zk);
        }
    }

    /**
     * 如果根节点不存在则创建
     * @param zk
     */
    public void createRegistryNode(ZooKeeper zk){
        try {
            Stat stat = zk.exists(Constants.ZK_REGISTRY_PATH,false);
            if(stat==null){
                zk.create(Constants.ZK_DATA_PATH,new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (InterruptedException|KeeperException e) {
            logger.error("创建根节点错误",e);
        }
    }

}
