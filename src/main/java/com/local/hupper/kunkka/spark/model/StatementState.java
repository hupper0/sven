package com.local.hupper.kunkka.spark.model;

/**
 * Created by lvhongpeng on 2017/9/5.
 */
public enum StatementState {
    waiting,
    running,
    available,
    error,
    cancelling,
    cancelled

}
