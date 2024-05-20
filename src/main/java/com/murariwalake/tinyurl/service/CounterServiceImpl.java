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
	private static final String ZK_NODE_PATH = "/counter";
	private static final Long RANGE_LENGTH = 100000L;
	private static final int MAX_RETRY = 3;
	private static final Long FIRST_RANGE_START_VALUE = 100000L;

	private Long counter;
	private Long rangeStartValue;
	private final ZooKeeper zooKeeper;

	public CounterServiceImpl(final ZooKeeper zooKeeper) {
		this.zooKeeper = zooKeeper;

		//Create "/counter" zknode if not exist
		createZKNodeIfNotExist();

		//Fetch rangeStartValue from ZooKeeper and initialise counter
		fetchRangeStartValueAndInitialiseCounter();
	}

	private void fetchRangeStartValueAndInitialiseCounter() {
		this.rangeStartValue = getRangeStartValue();
		log.info("Fetched range start value : {}", rangeStartValue);

		this.counter = rangeStartValue;
		log.info("Counter initialized with value : {}", counter);
	}

	/**
	 * Method to get the rangeStartValue and which also updates next range start value in zknode*
	 * @return
	 */
	private Long getRangeStartValue() {
		int currentRetry = 0;
		while (currentRetry < MAX_RETRY) {
			try {
				//Fetch rangeStartValue form ZooKeeper
				log.info("Fetching rangeStartValue from ZooKeeper. Retry: {}/{}", currentRetry + 1, MAX_RETRY);
				Stat stat = new Stat();
				byte[] data = zooKeeper.getData(ZK_NODE_PATH, false, stat);
				Long rangeStartValue = Long.parseLong(new String(data));

				//Update rangeStartValue in ZooKeeper
				Long nextRangeStartValue = rangeStartValue + RANGE_LENGTH;
				zooKeeper.setData(ZK_NODE_PATH, nextRangeStartValue.toString().getBytes(), stat.getVersion());
				log.info("rangeStartValue fetched from ZooKeeper and updated. rangeStartValue is {}, Updated nextRangeStartValue in zknode is {}",
						rangeStartValue, nextRangeStartValue);

				return rangeStartValue;
			} catch (KeeperException | InterruptedException e) {
				log.error("Error while fetching rangeStartValue from ZooKeeper. Retry: {}/{}. Error: {}", currentRetry + 1, MAX_RETRY, e.getMessage());
				currentRetry++;
			}
		}
		throw new RuntimeException("Failed to fetch rangeStartValue from ZooKeeper after " + MAX_RETRY + " retries.");
	}

	/**
	 * Method to create "/counter" zknode if not exist
	 */
	private void createZKNodeIfNotExist() {
		try {
			if (zooKeeper.exists(ZK_NODE_PATH, false) == null) {
				zooKeeper.create(ZK_NODE_PATH, FIRST_RANGE_START_VALUE.toString().getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				log.info("zknode \"/counter\" created in ZooKeeper with initial value {}", FIRST_RANGE_START_VALUE);
			}
		} catch (KeeperException | InterruptedException e) {
			log.info("Error while creating counter node in ZooKeeper. Error: {}", e.getMessage());

			throw new RuntimeException("Error while creating counter node in ZooKeeper. Error: " + e.getMessage());
		}
	}

	@Override
	//Synchronized method to avoid concurrent modification of counter
	public synchronized Long getNextCount() {
		Long currentCounterValue = counter++;

		//If current range is exhausted, fetch next range
		if (counter == rangeStartValue + RANGE_LENGTH) {
			//Fetch rangeStartValue from ZooKeeper and update next range start value in zknode
			fetchRangeStartValueAndInitialiseCounter();
		}

		return currentCounterValue;
	}
}
