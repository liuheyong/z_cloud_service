package com.cloud.service;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@EnableDubbo
@SpringBootApplication
public class ZCloudServiceApplication implements CommandLineRunner {

    public static final Logger logger = LoggerFactory.getLogger(ZCloudServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(ZCloudServiceApplication.class);
        //禁止命令行设置环境参数
        springApplication.setAddCommandLineProperties(false);
        ApplicationContext context = springApplication.run(args);
        //赋值ApplicationContext,以便随时手动获取bean
        SpringBeanUtil.setApplicationContext(context);
        logger.info("==========获取到ApplicationContext==========" + SpringBeanUtil.getApplicationContext());
    }

    /**
     * @date: 2019/5/28
     * @param: [strings]
     * @return: void
     * @description: 这个方法不需要手动调用，启动以后这个方法会被自动执行并存在于Spring容器中
     */
    @Override
    public void run(String... strings) {
        try {
            logger.info("==========随cloud启动而执行==========");
        } catch (Exception e) {
            logger.error("启动异常", e);
            e.printStackTrace();
        }
    }

}
