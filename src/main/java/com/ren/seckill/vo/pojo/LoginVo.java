package com.ren.seckill.vo.pojo;

import com.ren.seckill.validator.IsMobile;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @author ren
 * @version 1.0
 * @date 2021/11/10 15:05
 */
@Data
public class LoginVo {
    @NotNull
    @IsMobile  // IsMobile自定义注解
    private String userName;

    @NotNull
    @Length()
    private String password;
}
