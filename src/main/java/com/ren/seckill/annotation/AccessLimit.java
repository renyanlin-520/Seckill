package com.ren.seckill.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口限流注解
 * @author ren
 * @version 1.0
 * @date 2021/11/23 11:26
 */
@Retention(RetentionPolicy.RUNTIME)  // 运行时注解
@Target(ElementType.METHOD)
public @interface AccessLimit {

    int second();
    int maxCount();
    boolean needLogin() default true;
}
