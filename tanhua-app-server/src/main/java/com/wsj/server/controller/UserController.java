package com.wsj.server.controller;

import com.wsj.domain.UserInfo;
import com.wsj.server.interceptor.UserHolder;
import com.wsj.server.service.UserInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserInfoService userInfoService;

    /**
     * 保存用户信息
     *   UserInfo
     *   请求头中携带token
     */
    @PostMapping("/loginReginfo")
    public ResponseEntity loginReginfo(@RequestBody UserInfo userInfo,
                                       @RequestHeader("Authorization") String token){

        userInfo.setId(UserHolder.getUserId());
        userInfoService.save(userInfo);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/loginReginfo/head")
    public ResponseEntity head(MultipartFile headPhota){
        userInfoService.updateHead(headPhota,UserHolder.getUserId());
        return ResponseEntity.ok(null);
    }
}
