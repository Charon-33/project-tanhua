package com.wsj.dubbo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wsj.domain.UserInfo;

public interface UserInfoMapper extends BaseMapper<UserInfo> {

    IPage<UserInfo> findBlackList(Page pages, Long userId);
}
