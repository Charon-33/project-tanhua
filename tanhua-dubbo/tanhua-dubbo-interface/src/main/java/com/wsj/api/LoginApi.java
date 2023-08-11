package com.wsj.api;

import com.wsj.domain.User;

public interface LoginApi {
    User findByMobile(String mobile);

    Long save(User user);
}
