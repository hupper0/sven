package com.local.hupper.kunkka.spark.model;

/**
 * Created by lvhongpeng on 2017/9/5.
 */
public class StatementResponse {

    /**
     * Execution status
     */
    private String status;
    /**
     * A monotonically increasing number
     */
    private int executionCount;
    /**
     * Statement output
     * An object mapping a mime type to the result.
     * If the mime type is application/json, the value is a JSON value.
     */
    private String data;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getExecutionCount() {
        return executionCount;
    }

    public void setExecutionCount(int executionCount) {
        this.executionCount = executionCount;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
