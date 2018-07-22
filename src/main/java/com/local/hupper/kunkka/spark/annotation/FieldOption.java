package com.local.hupper.kunkka.spark.annotation;

import java.lang.annotation.*;

/**
 * Created by lvhongpeng on 2017/9/11.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface FieldOption {

    String opt() default "";

    String longOpt() default "";

    boolean hasArg() default true;

    String description() default "";

}
