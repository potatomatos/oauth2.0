package cn.cxnxs.oauth.utils;

import org.redisson.api.RBatch;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author 邓健
 * @version 1.0
 * @date 2021/2/7 17:10
 */
@Component
@SuppressWarnings("all")
public class RedisUtils {

    @Resource
    private RedissonClient redissonClient;

    @Value("${cache.prefix}")
    private String cachePrefix;

    public String getCachePrefix(String key) {
        return cachePrefix + ":" + key;
    }

    //- - - - - - - - - - - - - - - - - - - - -  公共方法 - - - - - - - - - - - - - - - - - - - -

    /**
     * 给一个指定的 key 值附加过期时间
     *
     * @param key
     * @param time
     * @return
     */
    public boolean expire(String key, long time) {
        return redissonClient.getBucket(getCachePrefix(key)).expire(time, TimeUnit.SECONDS);
    }
    /**
     * 给一个指定的 key 值附加过期时间
     *
     * @param key
     * @param date
     * @return
     */
    public boolean expireAt(String key, Date date) {
        return redissonClient.getBucket(getCachePrefix(key)).expireAt(date);
    }

    /**
     * 是否存在key
     *
     * @param key
     * @return
     */
    public boolean hasKey(String key) {
        return redissonClient.getBucket(getCachePrefix(key)).isExists();
    }

    /**
     * 移除指定key 的过期时间
     *
     * @param key
     * @return
     */
    public boolean persist(String key) {
        return redissonClient.getBucket(getCachePrefix(key)).clearExpire();
    }

    /**
     * 删除指定的 key
     *
     * @param key
     * @return 删除成功的 数量
     */
    public Boolean del(String key) {
        return redissonClient.getBucket(getCachePrefix(key)).delete();
    }

    /**
     * 批量删除指定的 key
     *
     * @param key
     * @return 删除成功的 数量
     */
    public void batchdeleted(List<String> keys) {
        RBatch rBatch = redissonClient.createBatch();
        keys.forEach(key -> {
            rBatch.getBucket(getCachePrefix(key)).deleteAsync();
        });
        rBatch.execute();
    }

    //- - - - - - - - - - - - - - - - - - - - -  String类型 - - - - - - - - - - - - - - - - - - - -

    /**
     * 根据key获取值
     *
     * @param key 键
     * @return 值
     */
    public <T> T get(String key) {
        return key == null ? null : (T)redissonClient.getBucket(getCachePrefix(key)).get();
    }

    /**
     * 将值放入缓存
     *
     * @param key   键
     * @param value 值
     * @return true成功 false 失败
     */
    public <T> void set(String key, T value) {
        redissonClient.getBucket(getCachePrefix(key)).set(value);
    }

    /**
     * 设置value 并设置有效期
     * @param key
     * @param val
     * @param seconds
     * @return
     */
    public Boolean setIfAbsent(String key, String val, int seconds) {
        return redissonClient.getBucket(getCachePrefix(key)).trySet(val, seconds, TimeUnit.SECONDS);
    }

    /**
     * 将值放入缓存并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) -1为无期限
     * @return true成功 false 失败
     */
    public <T> void set(String key, T value, long time) {
        if (time > 0) {
            redissonClient.getBucket(getCachePrefix(key)).set(value, time, TimeUnit.SECONDS);
        } else {
            redissonClient.getBucket(getCachePrefix(key)).set(value);
        }
    }

    /**
     * 批量添加 key (重复的键会覆盖)
     *
     * @param keyAndValue
     */
    public void batchSet(Map<String, Object> keyAndValue) {
        RBatch rBatch = redissonClient.createBatch();
        keyAndValue.forEach((key, value) -> {
            rBatch.getBucket(getCachePrefix(key)).setAsync(value);
        });
        rBatch.execute();
    }

    /**
     * 批量查询 key
     *
     * @param keys
     */
    public <T,V> List<T> batchGet(List<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return null;
        }
        RBatch rBatch = redissonClient.createBatch();
        for (String key : keys) {
            rBatch.getBucket(getCachePrefix(key)).getAsync();
        }
        return (List<T>) rBatch.execute().getResponses();
    }

    /**
     * 批量添加 key-value 只有在键不存在时,才添加
     *
     * @param keyAndValue
     */
    public void batchSetIfAbsent(Map<String, String> keyAndValue) {
        if (!CollectionUtils.isEmpty(keyAndValue)) {
            Map<String, String> anotherMap = new HashMap<>();
            keyAndValue.forEach((key, object) -> {
                anotherMap.put(getCachePrefix(key), object);
            });
            redissonClient.getBuckets().trySet(anotherMap);
        }
    }

    /**
     * 对一个 key-value 的值进行加减操作,
     * 如果该 key 不存在 将创建一个key 并赋值该 number
     * 如果 key 存在,但 value 不是长整型 ,将报错
     *
     * @param key
     * @param number
     */
    public Long increment(String key, long number) {
        return redissonClient.getAtomicLong(getCachePrefix(key)).addAndGet(number);
    }

    /**
     * 对一个 key-value 的值进行加减操作,
     * 如果该 key 不存在 将创建一个key 并赋值该 number
     * 如果 key 存在,但 value 不是 纯数字 ,将报错
     *
     * @param key
     * @param number
     */
    public Double increment(String key, double number) {
        return redissonClient.getAtomicDouble(getCachePrefix(key)).addAndGet(number);
    }

    //- - - - - - - - - - - - - - - - - - - - -  set类型 - - - - - - - - - - - - - - - - - - - -

    /**
     * 将数据放入set缓存
     *
     * @param key 键
     * @return
     */
    public void sSet(String key, String value) {
        redissonClient.getSet(getCachePrefix(key)).add(value);
    }

    /**
     * 获取变量中的值
     *
     * @param key 键
     * @return
     */
    public Set<Object> members(String key) {
        return redissonClient.getSet(getCachePrefix(key)).readAll();
    }

    /**
     * 随机获取变量中指定个数的元素
     *
     * @param key   键
     * @param count 值
     * @return
     */
    public Set<Object> randomMembers(String key, int count) {
        return redissonClient.getSet(getCachePrefix(key)).random(count);
    }

    /**
     * 随机获取变量中的元素
     *
     * @param key 键
     * @return
     */
    public Object randomMember(String key) {
        return redissonClient.getSet(getCachePrefix(key)).random();
    }

    /**
     * 弹出变量中的元素
     *
     * @param key 键
     * @return
     */
    public Object pop(String key) {
        return redissonClient.getSet(getCachePrefix(key)).removeRandom();
    }

    /**
     * 获取变量中值的长度
     *
     * @param key 键
     * @return
     */
    public long size(String key) {
        return redissonClient.getSet(getCachePrefix(key)).size();
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasValue(String key, Object value) {
        return redissonClient.getSet(getCachePrefix(key)).contains(value);
    }

    /**
     * 转移变量的元素值到目的变量。
     *
     * @param key     键
     * @param value   元素对象
     * @param destKey 元素对象
     * @return
     */
    public boolean move(String key, String value, String destKey) {
        return redissonClient.getSet(getCachePrefix(key)).move(value, destKey);
    }

    /**
     * 批量移除set缓存中元素
     *
     * @param key    键
     * @param values 值
     * @return
     */
    public void remove(String key, List<Object> values) {
        redissonClient.getSet(getCachePrefix(key)).removeAll(values);
    }

    /**
     * 通过给定的key求2个set变量的差值
     *
     * @param key     键
     * @param destKey 键
     * @return
     */
    public <T> Set<T> difference(String key, String destKey) {
        return (Set<T>) redissonClient.getSet(getCachePrefix(key)).readDiff(destKey);
    }

    /**
     * 给定集合
     * @param key
     * @param destKey
     * @param <T>
     * @param <V>
     * @return
     */
    public <T,V> Set<T> difference(String key, String ... destKey) {
        return (Set<T>) redissonClient.getSet(getCachePrefix(key)).readDiff(destKey);
    }

    /**
     * redis set 交集
     */
    public <T> Set<T> intersect(String s1,String s2) {
        return (Set<T>) redissonClient.getSet(getCachePrefix(s1)).readIntersection(getCachePrefix(s2));
    }

    /**
     * redis set 和集
     */
    public <T> Set<T> union(String s1,String s2) {
        return (Set<T>) redissonClient.getSet(getCachePrefix(s1)).readUnion(getCachePrefix(s2));
    }


    //- - - - - - - - - - - - - - - - - - - - -  hash类型 - - - - - - - - - - - - - - - - - - - -

    /**
     * 加入缓存
     *
     * @param key     键
     * @param hashKey 键
     * @param value   值
     * @return
     */
    public void hset(String key, String hashKey, Object value) {
        redissonClient.getMap(getCachePrefix(key)).put(hashKey, value);
    }

    /**
     * 批量加入缓存
     *
     * @param key 键
     * @param map 键
     * @return
     */
    public void add(String key, Map<String, Object> map) {
        redissonClient.getMap(getCachePrefix(key)).putAll(map);
    }

    /**
     * 批量查询 key
     *
     * @param keys
     */
    public <T,V> List<T> batchHget(String key, List<String> hashKeys) {
        if (CollectionUtils.isEmpty(hashKeys)) {
            return null;
        }
        RBatch rBatch = redissonClient.createBatch();
        for (String hashKey : hashKeys) {
            rBatch.getMap(getCachePrefix(key)).getAsync(hashKey);
        }
        return (List<T>) rBatch.execute().getResponses();
    }

    /**
     * 获取 key 下的 所有  hashkey 和 value
     *
     * @param key 键
     * @return
     */
    public <K,V>  Map<K,V> getHashEntries(String key) {
        return (Map<K,V>) redissonClient.getMap(getCachePrefix(key)).readAllMap();
    }

    /**
     * 验证指定 key 下 有没有指定的 hashkey
     *
     * @param key
     * @param hashKey
     * @return
     */
    public boolean hashKey(String key, String hashKey) {
        return redissonClient.getMap(getCachePrefix(key)).containsKey(hashKey);
    }

    /**
     * 获取指定key的值string
     *
     * @param key     键
     * @param hashKey 键
     * @return
     */
    public <T> T hget(String key, String hashKey) {
        return (T)redissonClient.getMap(getCachePrefix(key)).get(hashKey);
    }

    /**
     * 获取指定的值Int
     *
     * @param key  键
     * @param key2 键
     * @return
     */
    public Integer getMapInt(String key, String key2) {
        return (Integer) redissonClient.getMap(getCachePrefix(key)).get(key2);
    }

    /**
     * 删除指定 hash 的 HashKey
     *
     * @param key
     * @param hashKeys
     * @return 删除成功的 数量
     */
    public Long delete(String key, String... hashKeys) {
        return redissonClient.getMap(getCachePrefix(key)).fastRemove(hashKeys);
    }

    /**
     * 给指定 hash 的 hashkey 做增减操作
     *
     * @param key
     * @param hashKey
     * @param number
     * @return
     */
    public Long increment(String key, String hashKey, long number) {
        return (Long) redissonClient.getMap(getCachePrefix(key)).addAndGet(hashKey, number);
    }

    /**
     * 给指定 hash 的 hashkey 做增减操作
     *
     * @param key
     * @param hashKey
     * @param number
     * @return
     */
    public Double increment(String key, String hashKey, Double number) {
        return (Double) redissonClient.getMap(getCachePrefix(key)).addAndGet(hashKey, number);
    }

    /**
     * 获取 key 下的 所有 hashkey 字段
     *
     * @param key
     * @return
     */
    public Set<Object> hashKeys(String key) {
        return redissonClient.getMap(getCachePrefix(key)).keySet();
    }

    /**
     * 获取指定 hash 下面的 键值对 数量
     *
     * @param key
     * @return
     */
    public int hashSize(String key) {
        return redissonClient.getMap(getCachePrefix(key)).size();
    }

    //- - - - - - - - - - - - - - - - - - - - -  list类型 - - - - - - - - - - - - - - - - - - - -

    /**
     * 在变量左边添加元素值
     *
     * @param key
     * @param value
     * @return
     */
    public void leftPush(String key, Object value) {
        redissonClient.getDeque(getCachePrefix(key)).addFirst(value);
    }

    /**
     * 获取集合指定位置的值。
     *
     * @param key
     * @param index
     * @return
     */
    public Object index(String key, int index) {
        return redissonClient.getList(getCachePrefix(key)).get(index);
    }

    /**
     * 获取指定区间的值。
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<Object> range(String key, int start, int end) {
        return redissonClient.getList(getCachePrefix(key)).range(start, end);
    }

    /**
     * 把最后一个参数值放到指定集合的第一个出现中间参数的前面，
     * 如果中间参数值存在的话。
     *
     * @param key
     * @param pivot
     * @param value
     * @return
     */
    public void leftPush(String key, String pivot, String value) {
        redissonClient.getList(getCachePrefix(key)).addBefore(pivot, value);
    }

    /**
     * 向左边批量添加参数元素。
     *
     * @param key
     * @param values
     * @return
     */
    public void leftPushAll(String key, String... values) {
        redissonClient.getDeque(getCachePrefix(key)).addFirstIfExists(values);
    }

    /**
     * 向集合最右边添加元素。
     *
     * @param key
     * @param value
     * @return
     */
    public void rightPush(String key, String value) {
        redissonClient.getList(getCachePrefix(key)).add(value);
    }

    /**
     * 向右边批量添加参数元素。
     *
     * @param key
     * @param values
     * @return
     */
    public void rightPushAll(String key, List<String> values) {
        redissonClient.getList(getCachePrefix(key)).addAll(values);
    }

    /**
     * 以集合方式向右边添加元素。
     *
     * @param key
     * @param values
     * @return
     */
    public <V> void rightPushAll(String key, Collection<V> values) {
        redissonClient.getList(getCachePrefix(key)).addAll(values);
    }

    /**
     * 向已存在的集合中添加元素。
     *
     * @param key
     * @param value
     * @return
     */
    public void rightPushIfPresent(String key, Object value) {
        redissonClient.getDeque(getCachePrefix(key)).addLastIfExists(value);
    }

    /**
     * 查询集合中的元素个数。
     *
     * @param key
     * @return
     */
    public long listLength(String key) {
        return redissonClient.getList(getCachePrefix(key)).size();
    }

    /**
     * 移除集合中的左边第一个元素。
     *
     * @param key
     * @return
     */
    public <T> T leftPop(String key) {
        return (T) redissonClient.getQueue(getCachePrefix(key)).poll();
    }

    /**
     * 移除集合中左边的元素在等待的时间里，如果超过等待的时间仍没有元素则退出。
     *
     * @param key
     * @return
     */
    public <T> T leftPop(String key, long timeout, TimeUnit unit) {
        try {
            return (T) redissonClient.getBlockingQueue(getCachePrefix(key)).poll(timeout, unit);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 弹出集合中右边的元素。
     *
     * @param key
     * @return
     */
    public <T> T rightPop(String key) {
        return (T) redissonClient.getDeque(getCachePrefix(key)).pollLast();
    }
}
