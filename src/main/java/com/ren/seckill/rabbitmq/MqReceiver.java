package com.ren.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * 消息消费者
 * @author ren
 * @version 1.0
 * @date 2021/11/20 15:31
 */
@Service
@Slf4j
public class MqReceiver {

    @RabbitListener(queues = "queuq")
    public void receive(Object msg) {
        log.info("接收消息：" + msg);
    }
}
