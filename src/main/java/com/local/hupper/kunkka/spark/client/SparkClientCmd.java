package com.local.hupper.kunkka.spark.client;

import com.local.hupper.kunkka.ClientConf;
import com.local.hupper.kunkka.hdfs.HDFSFile;
import com.local.hupper.kunkka.spark.api.LivyServerApi;
import com.local.hupper.kunkka.spark.model.Batch;
import com.local.hupper.kunkka.spark.model.PostBathRequest;
import com.local.hupper.kunkka.spark.model.SessionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lvhongpeng on 2017/9/6.
 */
public class SparkClientCmd {


    private static final Logger logger = LoggerFactory.getLogger(SparkClientCmd.class);

    private static PostBathRequest postBathRequest;
    private static String bathId;
    private static String fileName;
    private static String paraFileDir;
    private static ExecutorService pool = Executors.newCachedThreadPool();
    private static SparkClientArguments sparkClientArguments = null;

    private static class SingletonHolder {
        static final SparkClientCmd INSTANCE = new SparkClientCmd();
    }

    static SparkClientCmd getInstance() {
        return SingletonHolder.INSTANCE;
    }


    boolean init(String[] args) {
        try {
            sparkClientArguments = new SparkClientArguments(args);
        } catch (Exception e) {
            logger.error("SparkClientCmd init error", e);
            e.printStackTrace();
            sparkClientArguments.printHelp();
            return false;
        }

        if (sparkClientArguments.isPrintHelp()) {
            sparkClientArguments.printHelp();
            return false;
        }
        postBathRequest = sparkClientArguments.generateRequest();
        if (null == postBathRequest) {
            return false;
        }
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
        return true;
    }


    void start() {
        try {
            postBathRequest.setFile(this.putFileToHdfs(postBathRequest.getFile()));
            if(postBathRequest.getFiles()!=null && postBathRequest.getFiles().size()>0){
                this.putParaFilesToHdfs(postBathRequest.getFiles());
            }
            Batch batch = LivyServerApi.postSession(postBathRequest);
            bathId = String.valueOf(batch.getId());
            pool.submit(new TaskWorker(batch));
        } catch (Exception e) {
            if (sparkClientArguments != null) {
                sparkClientArguments.printHelp();
            }
            StringBuilder builder = new StringBuilder(e.toString()).append("\n");
            for (StackTraceElement element : e.getStackTrace()) {
                builder.append("\tat ").append(element.toString()).append("\n");
            }
            logger.warn(builder.toString());
        }
    }

    /**
     * 把本地文件上传hdfs ， 同时返回hdfs路径
     * 任务运行完毕， 删除该文件
     */
    private String putFileToHdfs(String file) throws Exception {
        HDFSFile hdfsFile = new HDFSFile();
        File localFile = new File(file);
        hdfsFile.checkPackagePath();
        if (localFile.exists() && localFile.isFile()) {
            fileName = ClientConf.APPLICATION_HDFS_DIR + localFile.getName();
            hdfsFile.putFromLocalToHdfs(file, ClientConf.APPLICATION_HDFS_DIR);
        }
        return fileName;
    }


    private void putParaFilesToHdfs(List<String> files) throws Exception {
        paraFileDir = ClientConf.APPLICATION_HDFS_DIR  ;
        List<String> hdfsFiles = new ArrayList<String>();
        for(String f : files){
            HDFSFile hdfsFile = new HDFSFile();
            File localFile = new File(f);
            hdfsFile.checkPackagePath();
            if (localFile.exists() && localFile.isFile()) {
                hdfsFiles.add(paraFileDir + localFile.getName());
                hdfsFile.putFromLocalToHdfs(f, paraFileDir);
            }
        }
        postBathRequest.setFiles(hdfsFiles);
    }


    private void deleteApplicationPackage() {
        try {
            HDFSFile hdfsFile = new HDFSFile();
            hdfsFile.deleteFile(ClientConf.APPLICATION_HDFS_DIR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void stop() {
        String state = LivyServerApi.getBathState(bathId);
        logger.info("SparkClientCmd state:{}", state);
        if (SessionState.dead.toString().equals(state) || SessionState.shutting_down.toString().equals(state)
                || SessionState.success.toString().equals(state) || SessionState.error.toString().equals(state)
                ) {
            logger.info(".............SparkClientCmd state:{}", state);
        } else {
            logger.info("start kill job...");
            LivyServerApi.killBashJob(bathId);
        }
        deleteApplicationPackage();
        logger.info("finish shutdown procedure.");
    }


}
