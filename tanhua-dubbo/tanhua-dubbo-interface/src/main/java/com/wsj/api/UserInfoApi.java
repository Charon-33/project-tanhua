package com.wsj.api;

import com.wsj.domain.UserInfo;

public interface UserInfoApi {
    void save(UserInfo userInfo);

    void update(UserInfo userInfo);
}
