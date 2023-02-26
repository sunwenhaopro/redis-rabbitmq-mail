package com.example.demo.service.serviceimpl;

import com.example.demo.entity.MailSendLog;
import com.example.demo.mapper.MailSendLogMapper;
import com.example.demo.service.MailSendLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class MailSendLogServiceImpl implements MailSendLogService {
    @Resource
    MailSendLogMapper mailSendLogMapper;
    public Integer insert(MailSendLog mailSendLog){
        return mailSendLogMapper.insert(mailSendLog);
    }

    public Integer updateMailSendLogStatus(String msgId, Integer status) {
        return mailSendLogMapper.updateMailSendLogStatus(msgId, status);
    }
    public List<MailSendLog> getMailSendLogsByStatus() {
        return mailSendLogMapper.getMailSendLogsByStatus();
    }

    public Integer updateCount(String msgId) {
        return mailSendLogMapper.updateCount(msgId);
    }
}

