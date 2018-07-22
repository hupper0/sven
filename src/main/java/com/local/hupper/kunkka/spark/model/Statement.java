package com.local.hupper.kunkka.spark.model;

/**
 * Created by lvhongpeng on 2017/9/5.
 */
public class Statement {

    private int id;
    private StatementState state;

    private StatementOutput output;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public StatementState getState() {
        return state;
    }

    public void setState(StatementState state) {
        this.state = state;
    }

    public StatementOutput getStatementOutput() {
        return output;
    }

    public void setStatementOutput(StatementOutput output) {
        this.output = output;
    }
}
