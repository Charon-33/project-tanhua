package com.wsj.server.service.impl;

import com.wsj.domain.User;
import com.wsj.dubbo.api.UserApi;
import com.wsj.server.interceptor.UserHolder;
import com.wsj.server.service.HuanXinService;
import com.wsj.vo.HuanXinUserVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
public class HuanXinServiceImpl implements HuanXinService {

    @DubboReference
    private UserApi userApi;

    @Override
    public HuanXinUserVo findHuanXinUser() {
        Long userId = UserHolder.getUserId();
        User user = userApi.findById(userId);

        if(user != null){
            return new HuanXinUserVo(user.getHxUser(),user.getHxPassword());
        }

        return null;
    }
}
