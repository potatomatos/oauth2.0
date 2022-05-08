package cn.cxnxs.oauth.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * <p></p>
 *
 * @author mengjinyuan
 * @date 2022-05-08 21:25
 **/
@Component
public class ApplicationContextUtils implements ApplicationContextAware {
    private static ApplicationContext application;

    public static ApplicationContext getApplicationContext() {
        return application;
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextUtils.application = applicationContext;
    }
}
