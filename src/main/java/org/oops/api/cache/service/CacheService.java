package org.oops.api.cache.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    @Cacheable(value = "numbers", key = "#id")
    public String getCachedData(String id) {
        System.out.println("Fetching data from the database...");
        return "Data for ID: " + id;
    }
}