package com.wsj.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wsj.domain.User;
import com.wsj.mapper.LoginMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class LoginApiImpl implements LoginApi {

    @Autowired
    private LoginMapper userMapper;

    @Override
    public User findByMobile(String mobile) {
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("mobile",mobile);
        return userMapper.selectOne(qw);
    }

    @Override
    public Long save(User user) {
        userMapper.insert(user);
        return user.getId();
    }
}
