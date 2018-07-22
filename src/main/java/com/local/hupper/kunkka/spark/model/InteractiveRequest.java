package com.local.hupper.kunkka.spark.model;

/**
 * Created by lvhongpeng on 2017/9/5.
 */
public class InteractiveRequest extends Request {

    /**
     * The session kind (required)
     */
    private SessionKind kind;
    /**
     * Timeout in second to which session be orphaned
     */
    private int heartbeatTimeoutInSecond;


    public SessionKind getKind() {
        return kind;
    }

    public void setKind(SessionKind kind) {
        this.kind = kind;
    }

    public int getHeartbeatTimeoutInSecond() {
        return heartbeatTimeoutInSecond;
    }

    public void setHeartbeatTimeoutInSecond(int heartbeatTimeoutInSecond) {
        this.heartbeatTimeoutInSecond = heartbeatTimeoutInSecond;
    }
}
