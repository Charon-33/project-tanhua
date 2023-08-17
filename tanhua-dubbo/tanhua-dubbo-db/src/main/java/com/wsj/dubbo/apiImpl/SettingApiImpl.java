package com.wsj.dubbo.apiImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wsj.domain.Settings;
import com.wsj.dubbo.api.SettingsApi;
import com.wsj.dubbo.mapper.SettingMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class SettingApiImpl implements SettingsApi {

    @Resource
    private SettingMapper settingMapper;

    @Override
    public Settings findByUserId(Long userId) {

        QueryWrapper<Settings> qw = new QueryWrapper<>();
        qw.eq("user_id",userId);
        return settingMapper.selectOne(qw);
    }

    @Override
    public void save(Settings settings) {
        settingMapper.insert(settings);
    }

    @Override
    public void update(Settings settings) {
        settingMapper.updateById(settings);
    }
}
