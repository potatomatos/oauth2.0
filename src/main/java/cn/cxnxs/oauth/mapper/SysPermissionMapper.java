package cn.cxnxs.oauth.mapper;

import cn.cxnxs.oauth.config.MybatisRedisCache;
import cn.cxnxs.oauth.entity.SysPermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.CacheNamespace;

/**
 * <p>
 * 权限表 Mapper 接口
 * </p>
 *
 * @author mengjinyuan
 * @since 2022-05-01
 */

/**
 * 开启二级缓存，以下是各参数解释：
    1. eviction：缓存回收策略：• 默认的是 LRU。
        LRU – 最近最少使用的：移除最长时间不被使用的对象。
        FIFO – 先进先出：按对象进入缓存的顺序来移除它们。
        SOFT – 软引用：移除基于垃圾回收器状态和软引用规则的对象。
        WEAK – 弱引用：更积极地移除基于垃圾收集器状态和弱引用规则的对象。
        2. flushInterval：刷新间隔，单位毫秒
        默认情况是不设置，也就是没有刷新间隔，缓存仅仅调用语句时刷新
        3. size：引用数目，正整数代表缓存最多可以存储多少个对象，太大容易导致内存溢出
        4. readOnly：只读，true/false
        true：只读缓存；会给所有调用者返回缓存对象的相同实例。因此这些对象不能被修改。这提供了很重要的性能优势。
        false：读写缓存；会返回缓存对象的拷贝（通过序列化）。这会慢一些，但是安全，因此默认是 false。
        **/
@CacheNamespace(implementation= MybatisRedisCache.class,flushInterval=3600000)
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

}
