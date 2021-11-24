package com.ren.seckill.utils;

import java.util.UUID;

/**
 * UUID工具类
 * @author ren
 * @version 1.0
 * @date 2021/11/11 15:42
 */
public class UUIDUtil {
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}