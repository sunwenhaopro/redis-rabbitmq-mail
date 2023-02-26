package com.example.demo.service.serviceimpl;

import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserService;
import com.example.demo.util.CodeUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    UserMapper userMapper;

    @Resource
    CodeUtil codeUtil;

    /**
     * 添加注册的用户信息
     * @param user 注册的用户信息
     * @return 是否添加成功
     */
    @Override
    public boolean adduser(User user) {
        return userMapper.adduser(user) > 0;
    }

    /**
     * 生成链接和发送链接
     *
     * @param user 注册的用户信息
     */
    @Override
    public boolean sendCode(User user) {
        if ( codeUtil.sendCode(user)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断token是否过期
     * @param token 用户注册所接收的token
     * @return 注册成功与否
     */
    @Override
    public boolean eqToken(String token) {
        boolean flag = codeUtil.eqToken(token);

        if (flag){
            User user = codeUtil.findUser(token);
            adduser(user);
            return true;
        }else {
            return false;
        }
    }

}
