package com.wsj.dubbo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wsj.domain.User;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {

    List<User> findAll();
}
