package com.local.hupper.kunkka.spark.model;

import net.sf.json.JSONObject;

/**
 * Created by lvhongpeng on 2017/9/5.
 */
public class StatementOutput {

    private String status;
    private String execution_count;
    private JSONObject data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExecution_count() {
        return execution_count;
    }

    public void setExecution_count(String execution_count) {
        this.execution_count = execution_count;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }
}
