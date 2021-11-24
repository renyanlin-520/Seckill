package com.ren.seckill.exception;

import com.ren.seckill.vo.result.ResultBean;
import com.ren.seckill.vo.result.ResultBeanEnum;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理类
 * @author ren
 * @version 1.0
 * @date 2021/11/11 15:18
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResultBean ExceptionHandler(Exception e) {
        if(e instanceof GlobalException) {
            GlobalException globalException = (GlobalException) e;
            return ResultBean.error(globalException.getResultBeanEnum());
        } else if (e instanceof BindException) {
            BindException bindException = (BindException) e;
            ResultBean resultBean = ResultBean.error(ResultBeanEnum.VALIDATE_BIND_ERROR);
            resultBean.setMessage("参数校验异常：" + bindException.getBindingResult().getAllErrors().get(0).getDefaultMessage());
            return resultBean;
        }
        return ResultBean.error(ResultBeanEnum.ERROR);
    }
}
