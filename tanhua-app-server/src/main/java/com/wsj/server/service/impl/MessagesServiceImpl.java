package com.wsj.server.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.wsj.domain.User;
import com.wsj.domain.UserInfo;
import com.wsj.dubbo.api.FriendApi;
import com.wsj.dubbo.api.UserApi;
import com.wsj.dubbo.api.UserInfoApi;
import com.wsj.mongo.Friend;
import com.wsj.server.exception.BusinessException;
import com.wsj.server.interceptor.UserHolder;
import com.wsj.server.service.MessagesService;
import com.wsj.template.HuanXinTemplate;
import com.wsj.utils.Constants;
import com.wsj.vo.ContactVo;
import com.wsj.vo.ErrorResult;
import com.wsj.vo.PageResult;
import com.wsj.vo.UserInfoVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MessagesServiceImpl implements MessagesService {
    @DubboReference
    private UserApi userApi;
    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private FriendApi friendApi;

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    @Override
    public UserInfoVo findUserInfoByHuanxin(String huanxinId) {
        User user = userApi.findByHuanxin(huanxinId);

        UserInfo userInfo = userInfoApi.findById(user.getId());

        UserInfoVo vo = new UserInfoVo();
        BeanUtils.copyProperties(userInfo, vo);//copy同名同类型的属性
        if (userInfo.getAge() != null) {
            vo.setAge(userInfo.getAge().toString());
        }
        return vo;
    }

    /**
     * 添加联系人
     */
    @Override
    public void contacts(Long friendId) {
        //1、将好友关系注册到环信
        Boolean aBoolean = huanXinTemplate.addContact(Constants.HX_USER_PREFIX + UserHolder.getUserId(),
                Constants.HX_USER_PREFIX + friendId);
        if (!aBoolean) {
            throw new BusinessException(ErrorResult.error());
        }
        //2、如果注册成功，记录好友关系到mongodb
        friendApi.save(UserHolder.getUserId(), friendId);
    }

    //分页查询联系人列表
    @Override
    public PageResult findFriends(Integer page, Integer pagesize, String keyword) {
        //1、调用API查询当前用户的好友数据 -- List<Friend>
        List<Friend> friendList = friendApi.findByUserId(UserHolder.getUserId(), page, pagesize);
        if (CollUtil.isEmpty(friendList)) {
            return new PageResult();
        }
        //2、提取数据列表中的好友id
        List<Long> userIds = CollUtil.getFieldValues(friendList, "friendId", Long.class);
        //3、调用UserInfoAPI查询好友的用户详情
        UserInfo info = new UserInfo();
        info.setNickname(keyword);
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, info);
        //4、构造VO对象
        List<ContactVo> vos = new ArrayList<>();
        for (Friend friend : friendList) {
            UserInfo userInfo = map.get(friend.getFriendId());
            if (userInfo != null) {
                ContactVo vo = ContactVo.init(userInfo);
                vos.add(vo);
            }
        }
        return new PageResult(page, pagesize, 0L, vos);
    }
}
