package com.alibaba.rocketmq.storm.redis;

import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;

/**
 * Created by robert on 2015/5/20.
 */
public class CacheManager {

    private Jedis jedis = RedisPoolManager.createInstance();

    private static CacheManager cacheManager = new CacheManager();

    private CacheManager() {
    }

    public static CacheManager getInstance(){
        return cacheManager;
    }

    public void set(Map<String, String> entries) {
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            jedis.set(entry.getKey(), entry.getValue());
        }
    }

    public void set(String key, String value) {
        jedis.set(key, value);
    }

    /**
     * Set the value to the key and specify the key's life cycle as seconds.
     * @param key
     * @param live
     * @param value
     */
    public void setKeyLive(String key, int live, String value) {
        jedis.setex(key, live, value);
    }

    /**
     * Append the value to an existing key
     * @param key
     * @param value
     */
    public void append(String key, String value) {
        jedis.append(key, value);
    }

    public String getValue(String key) {
        return jedis.get(key);
    }

    public List<String> getValues(String... keys) {
        return jedis.mget(keys);
    }

    public Long deleteValue(String key) {
        return jedis.del(key);
    }

    public Long deleteValues(String... keys) {
        return jedis.del(keys);
    }

    public void returnSource() {
        RedisPoolManager.returnResource(jedis);
    }

    public long calculateSize() {
        return jedis.dbSize();
    }

}
