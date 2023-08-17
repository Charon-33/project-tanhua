package com.wsj.dubbo.apiImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wsj.domain.User;
import com.wsj.dubbo.api.UserApi;
import com.wsj.dubbo.mapper.UserMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;

@DubboService
public class UserApiImpl implements UserApi {

    @Resource
    private UserMapper userMapper;

    @Override
    public User findByMobile(String mobile) {
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("mobile", mobile);
        return userMapper.selectOne(qw);
    }

    @Override
    public Long save(User user) {
        userMapper.insert(user);
        return user.getId();
    }

    @Override
    public void update(User user) {
        userMapper.updateById(user);
    }

    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }

    @Override
    public User findById(Long userId) {
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("id", userId);
        return userMapper.selectOne(qw);
    }

    @Override
    public User findByHuanxin(String huanxinId) {
        QueryWrapper qw = new QueryWrapper<>();
        qw.eq("hx_user", huanxinId);
        return userMapper.selectOne(qw);
    }
}
