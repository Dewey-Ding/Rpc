package com.dewey.rpc;

import com.dewey.rpc.registry.ZkBase;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RpcApplication {

	public static void main(String[] args) {
		SpringApplication.run(RpcApplication.class, args);
		String zkAddress = "54.251.182.155:2181";

		ZkBase zkBase = new ZkBase(zkAddress);
		ZooKeeper zk = zkBase.connectZkServer();
		System.out.println(zk.getState());
	}
}
