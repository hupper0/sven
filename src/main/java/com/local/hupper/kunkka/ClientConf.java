package com.local.hupper.kunkka;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;

/**
 * Created by lvhongpeng on 2017/9/4.
 */
public class ClientConf {


    static {
        ConfigCenter.init();
    }

    private static Config LIVY = ConfigCenter.getConfig("livy");
    public static String LIVY_URL = LIVY.getString("livy.url");
    public static String LIVY_PORT = LIVY.getString("livy.port");

    public static Config hadoop = ConfigCenter.getConfig("hadoop");
    public static String HADOOP_USER_NAME = hadoop.getString("HADOOP_USER_NAME");

    public static String HDFS_URI = hadoop.getString("uri");

    public static String APPLICATION_HDFS_DIR = hadoop.getString("application.hdfs.dir") + System.currentTimeMillis() + "/";


    private static class ConfigCenter {
        private static Config config;

        private static void init() {
            config = ConfigFactory.load();
            String confFile = System.getProperty("sven.path");
            config = config.withFallback(ConfigFactory.parseFile(new File(confFile))).resolve();
        }

        private static Config getConfig(String path) {
            return config.getConfig(path);
        }
    }
}
