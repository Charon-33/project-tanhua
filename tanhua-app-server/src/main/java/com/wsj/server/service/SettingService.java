package com.wsj.server.service;


import com.wsj.vo.PageResult;
import com.wsj.vo.SettingsVo;

import java.util.Map;

public interface SettingService {
    PageResult blacklist(int page, int size);

    void deleteBlackList(Long blackUserId);

    void saveQuestion(String content);

    SettingsVo settings();

    void saveSettings(Map<String, Object> map);
}
