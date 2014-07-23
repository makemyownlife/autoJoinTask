package com.diyicai.job;


import com.diyicai.service.TlBonusTopService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 定制跟单的统计任务
 * User: zhangyong
 * Date: 13-12-30
 * Time: 下午5:58
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class TlBonusTopTask {

    private static final Logger logger = LoggerFactory.getLogger(TlBonusTopTask.class);

    @Autowired
    private TlBonusTopService tlBonusTopService;

    public void doJob() {
        long startTime = System.currentTimeMillis();
        logger.info("开始执行自动跟单的统计任务");
        try {
            tlBonusTopService.executeTlBonusTop();
        } catch (Exception e) {
            logger.error("doJob error ", e);
        }
        logger.info("结束执行 耗时{}秒", (System.currentTimeMillis() - startTime) / 1000.0);
    }

}
