package com.ren.seckill.controller;

import com.ren.seckill.pojo.User;
import com.ren.seckill.service.IUserService;
import com.ren.seckill.vo.pojo.LoginVo;
import com.ren.seckill.vo.result.ResultBean;
import com.ren.seckill.vo.result.ResultBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author ren
 * @version 1.0
 * @date 2021/11/5 16:40
 */
@Controller
@RequestMapping("/sk/login")
public class LoginController {

    @Autowired
    private IUserService userService;

    /**
     * 跳转到登录页
     * @return
     */
    @RequestMapping("/toLogin")
    public String toLogin() {
        return "login";
    }

    @PostMapping("/loginUser")
    @ResponseBody
    public ResultBean loginUser(@Valid LoginVo loginVo, HttpServletResponse response, HttpServletRequest request) {
        return userService.loginUser(loginVo, response, request);
    }
}
