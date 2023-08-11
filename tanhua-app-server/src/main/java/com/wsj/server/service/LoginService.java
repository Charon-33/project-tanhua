package com.wsj.server.service;

import java.util.Map;

public interface LoginService {
    void sendCheckCode(String phone);

    Map<String,Object> loginVerification(String phone, String code);
}
