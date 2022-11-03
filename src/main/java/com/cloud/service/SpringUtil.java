package com.cloud.service;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SpringUtil {

    private static ApplicationContext applicationContext;

    /**
     * 取得存储在静态变量中的ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 实现ApplicationContextAware接口的context注入函数, 将其存入静态变量
     */
    public static void setApplicationContext(ApplicationContext atx) throws BeansException {
        applicationContext = atx;
    }

    /**
     * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) applicationContext.getBean(name);
    }

    /**
     * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> clazz) {
        return (T) applicationContext.getBeansOfType(clazz);
    }

    /**
     * 清除applicationContext静态变量.
     */
    public static void cleanApplicationContext() {
        applicationContext = null;
    }
}
