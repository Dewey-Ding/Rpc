package com.dewey.rpc.registry;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class main {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                ZkRegistry zkRegistry = new ZkRegistry("54.251.182.155:2181");
                zkRegistry.register("test", "127.0.0.1:10086");
            }
        });
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ZkRegistry zkRegistry = new ZkRegistry("54.251.182.155:2181");
                zkRegistry.register("test", "127.0.0.1:10087");
            }
        });
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ZkDiscovery zkDiscovery = new ZkDiscovery("54.251.182.155:2181");
                System.out.println(zkDiscovery.discovery("test") + "test---------------");
            }
        });
    }
}
