package com.wsj.server.service;

import com.wsj.domain.UserInfo;
import com.wsj.vo.UserInfoVo;
import org.springframework.web.multipart.MultipartFile;

public interface UserInfoService {
    void save(UserInfo userInfo);

    void updateHead(MultipartFile headPhoto, Long id);

    UserInfoVo findById(Long id);

    void update(UserInfo userInfo);
}
