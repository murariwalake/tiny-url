package com.murariwalake.tinyurl.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.murariwalake.tinyurl.model.TinyUrlEntity;
import com.murariwalake.tinyurl.service.TinyUrlService;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/tinyUrl")
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
    public ResponseEntity<Map> createTinyUrl(@RequestBody Map<String, String> longUrlRequestBody) {
        String longUrl = longUrlRequestBody.get("longUrl");
        String host = environment.getProperty("server.host", "localhost");
        String port = environment.getProperty("server.port", "8080");
        String tinyUrlKey = tinyUrlService.createTinyUrl(longUrl);
        String tinyUrl = String.format("http://%s:%s/%s", host, port, tinyUrlKey);

        return ResponseEntity.ok(Map.of("tinyUrl", tinyUrl));
    }

    @GetMapping
    public ResponseEntity<List<TinyUrlEntity>> getAllTinyUrls() {
        return ResponseEntity.ok(tinyUrlService.getAllTinyUrls());
    }

    @DeleteMapping("/{tinyUrl}")
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
