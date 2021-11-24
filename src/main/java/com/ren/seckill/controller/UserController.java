package com.ren.seckill.controller;


import com.ren.seckill.rabbitmq.MqSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Ren
 * @since 2021-11-05
 */
@Controller
@RequestMapping("/sk/user")
public class UserController {

    @Autowired
    private MqSender mqSender;

    /**
     * 测试发送RabbitMq 发送消息
     */
    @RequestMapping("/mq")
    @ResponseBody
    public void mq() {
        mqSender.send("HELLO RABBITMQ");
    }

}
