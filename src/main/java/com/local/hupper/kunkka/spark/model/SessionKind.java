package com.local.hupper.kunkka.spark.model;

/**
 * Created by lvhongpeng on 2017/9/5.
 */
public enum SessionKind {

    spark,
    /**
     * Interactive Python 2 Spark session
     */
    pyspark,
    /**
     * Interactive Python 3 Spark session
     */
    pyspark3,
    sparkr
}
