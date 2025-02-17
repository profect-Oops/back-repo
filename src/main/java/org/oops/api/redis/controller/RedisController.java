package org.oops.api.redis.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * 레디스 서버 테스트용
 */
@RestController
@RequestMapping("/api/redis")
public class RedisController {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @PostMapping("/set/{key}/{value}")
    public ResponseEntity<String> setKeyValue(@PathVariable String key, @PathVariable String value) {
        redisTemplate.opsForValue().set(key, value);
        return ResponseEntity.ok("Key-Value set successfully");
    }

    @GetMapping("/get/{key}")
    public ResponseEntity<String> getValue(@PathVariable String key) {
        String value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            return ResponseEntity.ok(value);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Key not found");
        }
    }
}
