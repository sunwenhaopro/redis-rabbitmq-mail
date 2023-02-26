package com.example.demo.util;

import com.example.demo.entity.MailConstants;
import com.example.demo.entity.MailSendLog;
import com.example.demo.entity.User;
import com.example.demo.service.MailSendLogService;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class CodeUtil {
    @Resource
    RedisTemplate<String, User> redisTemplate;
    @Resource
    MailSendLogService mailSendLogService;
@Resource
    RabbitTemplate rabbitTemplate;
    public boolean sendCode(User user) {
        String msgId=UUID.randomUUID().toString();
        user.setMsgId(msgId);
        System.out.println(msgId);
        MailSendLog mailSendLog=new MailSendLog();
                 mailSendLog.setStatus(0);
                 mailSendLog.setCount(0);
                 mailSendLog.setMsgId(msgId);
                 mailSendLog.setExchange(MailConstants.MAIL_EXCHANGE_NAME);
                 mailSendLog.setRouteKey(MailConstants.MAIL_ROUTING_KEY_NAME);
                 mailSendLogService.insert(mailSendLog);
                 rabbitTemplate.convertAndSend(MailConstants.MAIL_EXCHANGE_NAME,MailConstants.MAIL_ROUTING_KEY_NAME,user,new CorrelationData(msgId));
        return true;
    }

    public boolean eqToken(String token) {
        return redisTemplate.hasKey(token);
    }

    public User findUser(String token) {
        return redisTemplate.opsForValue().get(token);
    }
}

