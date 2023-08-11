package com.wsj.server.controller;

import com.wsj.server.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class LoginController {

    @Autowired
    private LoginService userService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Map<String,Object> map){
        String phone = (String) map.get("phone");
        userService.sendCheckCode(phone);
        return ResponseEntity.ok("获取验证码成功");
    }

    @PostMapping("/loginVerification")
    public ResponseEntity loginVerification(@RequestBody Map<String,Object> map){
        //1、调用map集合获取请求参数
        String phone = (String) map.get("phone");
        String code = (String) map.get("verificationCode");
        //2、调用userService完成用户登录
        Map<String,Object> retMap = userService.loginVerification(phone,code);
        //3、构造返回
        return ResponseEntity.ok(retMap);
    }

}
