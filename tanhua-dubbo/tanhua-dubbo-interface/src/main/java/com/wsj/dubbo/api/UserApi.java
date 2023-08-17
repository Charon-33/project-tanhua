package com.wsj.dubbo.api;

import com.wsj.domain.User;

import java.util.List;

public interface UserApi {
    User findByMobile(String mobile);

    Long save(User user);

    void update(User user);

    List<User> findAll();

    User findById(Long userId);

    User findByHuanxin(String huanxinId);
}
