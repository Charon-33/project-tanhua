package com.wsj.dubbo.apiImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wsj.domain.BlackList;
import com.wsj.dubbo.api.BlackListApi;
import com.wsj.dubbo.mapper.BlackListMapper;
import com.wsj.dubbo.mapper.UserInfoMapper;
import org.apache.dubbo.config.annotation.DubboService;
import javax.annotation.Resource;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wsj.domain.UserInfo;

@DubboService
public class BlackListApiImpl implements BlackListApi {
    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private BlackListMapper blackListMapper;

    @Override
    public IPage<UserInfo> findByUserId(Long userId, int page, int size) {
        Page pages = new Page(page,size);
        return userInfoMapper.findBlackList(pages,userId);
    }

    @Override
    public void delete(Long userId, Long blackUserId) {
        QueryWrapper<BlackList> qw = new QueryWrapper<>();
        qw.eq("user_id",userId);
        qw.eq("black_user_id",blackUserId);
        blackListMapper.delete(qw);
    }
}
