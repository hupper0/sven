package com.local.hupper.kunkka.spark.model;

import java.util.List;
import java.util.Map;

/**
 * Created by lvhongpeng on 2017/9/5.
 */
public class Batch {
    /**
     * The session id
     */
    private int id;
    /**
     * The application id of this session
     */
    private Integer appId;
    /**
     * The detailed application info
     */
    private Map<String, String> appInfo;
    /**
     * The log lines
     */
    private List<String> log;
    /**
     * The batch state
     */
    private String state;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Map<String, String> getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(Map<String, String> appInfo) {
        this.appInfo = appInfo;
    }

    public List<String> getLog() {
        return log;
    }

    public void setLog(List<String> log) {
        this.log = log;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


    @Override
    public String toString() {
        return "Bath:" + this.hashCode() + "{ id : " + id + ",appId:" + appId + ",state:" + state + "}";
    }
}
