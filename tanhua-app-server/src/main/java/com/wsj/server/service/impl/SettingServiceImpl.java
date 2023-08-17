package com.wsj.server.service.impl;


import com.wsj.dubbo.api.QuestionApi;
import com.wsj.dubbo.api.SettingsApi;
import com.wsj.domain.Question;
import com.wsj.domain.Settings;
import com.wsj.vo.PageResult;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wsj.dubbo.api.BlackListApi;
import com.wsj.domain.UserInfo;
import com.wsj.server.interceptor.UserHolder;
import com.wsj.server.service.SettingService;
import com.wsj.vo.SettingsVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SettingServiceImpl implements SettingService {

    @DubboReference
    private BlackListApi blackListApi;

    @DubboReference
    private QuestionApi questionApi;

    @DubboReference
    private SettingsApi settingsApi;

    //查询通用设置
    @Override
    public SettingsVo settings() {
        SettingsVo vo = new SettingsVo();
        //1、获取用户id
        Long userId = UserHolder.getUserId();
        vo.setId(userId);
        //2、获取用户的手机号码
        vo.setPhone(UserHolder.getMobile());
        //3、获取用户的陌生人问题
        Question question = questionApi.findByUserId(userId);
        String txt = question == null ? "你喜欢java吗？" : question.getTxt();
        vo.setStrangerQuestion(txt);
        //4、获取用户的APP通知开关数据
        Settings settings = settingsApi.findByUserId(userId);
        if (settings != null) {
            vo.setGonggaoNotification(settings.getGonggaoNotification());
            vo.setPinglunNotification(settings.getPinglunNotification());
            vo.setLikeNotification(settings.getLikeNotification());
        }
        return vo;
    }

    @Override
    public void saveSettings(Map<String, Object> map) {
        boolean likeNotification = (Boolean) map.get("likeNotification");
        boolean pinglunNotification = (Boolean) map.get("pinglunNotification");
        boolean gonggaoNotification = (Boolean) map.get("gonggaoNotification");
       //1、获取当前用户id
        Long userId = UserHolder.getUserId();
        //2、根据用户id，查询用户的通知设置
        Settings settings = settingsApi.findByUserId(userId);
        if (settings == null) {
            settings = new Settings();
            settings.setUserId(userId);
            settings.setPinglunNotification(pinglunNotification);
            settings.setLikeNotification(likeNotification);
            settings.setGonggaoNotification(gonggaoNotification);
            settingsApi.save(settings);
        }else{
            settings.setPinglunNotification(pinglunNotification);
            settings.setLikeNotification(likeNotification);
            settings.setGonggaoNotification(gonggaoNotification);
            settingsApi.update(settings);
        }
        settingsApi.save(settings);
    }

    // 分页查询黑名单列表
    @Override
    public PageResult blacklist(int page, int size) {
        //1、获取当前用户的id
        Long userId = UserHolder.getUserId();
        //2、调用API查询用户的黑名单分页列表  Ipage对象
        IPage<UserInfo> iPage = blackListApi.findByUserId(userId, page, size);
        //3、对象转化，将查询的Ipage对象的内容封装到PageResult中
        PageResult pr = new PageResult(page, size, iPage.getTotal(), iPage.getRecords());
        //4、返回
        return pr;
    }

    // 移除黑名单
    @Override
    public void deleteBlackList(Long blackUserId) {
        //1、获取当前用户id
        Long userId = UserHolder.getUserId();
        //2、调用api删除
        blackListApi.delete(userId, blackUserId);
    }

    // 保存陌生人问题
    @Override
    public void saveQuestion(String content) {
        //1、获取当前用户id
        Long userId = UserHolder.getUserId();
        //2、调用api查询当前用户的陌生人问题
        Question question = questionApi.findByUserId(userId);
        //3、判断问题是否存在
        if (question == null) {
            question = new Question();
            question.setUserId(userId);
            question.setTxt(content);
            questionApi.save(question);
        } else {
            //3.2 如果存在，更新
            question.setTxt(content);
            questionApi.update(question);
        }
    }


}
