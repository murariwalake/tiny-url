package com.murariwalake.tinyurl.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class ZooKeeperConfig {

	//create zookeeper bean
	@Bean
	public ZooKeeper zooKeeper(final Environment environment) throws Exception {
		final String connectionString = environment.getProperty("zookeeper.connection.string", "localhost:2181");
		final CuratorFramework client = CuratorFrameworkFactory.newClient(connectionString, new ExponentialBackoffRetry(1000, 3));
		client.start();
		client.blockUntilConnected();
		return client.getZookeeperClient().getZooKeeper();
	}
}
