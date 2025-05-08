package com.example.demo.v2;

import redis.clients.jedis.Jedis;

public class RedisLock {
    private Jedis jedis;
    private String lockKey;

    public RedisLock(Jedis jedis, String lockKey) {
        this.jedis = jedis;
        this.lockKey = lockKey;
    }

    public boolean tryLock(long timestamp) {
        String lockValue = String.valueOf(timestamp);

        // 1. Try to acquire the lock if it doesn't exist
        Long setResult = jedis.setnx(lockKey, lockValue);
        if (setResult == 1) {
            return true; // Acquired lock
        }

        // 2. Get current lock's timestamp
        String currentValueStr = jedis.get(lockKey);
        if (currentValueStr != null && Long.parseLong(currentValueStr) < System.currentTimeMillis()) {
            // 3. Lock has expired, try to "steal" it
            String oldValueStr = jedis.getSet(lockKey, lockValue);
            return oldValueStr != null && oldValueStr.equals(currentValueStr);
        }

        return false; // Lock still held by someone else
    }
}
