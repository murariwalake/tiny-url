package com.murariwalake.tinyurl.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CounterServiceImpl implements CounterService {
	private static final String COUNTER_PATH = "/counter";
	private static final Long COUNTER_RANGE = 100000L;
	private static final int MAX_RETRY = 3;

	private Long counter;
	private final ZooKeeper zooKeeper;

	public CounterServiceImpl(final ZooKeeper zooKeeper) {
		this.zooKeeper = zooKeeper;
		this.counter = fetchCounterFromZookeeper();
		log.info("Counter initialized with value: {}", counter);
	}

	private Long fetchCounterFromZookeeper() {

//		Create "/counter" zknode if not exist
		createZKNodeIfNotExist();

		int currentRetry = 0;
		while (currentRetry < MAX_RETRY) {
			try {
				log.info("Fetching counter from ZooKeeper. Retry: {}/{}", currentRetry + 1, MAX_RETRY);
				Stat stat = new Stat();
				byte[] data = zooKeeper.getData(COUNTER_PATH, false, stat);
				Long nextRange = Long.parseLong(new String(data));
				Long nextNextRange = nextRange + COUNTER_RANGE;
				zooKeeper.setData(COUNTER_PATH, nextNextRange.toString().getBytes(), stat.getVersion());
				log.info("Counter fetched from ZooKeeper and updated. Current counter range is {}, Updated counter range to zookeeper is {}", nextRange, nextNextRange);
				return nextRange;
			} catch (KeeperException | InterruptedException e) {
				log.error("Error while fetching counter from ZooKeeper. Retry: {}/{}. Error: {}", currentRetry + 1, MAX_RETRY, e.getMessage());
				currentRetry++;
			}
		}
		throw new RuntimeException("Failed to fetch counter from ZooKeeper after " + MAX_RETRY + " retries.");
	}

	private void createZKNodeIfNotExist() {
		try {
			if (zooKeeper.exists(COUNTER_PATH, false) == null) {
				zooKeeper.create(COUNTER_PATH, "100001".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				log.info("Counter node created in ZooKeeper with initial value 100001");
			}
		} catch (KeeperException | InterruptedException e) {
			log.info("Error while creating counter node in ZooKeeper. Error: {}", e.getMessage());

			throw new RuntimeException("Error while creating counter node in ZooKeeper. Error: " + e.getMessage());
		}
	}

	@Override
	//Synchronized method to avoid concurrent modification of counter
	public synchronized Long getNextCount() {
		if (counter % 100000 == 0) {
			//Fetch new counter range if existing counter range is exhausted
			counter = fetchCounterFromZookeeper();
		}
		return counter++;
	}
}
