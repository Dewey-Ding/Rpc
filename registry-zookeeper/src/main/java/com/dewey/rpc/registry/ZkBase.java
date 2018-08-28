package com.dewey.rpc.registry;

import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 *
 * @author deweyding
 */
public class ZkBase implements Watcher{

    private static final Logger logger = Logger.getLogger(ZkBase.class);

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    /**
     * 获取zookeeper连接
     * @return
     */
    public ZooKeeper connectZkServer(String zkAddress){
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(zkAddress, Constants.SEESSION_TIMEOUT, new ZkBase());
            countDownLatch.await();
        }catch (IOException|InterruptedException e){
            logger.error("连接zookeeper服务器失败",e);
        }
        return zk;
    }

    @Override
    public void process(WatchedEvent event) {
        if(event.getState()== Watcher.Event.KeeperState.SyncConnected){
            countDownLatch.countDown();
            logger.info("连接到zookeeper服务器");
        }
    }

    /**
     * 关闭zookeeper连接
     * @param zk
     */
    public void disConnect(ZooKeeper zk){
        if(zk!=null){
            try {
                zk.close();
            }catch (InterruptedException e) {
                logger.error("关闭连接失败",e);
            }
        }
    }

    //TODO remove
    public static void main(String[] args) {
        ZkBase zkBase = new ZkBase();
        System.out.println(zkBase.connectZkServer("54.251.182.155:2181").getState());
    }
}
