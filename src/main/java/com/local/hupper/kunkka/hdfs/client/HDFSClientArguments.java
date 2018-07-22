package com.local.hupper.kunkka.hdfs.client;

import com.local.hupper.kunkka.hdfs.HDFSFile;
import org.apache.commons.cli.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.PrintWriter;

/**
 * Created by lvhongpeng on 2017/9/12.
 * <p>
 * usage: sven.sh [arguments] [options]
 * arguments
 * -fs(required) 				identification	run manange the hdfs
 * Options:
 * -ls 						eg: sven.sh fs -ls         hdfsDir
 * -cat 						eg: sven.sh fs -cat        hdfsFile
 * -getmerge					eg: sven.sh fs -getmerge   hdfsDir     localFile
 * -copyToLocal				eg: sven.sh fs -copyToLocal   hdfsDir     localPath
 * -put 						eg: sven.sh fs -put        localDir    hdfsDir
 * -rm 						eg: sven.sh fs -rm         hdfsFile
 * -rmr 					    eg: sven.sh fs -rmr        hdfsDir
 * -mkdir 					    eg: sven.sh fs -mkdir      hdfsDir
 * -get 						eg: sven.sh fs -get        hdfsPath     localFile
 */
public class HDFSClientArguments {


    private static final Logger logger = LoggerFactory.getLogger(HDFSClientArguments.class);


    private static Options options = new Options();
    private CommandLine commandLine;
    private static PrintWriter logPrintWriter = new PrintWriter(System.out);
    private static HDFSFile hdfsFile = null;

    private static final Option ls = new Option("ls", true, "sven.sh fs -ls hdfsDir;list show the hdfs file");
    private static final Option put = new Option("put", true, "sven.sh fs -put localPath hdfsDir; put the local file to Hdfs");
    private static final Option cat = new Option("cat", true, "sven.sh fs -cat hdfsFile; cat the hdfs file");
    private static final Option rm = new Option("rm", true, "sven.sh fs -rm  hdfsFile; rm the hdfs file");
    private static final Option rmr = new Option("rmr", true, "sven.sh fs -rmr hdfsFile; recursion rm the hdfs file");
    private static final Option copyToLocal = new Option("copyToLocal", true, "sven.sh fs -copyToLocal hdfsDir localPath");
    private static final Option moveToLocal = new Option("moveToLocal", true, "sven.sh fs -moveToLocal hdfsDir localPath");

    private static final Option cp = new Option("cp", true, "sven.sh fs -cp hdfsDir hdfsDir");
    private static final Option re = new Option("re", true, "sven.sh fs -re hdfsDir hdfsDir; rename file");

    private static final Option mkdir = new Option("mkdir", true, "sven.sh fs -mkdir hdfsDir; mkdir on the hdfs");
    private static final Option get = new Option("get", true, "sven.sh fs -get hdfsFile localFile;get hdfsFile to localPath");
    private static final Option getmerge = new Option("getmerge", true, "sven.sh fs -getmerge hdfsFile localFile;getmerge hdfsFile to localPath");
    private static final Option version = new Option("v", "version", false, "Print the version of current hdfs-client");
    private static final Option helpOption = new Option("h", "help", false, "print the help messages");


    /**
     * init command args
     *
     * @param args args
     */
    public HDFSClientArguments(String[] args) throws Exception {
        hdfsFile = new HDFSFile();
        loadArgs();
        CommandLineParser commandLineParser = new DefaultParser();
        this.commandLine = commandLineParser.parse(options, args);
    }


    /**
     * load command args options
     */
    private void loadArgs() {
        options.addOption(ls).addOption(put).addOption(cat).addOption(rm).addOption(rmr).addOption(mkdir)
                .addOption(get).addOption(getmerge)
                .addOption(version).addOption(helpOption).addOption(copyToLocal).addOption(cp).addOption(re).addOption(moveToLocal);
    }

    /**
     * check sven.sh version
     */
    private void checkVersion() {
        File folder = new File("../conf/");
        File[] fileArray = folder.listFiles();
        if (fileArray != null) {
            for (File file : fileArray) {
                if (file.getName().matches("VERSION-\\d+.\\d+.\\d+")) {
                    String version = file.getName().substring(8);
                    logPrintWriter.println("sven.sh hdfs client");
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
     * isPrintHelp
     *
     * @return isPrintHelp
     */
    boolean isPrintHelp() {
        return this.commandLine.hasOption(helpOption.getOpt());
    }


    /**
     * print sven.sh usage help
     */
    void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("sven.sh", options, true);
    }

    /**
     * checkArgs
     *
     * @param args arg: ls, mkdir, get, getmerge, rm, rmr, cat ,put
     * @return check status
     */
    private boolean checkArgs(String args) {
        return (StringUtils.isNotEmpty(args) &&
                StringUtils.isNotEmpty(this.commandLine.getOptionValue(args)));
    }



    private void printArr(String arr[]){
        if(arr.length>0){
            for(String key : arr){
                System.out.print(key + ",");
            }
        }
    }

    public boolean generateJob() {
        if (this.commandLine.hasOption(version.getOpt())) {
            checkVersion();
            return true;
        }

        try {
            if (this.commandLine.hasOption("ls")) {
                String dir = this.commandLine.getOptionValue("ls");
                if (checkArgs("ls")) {
                    hdfsFile.ls(dir, logPrintWriter);
                    return true;
                }
            } else if (this.commandLine.hasOption("put")) {
                String dir = this.commandLine.getOptionValue("put");
                if (checkArgs("put")) {
                    String argsArr[] = commandLine.getArgs();
                    printArr(argsArr);
                    if (argsArr.length == 2) {
                        hdfsFile.putFromLocalToHdfs(dir, argsArr[1]);
                        return true;
                    }
                }
            } else if (this.commandLine.hasOption("get")) {
                String dir = this.commandLine.getOptionValue("get");
                if (checkArgs("get")) {
                    String argsArr[] = commandLine.getArgs();
                    if (argsArr.length == 2) {
                        hdfsFile.get(dir, argsArr[1]);
                        return true;
                    }
                }
            } else if (this.commandLine.hasOption("cp")) {
                String dir = this.commandLine.getOptionValue("cp");
                if (checkArgs("cp")) {
                    String argsArr[] = commandLine.getArgs();
                    if (argsArr.length == 2) {
                        hdfsFile.putFromHdfsToHdfs(dir, argsArr[1]);
                        return true;
                    }
                }
            } else if (this.commandLine.hasOption("re")) {
                String dir = this.commandLine.getOptionValue("re");
                if (checkArgs("re")) {
                    String argsArr[] = commandLine.getArgs();
                    if (argsArr.length == 2) {
                        hdfsFile.rename(dir, argsArr[1]);
                        return true;
                    }
                }
            }
            else if (this.commandLine.hasOption("moveToLocal")) {
                String dir = this.commandLine.getOptionValue("moveToLocal");
                if (checkArgs("moveToLocal")) {
                    String argsArr[] = commandLine.getArgs();
                    if (argsArr.length == 2) {
                        hdfsFile.moveFromHdfsToLocal(dir, argsArr[1], logPrintWriter);
                        return true;
                    }
                }
            }
            else if (this.commandLine.hasOption("copyToLocal")) {
                String dir = this.commandLine.getOptionValue("copyToLocal");
                if (checkArgs("copyToLocal")) {
                    String argsArr[] = commandLine.getArgs();
                    if (argsArr.length == 2) {
                        hdfsFile.copyFromHdfsToLocal(dir, argsArr[1], logPrintWriter);
                        return true;
                    }
                }
            }else if (this.commandLine.hasOption("getmerge")) {
                String dir = this.commandLine.getOptionValue("getmerge");
                if (checkArgs("getmerge")) {
                    String argsArr[] = commandLine.getArgs();
                    if (argsArr.length == 2) {
                        hdfsFile.getMerge(dir, argsArr[1]);
                        return true;
                    }
                }
            } else if (this.commandLine.hasOption("cat")) {
                String dir = this.commandLine.getOptionValue("cat");
                if (checkArgs("cat")) {
                    hdfsFile.read(dir);
                    return true;
                }

            } else if (this.commandLine.hasOption("rm")) {
                String dir = this.commandLine.getOptionValue("rm");
                if (checkArgs("rm")) {
                    hdfsFile.deleteFile(dir);
                    return true;
                }
            } else if (this.commandLine.hasOption("rmr")) {
                String dir = this.commandLine.getOptionValue("rmr");
                if (checkArgs("rmr")) {
                    hdfsFile.deleteFile(dir);
                    return true;
                }
            } else if (this.commandLine.hasOption("mkdir")) {
                String dir = this.commandLine.getOptionValue("mkdir");
                if (checkArgs("mkdir")) {
                    hdfsFile.mkdirs(dir);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("generateJob error", e);
            return false;
        }
        return false;
    }


}
