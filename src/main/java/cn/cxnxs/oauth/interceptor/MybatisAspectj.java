package cn.cxnxs.oauth.interceptor;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * <p>mybatisPlus拦截，为了解决selectOne结果多条报错问题</p>
 *
 * @author mengjinyuan
 * @date 2022-04-09 23:18
 **/
@Aspect
@Component
public class MybatisAspectj {

    /**
     * 配置织入点
     */
    @Pointcut("execution(public * com.baomidou.mybatisplus.core.mapper.BaseMapper.selectOne(..))")
    public void selectOneAspect() {
    }

    @Before("selectOneAspect()")
    public void beforeSelect(JoinPoint point) {
        Object arg = point.getArgs()[0];
        if (arg instanceof AbstractWrapper) {
            ((AbstractWrapper) arg).last("limit 1");
        }
    }
}
