package com.wsj.server.service.impl;

import com.wsj.dubbo.api.UserInfoApi;
import com.wsj.domain.UserInfo;
import com.wsj.server.service.UserInfoService;
import com.wsj.vo.UserInfoVo;
import org.springframework.beans.BeanUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @DubboReference
    private UserInfoApi userInfoApi;

    @Override
    public void save(UserInfo userInfo) {
        userInfoApi.save(userInfo);
    }

    @Override
    public void updateHead(MultipartFile headPhoto, Long id) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setAvatar("https://avatars.dicebear.com/api/avataaars/tanhua"+id+".svg");
        userInfoApi.update(userInfo);
    }

    @Override
    public UserInfoVo findById(Long id) {
        UserInfo userInfo = userInfoApi.findById(id);

        UserInfoVo vo = new UserInfoVo();

        BeanUtils.copyProperties(userInfo,vo);

        if(userInfo.getAge() != null){
            vo.setAge(userInfo.getAge().toString());
        }

        return vo;
    }

    @Override
    public void update(UserInfo userInfo) {
        userInfoApi.update(userInfo);
    }
}
