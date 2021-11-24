package com.ren.seckill.vo.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author ren
 * @version 1.0
 * @date 2021/11/5 16:48
 */
@Getter
@ToString
@AllArgsConstructor
public enum ResultBeanEnum {
    /**
     * 通用
     */
    SUCCESS(200, "SUCCESS"),
    ERROR(500, "SERVER ERROR"),

    /**
     * 登录
     */
    LOGIN_ERROR(5001, "用户名或密码不正确"),
    LOGIN_USER_ERROR(5003, "找不到该用户"),
    LOGIN_MOBILE_ERROR(5002, "手机号码格式不正确"),

    /**
     * 秒杀
     */
    EMPTY_STOCK(5005, "库存不足"),
    REPEAT_STOCK(5006, "该商品每人只限购一件"),
    SESSION_ERROR(5009, "用户不存在"),
    REQUEST_ILLEGAL(5011, "该请求非法，请重新尝试"),
    CAPTCHA_ERROR(5012, "验证码不正确，请重新输入"),
    ACCESS_LIMIT_REAHCED(5012, "访问频繁，请稍后再试~"),

    /**
     * 更新密码
     */
    MOBILE_NOT_EXIST(5007, "手机号码不存在"),
    PASSWORD_UPDATE_FAILED(5008, "密码修改失败"),

    ORDER_NOT_EXIST(5010, "该订单不存在"),

    /**
     * 参数校验异常
     */
    VALIDATE_BIND_ERROR(5004, "参数校验异常")
    ;

    private final Integer code;
    private final String message;
}
