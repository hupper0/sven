package com.local.hupper.kunkka.spark.model;

/**
 * Created by lvhongpeng on 2017/9/5.
 */
public enum SessionState {
    /**
     * Statement is enqueued but execution hasn't started
     */
    not_started,
    starting,
    /**
     * Session is waiting for input
     */
    idle,
    /**
     * Session is executing a statement
     */
    busy,
    shutting_down,
    error,
    /**
     * Session has exited
     */
    dead,
    /**
     * Session is successfully stopped
     */
    success
}
