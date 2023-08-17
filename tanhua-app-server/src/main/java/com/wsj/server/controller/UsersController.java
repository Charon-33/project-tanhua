package com.wsj.server.controller;

import com.wsj.domain.UserInfo;
import com.wsj.server.interceptor.UserHolder;
import com.wsj.server.service.UserInfoService;
import com.wsj.vo.UserInfoVo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/users")
public class UsersController {
    @Resource
    private UserInfoService userInfoService;

    /**
     * 查询用户资料
     */
    @GetMapping
    public ResponseEntity users(Long userId){
        if(userId == null){
            userId = UserHolder.getUserId();
        }
        UserInfoVo userInfo = userInfoService.findById(userId);
        return ResponseEntity.ok(userInfo);
    }

    /**
     * 更新用户资料
     */
    @PutMapping
    public ResponseEntity updateUserInfo(@RequestBody UserInfo userInfo){
        userInfo.setId(UserHolder.getUserId());
        userInfoService.update(userInfo);
        return ResponseEntity.ok(null);
    }
}
