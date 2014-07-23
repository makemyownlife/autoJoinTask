package com.diyicai.server;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created with IntelliJ IDEA.
 * User: zhangyong
 * Date: 13-12-30
 * Time: 下午4:42
 * To change this template use File | Settings | File Templates.
 */
public class StartUp {

    private final static Logger logger = Logger.getLogger(StartUp.class);

    public static void main(String[] args) {
        logger.info("开始加载spring配置文件");
        try {
            ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
                    new String[]{
                            "spring/spring-config-*.xml",
                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("成功加载spring配置文件,继续任务之旅吧");
    }

}
