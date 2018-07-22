package com.local.hupper.kunkka.spark.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Created by lvhongpeng on 2017/9/7.
 */
public class RunSparkClient implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(com.local.hupper.kunkka.spark.client.RunSparkClient.class);


    public static void main(String[] args) {
        boolean success = SparkClientCmd.getInstance().init(args);
        if (!success) {
            System.exit(-1);
        } else {
            SparkClientCmd.getInstance().start();
        }
    }
}
