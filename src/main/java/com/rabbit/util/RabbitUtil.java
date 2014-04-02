package com.rabbit.util;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 用于提供Rabbit的操作类
 * Created by zero on 14-4-1.
 */
@Component
public class RabbitUtil {
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitUtil(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
    }

    public <T> void sendReliable(String exchange, T message) {
        //实现将message通过json转换&将对象发送
        //convertAndSend(String exchange, String routingKey, Object message, MessagePostProcessor messagePostProcessor, CorrelationData correlationData)
        rabbitTemplate.convertAndSend(exchange, "", message, new MessagePostProcessor() {
            //实现message操作处理实现
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                //设置信息的属性信息&设置发送模式（PERSISTENT:连续的）
                message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return message;
            }
        }, new CorrelationData(String.valueOf(message)));
    }
}
