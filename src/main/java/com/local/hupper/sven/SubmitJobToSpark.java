package com.local.hupper.sven;

import org.apache.spark.deploy.SparkSubmit;

/**
 * @author lhp@local.com
 * @date 2018/6/8 上午10:29
 */
public class SubmitJobToSpark {

    public static void main(String args[]) throws Exception {
        System.setProperty("HADOOP_USER_NAME", "hadoopuser");
        printArr(args);
        SparkSubmit.main(args);
    }

    private static void printArr(String[] args){
        for (String key : args){
            System.out.print(key+ ",");
        }
    }

}
