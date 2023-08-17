package com.wsj.server.service;

import com.wsj.vo.PageResult;
import com.wsj.vo.UserInfoVo;

public interface MessagesService {
    UserInfoVo findUserInfoByHuanxin(String huanxinId);

    void contacts(Long friendId);

    PageResult findFriends(Integer page, Integer pagesize, String keyword);
}
