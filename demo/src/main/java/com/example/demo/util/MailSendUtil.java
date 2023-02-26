package com.example.demo.util;

import com.alibaba.fastjson.JSON;
import com.example.demo.entity.MailConstants;
import com.example.demo.entity.MailSendLog;
import com.example.demo.entity.User;
import com.example.demo.service.MailSendLogService;
import com.example.demo.service.UserService;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class MailSendUtil {
    @Autowired
    MailSendLogService mailSendLogService;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    UserService userService;
    @Resource
    RedisTemplate redisTemplate;
    @Scheduled(cron = "0/10 * * * * ?")
    public void mailResendTask() {
        List<MailSendLog> logs = mailSendLogService.getMailSendLogsByStatus();
        if (logs == null || logs.size() == 0) {
            System.out.println(1111);
            return;
        }
        System.out.println(logs);
        logs.forEach(mailSendLog->{
            System.out.println(mailSendLog);
            if (mailSendLog.getCount() >= 3) {
                mailSendLogService.updateMailSendLogStatus(mailSendLog.getMsgId(), 2);//直接设置该条消息发送失败
            }else{
                mailSendLogService.updateCount(mailSendLog.getMsgId());
                System.out.println("mailsendlog:"+mailSendLog);
                User user = (com.example.demo.entity.User) redisTemplate.opsForValue().get(mailSendLog.getMsgId());
                System.out.println(user);
                rabbitTemplate.convertAndSend(MailConstants.MAIL_EXCHANGE_NAME, MailConstants.MAIL_ROUTING_KEY_NAME, user, new CorrelationData(mailSendLog.getMsgId()));
            }
        });
    }
}
