package com.local.hupper.kunkka.hdfs.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Created by lvhongpeng on 2017/9/7.
 */
public class RunHDFSClient implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(RunHDFSClient.class);


    public static void main(String[] args) {
        boolean success = HDFSClientCmd.getInstance().start(args);
        if (!success) {
            System.exit(-1);
        }
    }
}
