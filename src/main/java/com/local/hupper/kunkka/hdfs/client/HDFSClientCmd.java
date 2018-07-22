package com.local.hupper.kunkka.hdfs.client;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lvhongpeng on 2017/9/13.
 */
public class HDFSClientCmd {


    private static final Logger logger = LoggerFactory.getLogger(HDFSClientCmd.class);

    private static HDFSClientArguments hdfsClientArguments = null;

    private static class SingletonHolder {
        static final HDFSClientCmd INSTANCE = new HDFSClientCmd();
    }

    static HDFSClientCmd getInstance() {
        return HDFSClientCmd.SingletonHolder.INSTANCE;
    }

    boolean start(String[] args) {
        try {
            hdfsClientArguments = new HDFSClientArguments(args);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("HDFSClientCmd init error", e);
            hdfsClientArguments.printHelp();
            return false;
        }

        if (hdfsClientArguments.isPrintHelp()) {
            hdfsClientArguments.printHelp();
            return false;
        }
        boolean flag = hdfsClientArguments.generateJob();
        if (!flag) {
            hdfsClientArguments.printHelp();
            return false;
        }
        return true;
    }


}
