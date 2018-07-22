package com.local.hupper.kunkka.spark.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lvhongpeng on 2017/9/7.
 */
public class ShutdownHook extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);


    @Override
    public void run() {
        try {
            SparkClientCmd.getInstance().stop();
        } catch (Exception e) {
            logger.warn("exception exist in shutdown process: {}", e);
        }
    }
}
