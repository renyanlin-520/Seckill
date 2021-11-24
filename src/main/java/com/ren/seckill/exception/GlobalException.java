package com.ren.seckill.exception;

import com.ren.seckill.vo.result.ResultBeanEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 全局异常
 * @author ren
 * @version 1.0
 * @date 2021/11/11 15:16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalException extends RuntimeException {
    private ResultBeanEnum resultBeanEnum;
}
