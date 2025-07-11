package com.example.secretstash.service

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RateLimitingService {
    private val cache: Cache<String, Int> = Caffeine.newBuilder()
        .expireAfterWrite(1, TimeUnit.HOURS)
        .maximumSize(10_000)
        .build()

    fun checkRateLimit(userId: String): Boolean {
        val count = cache.getIfPresent(userId) ?: 0
        if (count >= 100) {
            return false
        }
        cache.put(userId, count + 1)
        return true
    }
}