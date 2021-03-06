package com.alibaba.rocketmq.storm.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * Wrapper of {@link Jedis} client.
 * </p>
 *
 * @author Xu Tao
 * @version 1.0
 * @since 1.0
 */
public class CacheManager {

    private Jedis jedis = RedisPoolManager.createInstance();

    private static CacheManager cacheManager = new CacheManager();

    private static final Logger LOG = LoggerFactory.getLogger(CacheManager.class);

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

    public boolean setKeyLive(Map<String, String> entries, int live) {
        Transaction tx = jedis.multi();
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            tx.setex(entry.getKey(), live, entry.getValue());
        }
        List<Object> result = tx.exec();
        if (null == result || result.isEmpty()) {
            LOG.error("Failed to insert " + entries);
            return false;
        }

        return true;
    }
    /**
     * Set the value to the key and specify the key's life cycle in seconds.
     *
     * @param key
     * @param live Time to live in seconds.
     * @param value
     */
    public void setKeyLive(String key, int live, String value) {
        jedis.setex(key, live, value);
    }

    public void publish(Map<String, String> entries, String channel){
        try {
            Transaction tx = jedis.multi();
            for (Map.Entry<String, String> entry : entries.entrySet()) {
                String key = entry.getKey();
                tx.publish(channel, key);
            }
            tx.exec();
        }catch (Exception e){
            LOG.error("Failed to publish.");
        }
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

    public Set<String> getKeys(String pattern){
        return jedis.keys(pattern);
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
