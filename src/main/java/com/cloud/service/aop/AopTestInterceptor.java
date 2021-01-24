package com.cloud.service.aop;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author: LiuHeYong
 * @create: 2019-06-20
 * @description: SpringAOP
 **/
@Aspect
@Component
public class AopTestInterceptor {

    public static final Logger logger = LoggerFactory.getLogger(AopTestInterceptor.class);

    @Before("@annotation(com.cloud.service.aop.AopTest)")
    public void interceptor(JoinPoint jp) {
        //logger.info("方法签名：" + jp.getSignature());
        //Signature signature = jp.getSignature();
        //Class<Signature> clazz = (Class<Signature>) signature.getClass();
        //logger.info(clazz.getSimpleName());
        //logger.info(clazz.getName());
        //logger.info(String.valueOf(clazz.getDeclaredFields()));
        //logger.info(String.valueOf(clazz.getAnnotations()));
        logger.info("==================进入到前置aop拦截==================");
    }
}
