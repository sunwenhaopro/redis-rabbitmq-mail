package com.example.demo.service;

import com.example.demo.entity.MailSendLog;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface MailSendLogService {
    Integer insert(MailSendLog mailSendLog);
    Integer updateMailSendLogStatus(@Param("msgId") String msgId, @Param("status") Integer status);



    List<MailSendLog> getMailSendLogsByStatus();

    Integer updateCount(@Param("msgId") String msgId);
}
