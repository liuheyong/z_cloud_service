package com.cloud.service;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.RestController;

@EnableDubbo
@RestController
@SpringBootApplication
public class ZCloudServiceApplication implements CommandLineRunner {

    public static final Logger logger = LoggerFactory.getLogger(ZCloudServiceApplication.class);

    //@NacosInjected
    //private NamingService namingService;

//    @Value("${server.port}")
//    private int serverPort;

//    @Value("${spring.application.name}")
//    private String applicationName;

    public static void main(String[] args) throws InterruptedException {
        SpringApplication springApplication = new SpringApplication(ZCloudServiceApplication.class);
        //禁止命令行设置环境参数
        springApplication.setAddCommandLineProperties(false);
        ApplicationContext context = springApplication.run(args);
        //赋值ApplicationContext,以便随时手动获取bean
        SpringUtil.setApplicationContext(context);
        logger.info("==========获取到ApplicationContext==========" + SpringUtil.getApplicationContext());
        //keepRunning();
    }

    /*@PostConstruct
    public void registerInstance() throws NacosException {
        namingService.registerInstance(applicationName, "127.0.0.1", serverPort);
    }

    @RequestMapping(value = Constants.CLOUD + "/getInstance", method = GET)
    @ResponseBody
    public List<Instance> getInstance(@RequestParam String serviceName) throws NacosException {
        return namingService.getAllInstances(serviceName);
    }*/

    /**
     * @date: 2019/6/18
     * @description: 阻塞main方法
     */
    private static void keepRunning() throws InterruptedException {
        logger.info("==========keepRunning==========");
        Thread currentThread = Thread.currentThread();
        synchronized (currentThread) {
            currentThread.wait();
        }
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
