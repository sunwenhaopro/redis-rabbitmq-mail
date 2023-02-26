package com.example.demo.receiver;

import com.example.demo.entity.MailConstants;
import com.example.demo.entity.User;

import com.example.demo.service.MailSendLogService;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class MailReceiver {
    public static final Logger logger = LoggerFactory.getLogger(MailReceiver.class);
    @Value("${spring.mail.username}")
    String fromAddress;
    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    MailSendLogService mailSendLogService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @RabbitListener(queues = MailConstants.MAIL_QUEUE_NAME)
    public void handler(Message message, Channel channel) throws IOException {
        User user = (User) message.getPayload();
        MessageHeaders headers = message.getHeaders();
        System.out.println(message);
        System.out.println(headers);
        Long tag = (Long) headers.get
                (AmqpHeaders.DELIVERY_TAG);
        String msgId =  user.getMsgId();
        System.out.println(msgId);
        if (stringRedisTemplate.opsForHash().entries("mail_log").containsKey(msgId)) {
            //redis 中包含该 key，说明该消息已经被消费过;
            logger.info(msgId + ":消息已经被消费");
            channel.basicAck(tag, false);//确认消息已消费
            return;
        }
        MimeMessage mailmessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mailmessage);
        String token = user.getMsgId();
        System.out.println("444");
        System.out.println(token);
        redisTemplate.opsForValue().set(token, user);
        System.out.println("555");
        redisTemplate.expire(token, 300000, TimeUnit.SECONDS);
        try {
            messageHelper.setFrom(fromAddress);
            messageHelper.setTo(user.getAccount());
            messageHelper.setSubject("注册");
            String html = "<html>\n" +
                    "<body>\n" +
                    "<p>请点击下方链接注册</p>\n" +
                    "<a href=\"http://localhost:8088/lookCode/" + token + "\">http://localhost:8081/lookCode/" + token + "</a>" +
                    "</body>\n" +
                    "</html>";
            messageHelper.setText(html, true);
            javaMailSender.send(mailmessage);
            stringRedisTemplate.opsForHash().put("mail_log", msgId, "javaboy");
                mailSendLogService.updateMailSendLogStatus(msgId, 1);
                channel.basicAck(tag, false);
                System.out.println("邮件发送成功");
              } catch (Exception e) {
            channel.basicNack(tag, false, false);
            e.printStackTrace();
            System.out.println("邮件发送失败");
        }
}
    }

