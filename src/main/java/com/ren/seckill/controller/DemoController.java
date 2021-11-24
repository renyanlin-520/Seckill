package com.ren.seckill.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author ren
 * @version 1.0
 * @date 2021/11/5 14:33
 */
@Controller
@RequestMapping("/seckill/demo")
public class DemoController {

    /**
     * 测试页面跳转
     * @param model
     * @return
     */
    @RequestMapping("/test")
    public String hello(Model model) {
        model.addAttribute("msg", "The project startup test succeeded!");
        model.addAttribute("name", "Ren YanLin");
        model.addAttribute("time", new Date());
        return "test";
    }
}
