package com.wsj.api;

import com.wsj.domain.UserInfo;
import com.wsj.mapper.UserInfoMapper;

import javax.annotation.Resource;

public class UserInfoApiImpl implements UserInfoApi {

    @Resource
    private UserInfoMapper userInfoMapper;


    @Override
    public void save(UserInfo userInfo) {
        userInfoMapper.insert(userInfo);
    }

    @Override
    public void update(UserInfo userInfo) {
        userInfoMapper.updateById(userInfo);
    }


}
