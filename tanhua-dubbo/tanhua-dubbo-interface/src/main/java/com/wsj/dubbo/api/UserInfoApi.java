package com.wsj.dubbo.api;

import com.wsj.domain.UserInfo;

import java.util.List;
import java.util.Map;

public interface UserInfoApi {
    void save(UserInfo userInfo);

    void update(UserInfo userInfo);

    UserInfo findById(Long id);

    Map<Long, UserInfo> findByIds(List<Long> ids, UserInfo userInfo);
}
