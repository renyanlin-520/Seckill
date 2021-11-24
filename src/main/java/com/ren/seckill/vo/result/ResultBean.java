package com.ren.seckill.vo.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ren
 * @version 1.0
 * @date 2021/11/5 16:47
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultBean {

    private long code;
    private String message;
    private Object data;

    /**
     * 成功的返回结果
     * @return
     */
    public static ResultBean success() {
        return new ResultBean(ResultBeanEnum.SUCCESS.getCode(), ResultBeanEnum.SUCCESS.getMessage(), null);
    }

    /**
     * 成功的返回结果
     * @param data
     * @return
     */
    public static ResultBean success(Object data) {
        return new ResultBean(ResultBeanEnum.SUCCESS.getCode(), ResultBeanEnum.SUCCESS.getMessage(), data);
    }

    /**
     * 失败的返回结果
     * @param resultBeanEnum
     * @return
     */
    public static ResultBean error(ResultBeanEnum resultBeanEnum) {
        return new ResultBean(resultBeanEnum.getCode(), resultBeanEnum.getMessage(), null);
    }

    /**
     * 失败的返回结果
     * @param resultBeanEnum
     * @param data
     * @return
     */
    public static ResultBean error(ResultBeanEnum resultBeanEnum, Object data) {
        return new ResultBean(resultBeanEnum.getCode(), resultBeanEnum.getMessage(), data);
    }
}
