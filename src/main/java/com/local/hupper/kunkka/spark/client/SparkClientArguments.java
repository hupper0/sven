package com.local.hupper.kunkka.spark.client;

import com.local.hupper.kunkka.spark.annotation.FieldOption;
import com.local.hupper.kunkka.spark.model.PostBathRequest;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by lvhongpeng on 2017/9/6.
 * <p>
 * usage: sven.sh [arguments] [options]
 * sven.sh [arguments] [options]
 * arguments
 * --file(required) 			File containing the application to execute
 * Options:
 * --className 				Application Java/Spark main class.
 * --args 					    Command line arguments for the application
 * --proxyUser 				User to impersonate when running the job
 * --jars 					    jars to be used in this session
 * --pyFiles 					Python files to be used in this session
 * --driverMemory 				Amount of memory to use for the driver process
 * --driverCores 				Number of cores to use for the driver process
 * --executorMemory 			Amount of memory to use per executor process
 * --executorCores 			Number of cores to use for each executor
 * --numExecutors 				Number of executors to launch for this session
 * --archives 					Archives to be used in this session
 * --queue 					The name of the YARN queue to which submitted
 * --name 					    The name of this session
 * --conf 					    PROP=VALUE Arbitrary Spark configuration property
 * --logs 					    Logs of the application output in the yarn cluster
 * --version 					Print the version of current spark-client.
 */
public class SparkClientArguments {

    private static final Logger logger = LoggerFactory.getLogger(SparkClientArguments.class);


    private static Options options = new Options();
    private CommandLine commandLine;
    static PrintWriter logPrintWriter = new PrintWriter(System.out);


    private static final Option logs = new Option("l", "logs", true, "Logs of the application output in the yarn cluster");
    private static final Option version = new Option("v", "version", false, "Print the version of current spark-client");
    private static final Option helpOption = new Option("h", "help", false, "print the help messages");
    private static final Option filesOption = OptionBuilder.withArgName(" ").hasArgs().withDescription("files to be used in this session").create("files");
    private static final Option jarsOption = OptionBuilder.withArgName("jars").hasArgs().withDescription("jars to be used in this session").create("jars");
    private static final Option argsOption = OptionBuilder.withArgName("args").hasArgs().withDescription("Command line arguments for the application").create("args");
    private static final Option conf = OptionBuilder.withArgName("conf").hasArgs().withDescription("PROP=VALUE Arbitrary Spark configuration property").create("conf");





    /**
     * load command args options
     */
    private void loadArgs() {
        logger.info("loadArgs options");
        List<Field> fieldList = new ArrayList();
        List<Field> declaredFieldList = Arrays.asList(PostBathRequest.class.getDeclaredFields());
        List<Field> superClassFieldList = Arrays.asList(PostBathRequest.class.getSuperclass().getDeclaredFields());
        fieldList.addAll(declaredFieldList);
        fieldList.addAll(superClassFieldList);
        for (Field f : fieldList) {
            FieldOption fieldOption = f.getAnnotation(FieldOption.class);
            if(fieldOption!=null){
                Option option = new Option(fieldOption.opt(), fieldOption.longOpt(), fieldOption.hasArg(), fieldOption.description());
                options.addOption(option);
            }
        }
        options.addOption(logs).addOption(version).addOption(helpOption);
        argsOption.setRequired(false);
        filesOption.setRequired(false);
        conf.setRequired(false);
        options.addOption(argsOption).addOption(conf).addOption(filesOption);
    }




    /**
     * check sven.sh version
     */
    private void checkVersion() {
        File folder = new File("/www/kunkka-sven/conf/");
        File[] fileArray = folder.listFiles();
        if (fileArray != null) {
            for (File file : fileArray) {
                if (file.getName().matches("VERSION-\\d+.\\d+.\\d+")) {
                    String version = file.getName().substring(8);
                    logPrintWriter.println("sven.sh spark-client");
                    logPrintWriter.println("Version: v" + version);
                    logPrintWriter.flush();
                    return;
                }
            }
        }
        logPrintWriter.println("No version file found, exit");
        logPrintWriter.flush();
    }


    /**
     * print sven.sh usage help
     */
    void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("sven.sh", options);
        System.out.println("");
    }

    /**
     * isPrintHelp
     *
     * @return isPrintHelp
     */
    boolean isPrintHelp() {
        return this.commandLine.hasOption(helpOption.getOpt());
    }


    /**
     * init command args
     *
     * @param args args
     */
    SparkClientArguments(String[] args) throws Exception {
        loadArgs();
        CommandLineParser commandLineParser = new PosixParser();
        this.commandLine = commandLineParser.parse(options, args, false);
    }


    /**
     * check file exist
     *
     * @return check return
     */
    private boolean checkArgs() {
        if (!commandLine.hasOption("f")) {
            logPrintWriter.println("Error arguments -file is required ,please check your command");
            logPrintWriter.flush();
            printHelp();
            return false;
        } else {
            File f = new File(this.commandLine.getOptionValue("file"));
            if (!f.exists() || f.isDirectory()) {
                logPrintWriter.println("Error arguments -file is required and the file must be exists," +
                        " please check your command");
                logPrintWriter.flush();
                return false;
            }
        }
        return true;
    }


    PostBathRequest generateRequest() {
        PostBathRequest postBathRequest = null;
        if (this.commandLine.hasOption(version.getOpt())) {
            checkVersion();
            return null;
        }
        if (checkArgs()) {
            try {
                postBathRequest = new PostBathRequest();
                postBathRequest.setFile(this.commandLine.getOptionValue("file"));
                if (this.commandLine.hasOption("class")) {
                    postBathRequest.setClassName(this.commandLine.getOptionValue("class"));
                }
                if (this.commandLine.hasOption("args")) {
                    List<String> argsList = Arrays.asList(this.commandLine.getOptionValues("args"));
                    postBathRequest.setArgs(argsList);
                    logger.info("args: {}", argsList.toString());
                }
                if (this.commandLine.hasOption("proxyUser")) {
                    postBathRequest.setProxyUser(this.commandLine.getOptionValue("proxyUser"));
                }
                if (this.commandLine.hasOption("jars")) {
                    List<String> jarList = Arrays.asList(this.commandLine.getOptionValues("jars"));
                    postBathRequest.setJars(jarList);
                }
                if (this.commandLine.hasOption("pyFiles")) {
                    List<String> pyFilesList = Arrays.asList(this.commandLine.getOptionValues("pyFiles"));
                    postBathRequest.setPyFiles(pyFilesList);
                }
                if (this.commandLine.hasOption("driverMemory")) {
                    postBathRequest.setDriverMemory(this.commandLine.getOptionValue("driverMemory"));
                }
                if (this.commandLine.hasOption("driverCores")) {
                    postBathRequest.setDriverCores(Integer.parseInt(this.commandLine.getOptionValue("driverCores")));
                }
                if (this.commandLine.hasOption("executorMemory")) {
                    postBathRequest.setExecutorMemory(this.commandLine.getOptionValue("executorMemory"));
                }
                if (this.commandLine.hasOption("executorCores")) {
                    postBathRequest.setExecutorCores(Integer.parseInt(this.commandLine.getOptionValue("executorCores")));
                }
                if (this.commandLine.hasOption("numExecutors")) {
                    postBathRequest.setNumExecutors(Integer.parseInt(this.commandLine.getOptionValue("numExecutors")));
                }
                if (this.commandLine.hasOption("archives")) {
                    List<String> archivesList = Arrays.asList(this.commandLine.getOptionValues("archives"));
                    postBathRequest.setArchives(archivesList);
                }
                if (this.commandLine.hasOption("files")) {
                    List<String> files = Arrays.asList(this.commandLine.getOptionValues("files"));
                    postBathRequest.setFiles(files);
                }
                if (this.commandLine.hasOption("queue")) {
                    postBathRequest.setQueue(this.commandLine.getOptionValue("queue"));
                }
                if (this.commandLine.hasOption("name")) {
                    postBathRequest.setName(this.commandLine.getOptionValue("name"));
                }
                if (this.commandLine.hasOption("conf")) {
                    String confArr[] = this.commandLine.getOptionValues("conf");
                    Map<String, String> confMap = new HashMap();
                    for (String entry : confArr) {
                        confMap.put(entry.split("=")[0], entry.replace(entry.split("=")[0] + "=", ""));
                    }
                    postBathRequest.setConf(confMap);
                }
                if (this.commandLine.hasOption("logs")) {
                    try {
                        File file = new File(this.commandLine.getOptionValue("logs"));
                        if (file.exists()) {
                            file.createNewFile();
                        }
                        logPrintWriter = new PrintWriter(new FileWriter(file), true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    logPrintWriter = new PrintWriter(System.out);
                }
            } catch (Exception e) {
                logger.error("generateRequest fail", e);
                e.printStackTrace();
                return null;
            }
        }
        return postBathRequest;
    }


}
