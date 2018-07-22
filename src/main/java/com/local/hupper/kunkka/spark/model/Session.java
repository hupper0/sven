package com.local.hupper.kunkka.spark.model;

import java.util.List;
import java.util.Map;

/**
 * Created by lvhongpeng on 2017/9/5.
 */
public class Session {

    /**
     * The session id
     */
    private int id;
    /**
     * The application id of this session
     */
    private Integer appId;
    /**
     * Remote user who submitted this session
     */
    private String owner;
    /**
     * User to impersonate when running
     */
    private String proxyUser;
    /**
     * Session kind (spark, pyspark, or sparkr)
     */
    private SessionKind kind;
    /**
     * The log lines
     */
    private List<String> log;
    /**
     * The session state
     */
    private SessionState state;
    /**
     * The detailed application info
     */
    private Map<String, String> appInfo;

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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public SessionKind getKind() {
        return kind;
    }

    public void setKind(SessionKind kind) {
        this.kind = kind;
    }

    public List<String> getLog() {
        return log;
    }

    public void setLog(List<String> log) {
        this.log = log;
    }

    public SessionState getState() {
        return state;
    }

    public void setState(SessionState state) {
        this.state = state;
    }

    public Map<String, String> getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(Map<String, String> appInfo) {
        this.appInfo = appInfo;
    }
}
