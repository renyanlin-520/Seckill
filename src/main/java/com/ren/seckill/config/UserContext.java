package com.ren.seckill.config;

import com.ren.seckill.pojo.User;

/**
 * @author ren
 * @version 1.0
 * @date 2021/11/23 11:40
 */
public class UserContext {
    private static ThreadLocal<User> userHolder = new ThreadLocal<>();

    public static void setUser(User user) {
        userHolder.set(user);
    }

    public static User getUser() {
        return userHolder.get();
    }
}
