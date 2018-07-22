package com.local.hupper.kunkka.spark.model;

import com.local.hupper.kunkka.spark.annotation.FieldOption;

import java.util.List;
import java.util.Map;

/**
 * Created by lvhongpeng on 2017/9/6.
 */
abstract public class Request {

    /**
     * User to impersonate when running the job
     */
    @FieldOption(opt = "p", longOpt = "proxyUser", description = "User to impersonate when running the job")
    private String proxyUser;
    /**
     * jars to be used in this session
     */
//    @FieldOption(opt = "j", longOpt = "jars", description = "jars to be used in this session")
    private List<String> jars;
    /**
     * Python files to be used in this session
     */
    @FieldOption(opt = "pf", longOpt = "pyFiles", description = "Python files to be used in this session")
    private List<String> pyFiles;
    /**
     * files to be used in this session
     */
//    @FieldOption(opt = "files", longOpt = "files", description = "files to be used in this session")
    private List<String> files;
    /**
     * Amount of memory to use for the driver process
     */
    @FieldOption(opt = "dm", longOpt = "driverMemory", description = "Amount of memory to use for the driver process")
    private String driverMemory;
    /**
     * Number of cores to use for the driver process
     */
    @FieldOption(opt = "dc", longOpt = "driverCores", description = "Number of cores to use for the driver process")
    private int driverCores = -1;
    /**
     * Amount of memory to use per executor process
     */
    @FieldOption(opt = "em", longOpt = "executorMemory", description = "Amount of memory to use per executor process")
    private String executorMemory;
    /**
     * Number of cores to use for each executor
     */
    @FieldOption(opt = "ec", longOpt = "executorCores", description = "Number of cores to use for each executor")
    private int executorCores = -1;
    /**
     * Number of executors to launch for this session
     */
    @FieldOption(opt = "n", longOpt = "numExecutors", description = "Number of executors to launch for this session")
    private int numExecutors = -1;
    /**
     * Archives to be used in this session
     */
    @FieldOption(opt = "a", longOpt = "archives", description = "Archives to be used in this session")
    private List archives;
    /**
     * The name of the YARN queue to which submitted
     */
    @FieldOption(opt = "q", longOpt = "queue", description = "The name of the YARN queue to which submitted")
    private String queue;
    /**
     * The name of this session
     */
    @FieldOption(opt = "n", longOpt = "name", description = "The name of this session")
    private String name;
    /**
     * ROP=VALUE Arbitrary Spark configuration property
     */
//    @FieldOption(opt = "c", longOpt = "conf", description = "PROP=VALUE Arbitrary Spark configuration property")
    private Map conf;


    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public List<String> getJars() {
        return jars;
    }

    public void setJars(List<String> jars) {
        this.jars = jars;
    }

    public List<String> getPyFiles() {
        return pyFiles;
    }

    public void setPyFiles(List<String> pyFiles) {
        this.pyFiles = pyFiles;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public String getDriverMemory() {
        return driverMemory;
    }

    public void setDriverMemory(String driverMemory) {
        this.driverMemory = driverMemory;
    }

    public int getDriverCores() {
        return driverCores;
    }

    public void setDriverCores(int driverCores) {
        this.driverCores = driverCores;
    }

    public String getExecutorMemory() {
        return executorMemory;
    }

    public void setExecutorMemory(String executorMemory) {
        this.executorMemory = executorMemory;
    }

    public int getExecutorCores() {
        return executorCores;
    }

    public void setExecutorCores(int executorCores) {
        this.executorCores = executorCores;
    }

    public int getNumExecutors() {
        return numExecutors;
    }

    public void setNumExecutors(int numExecutors) {
        this.numExecutors = numExecutors;
    }

    public List getArchives() {
        return archives;
    }

    public void setArchives(List archives) {
        this.archives = archives;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map getConf() {
        return conf;
    }

    public void setConf(Map conf) {
        this.conf = conf;
    }


}
