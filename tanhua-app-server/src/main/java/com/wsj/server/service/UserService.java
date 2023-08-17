package com.wsj.server.service;

import java.util.Map;

public interface UserService {
    void sendCheckCode(String phone);

    Map<String,Object> loginVerification(String phone, String code);
}
