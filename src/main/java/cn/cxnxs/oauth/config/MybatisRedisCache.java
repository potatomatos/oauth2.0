package cn.cxnxs.oauth.config;

/**
 * <p></p>
 *
 * @author mengjinyuan
 * @date 2022-05-08 21:26
 **/

import cn.cxnxs.oauth.utils.ApplicationContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.Cache;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author shuangyueliao
 * @create 2019/9/10 14:02
 * @Version 0.1
 */
@Slf4j
public class MybatisRedisCache implements Cache {


    /**
     * 读写锁
     */
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);

    /**
     * 这里使用了redis缓存，使用springboot自动注入
     */
    private RedisTemplate<String, Object> redisTemplate;

    private final String id;

    public MybatisRedisCache(final String id) {
        if (id == null) {
            throw new IllegalArgumentException("Cache instances require an ID");
        }
        this.id = id;
    }

    public RedisTemplate<String, Object> getRedisTemplate() {
        redisTemplate = (RedisTemplate<String, Object>) ApplicationContextUtils.getApplicationContext().getBean("redisTemplate");
        return redisTemplate;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void putObject(Object key, Object value) {
        redisTemplate = getRedisTemplate();
        if (value != null) {
            redisTemplate.opsForHash().put(id, key.toString(), value);
        }
    }

    @Override
    public Object getObject(Object key) {
        redisTemplate = getRedisTemplate();
        try {
            if (key != null) {
                return redisTemplate.opsForHash().get(id, key.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("缓存出错 ");
        }
        return null;
    }

    @Override
    public Object removeObject(Object key) {
        redisTemplate = getRedisTemplate();
        if (key != null) {
            redisTemplate.delete(key.toString());
        }
        return null;
    }

    @Override
    public void clear() {
        log.info("清空缓存");
        redisTemplate = getRedisTemplate();
        redisTemplate.delete(id);
    }

    @Override
    public int getSize() {
        redisTemplate = getRedisTemplate();
        Long size = redisTemplate.opsForHash().size(id);
        return size.intValue();
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return this.readWriteLock;
    }
}
