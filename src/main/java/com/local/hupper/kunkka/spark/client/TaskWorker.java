package com.local.hupper.kunkka.spark.client;

import com.local.hupper.kunkka.spark.api.LivyServerApi;
import com.local.hupper.kunkka.spark.model.Batch;
import com.local.hupper.kunkka.spark.model.SessionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by lvhongpeng on 2017/9/8.
 */
public class TaskWorker implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(TaskWorker.class);

    private AtomicLong delay = new AtomicLong(1000);

    private Batch batch;

    TaskWorker(Batch batch) {
        this.batch = batch;
    }


    @Override
    public void run() {
        logger.info("start run TaskWorker....");
        int logOffset = 0;
        PrintWriter logPrintWriter = SparkClientArguments.logPrintWriter;
        if (logPrintWriter == null) {
            logPrintWriter = new PrintWriter(System.out);
        }
        String id = String.valueOf(batch.getId());
        long delay = this.delay.get();
        while (true) {
            if (batch == null) {
                break;
            }
            String state = LivyServerApi.getBathState(id);
            if (SessionState.dead.toString().equals(state) || SessionState.shutting_down.toString().equals(state)
                    || SessionState.error.toString().equals(state)
                    ) {
                logger.info("get state :{}", state);
                System.exit(1);
                break;
            }if(SessionState.success.toString().equals(state)){
                System.exit(0);
            } else {
                List<String> logs = LivyServerApi.getBashJobLog(id, 0, Integer.MAX_VALUE);
//                logger.info("get logs size:{}", logs.size());
                if (logs.size() > 0) {
                    for (int i = logOffset; i < logs.size(); i++) {
                        logger.info(logs.get(i));
                        logPrintWriter.println(logs.get(i));
                        logPrintWriter.flush();
                        logOffset = logOffset + 1;
                    }
                }
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    logger.error("TaskWorker sleep error: {}", e);
                    e.printStackTrace();
                }
            }
        }
    }


}
