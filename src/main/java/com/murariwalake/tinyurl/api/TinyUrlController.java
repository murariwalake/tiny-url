package com.murariwalake.tinyurl.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.murariwalake.tinyurl.model.TinyUrlEntity;
import com.murariwalake.tinyurl.service.TinyUrlService;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TinyUrlController {
	private final TinyUrlService tinyUrlService;
	private final ZooKeeper zooKeeper;
	private final Environment environment;

	public TinyUrlController(final TinyUrlService tinyUrlService, final ZooKeeper zooKeeper, final Environment environment) {
		this.tinyUrlService = tinyUrlService;
		this.zooKeeper = zooKeeper;
		this.environment = environment;
	}

	@GetMapping("/ping")
	public String ping() {
		log.info("Ping received!");
		return "Online";
	}

	@PostMapping
	@Cachable(value = "tinyUrl", key = "#longUrl.get('longUrl')")
	public ResponseEntity<Map> getTinyUrl(@RequestBody Map longUrl) {
		String host = environment.getProperty("server.host", "localhost");
		String port = environment.getProperty("server.port", "8080");
		String tinyUrl1 = String.format("http://%s:%s/%s", host, port, tinyUrlService.createTinyUrl(longUrl.get("longUrl").toString()));

		return ResponseEntity.ok(Map.of("tinyUrl", tinyUrl1));
	}

	@GetMapping("/{tinyUrl}")
	@Cacheable(value = "longUrl", key = "#tinyUrl")
	public ResponseEntity getLongUrl(@PathVariable String tinyUrl) {
		String longUrl = tinyUrlService.getLongUrl(tinyUrl);
		return ResponseEntity.status(302).header("Location", longUrl).build();
	}

	@GetMapping
	public ResponseEntity<List<TinyUrlEntity>> getAllTinyUrls() {
		return ResponseEntity.ok(tinyUrlService.getAllTinyUrls());
	}

	@DeleteMapping("/{tinyUrl}")
	@CacheEvict(cacheNames = {"tinyUrl"}, key = "#tinyUrl", beforeInvocation = true)
	public ResponseEntity deleteTinyUrl(@PathVariable String tinyUrl) {
		tinyUrlService.deleteTinyUrl(tinyUrl);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

// get stats of the zknode counter
	@GetMapping("/zookeeper/{zknode}/stats")
	public ResponseEntity<Map> getZKNodeStats(@PathVariable String zknode) {
		try {
			Map<String, Object> stats = new HashMap<>();
			stats.put("stat", zooKeeper.exists("/" + zknode, false));
			return ResponseEntity.ok(stats);
		} catch (KeeperException | InterruptedException e) {
			log.error("Error while fetching stats of ZooKeeper node. Error: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

//	get data of the zknode counter
	@GetMapping("/zookeeper/{zknode}/data")
	public ResponseEntity<Map> getZKNodeData(@PathVariable String zknode) {
		try {
			Map<String, Object> data = new HashMap<>();
			data.put("data", new String(zooKeeper.getData("/" + zknode, false, null)));
			return ResponseEntity.ok(data);
		} catch (KeeperException | InterruptedException e) {
			log.error("Error while fetching data of ZooKeeper node. Error: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
