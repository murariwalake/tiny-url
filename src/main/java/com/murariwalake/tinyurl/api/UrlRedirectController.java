
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class UrlRedirectController {
    private final TinyUrlService tinyUrlService;

    public UrlRedirectController(final TinyUrlService tinyUrlService) {
        this.tinyUrlService = tinyUrlService;
    }

    @GetMapping("/{tinyUrl}")
    public ResponseEntity getLongUrl(@PathVariable String tinyUrl) {
        String longUrl = tinyUrlService.getLongUrl(tinyUrl);
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).header("Location", longUrl).build();
    }
}
