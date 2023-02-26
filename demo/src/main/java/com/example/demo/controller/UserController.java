package com.example.demo.controller;

import com.alibaba.fastjson.JSON;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class UserController {
    @Resource
    UserService userService;
    @RequestMapping("/register")
    public String register(){
        return "login" ;
    }
    @ResponseBody
    @PostMapping("/sendCode")
    public Map<String,String> sendCode(@RequestParam Map<String,String> params){
        String s= JSON.toJSONString(params);
        User user =JSON.parseObject(s,User.class);
        boolean flag = userService.sendCode(user);
        Map<String,String> map = new HashMap<>();
        if (flag){
            map.put("msg","邮件发送成功，请前往您的邮箱进行注册验证");
            return map;
        }else {
            map.put("msg","邮件发送失败");
            return map;
        }
    }
    @ResponseBody
    // 判断是否注册成功
    @GetMapping("/lookCode/{token}")
    public Map<String,String> lookCode(@PathVariable("token")String token){
        boolean flag = userService.eqToken(token);
        Map<String,String> map = new HashMap<>();
        if (flag){
            map.put("msg","注册成功");

            /* 后续的操作 ... ...*/
            return map;
        }else {
            map.put("msg","注册码过期，请重新注册");
            return map;
        }
    }

}
