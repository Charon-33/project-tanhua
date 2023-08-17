package com.wsj.dubbo.apiImpl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wsj.domain.UserInfo;
import com.wsj.dubbo.api.UserInfoApi;
import com.wsj.dubbo.mapper.UserInfoMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@DubboService
public class UserInfoApiImpl implements UserInfoApi {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public void save(UserInfo userInfo) {
        userInfoMapper.insert(userInfo);
    }

    @Override
    public void update(UserInfo userInfo) {
        userInfoMapper.updateById(userInfo);
    }

    @Override
    public UserInfo findById(Long id) {
        return userInfoMapper.selectById(id);
    }

    @Override
    public Map<Long, UserInfo> findByIds(List<Long> ids, UserInfo userInfo) {
        QueryWrapper qw = new QueryWrapper();
        //1、用户id列表
        qw.in("id", ids);
        //2、添加筛选条件
        if (userInfo != null) {
            if (userInfo.getAge() != null) {
                qw.lt("age", userInfo.getAge());
            }
            if (!StringUtils.isEmpty(userInfo.getGender())) {
                qw.eq("gender", userInfo.getGender());
            }
        }
        List<UserInfo> list = userInfoMapper.selectList(qw);
        Map<Long, UserInfo> map = CollUtil.fieldValueMap(list, "id");
        return map;
    }


}
