package com.wsj.server.controller;

import com.wsj.server.service.SettingService;
import com.wsj.vo.PageResult;
import com.wsj.vo.SettingsVo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class SettingController {
    @Resource
    private SettingService settingService;

    /**
     * 查询通用设置
     */
    @GetMapping("/settings")
    public ResponseEntity settings(){
        SettingsVo vo = settingService.settings();
        return ResponseEntity.ok(vo);
    }

    /**
     * 通知设置
     */
    @PostMapping("/notifications/setting")
    public ResponseEntity notification(@RequestBody Map<String,Object> map){
        settingService.saveSettings(map);
        return ResponseEntity.ok(null);
    }

    /**
     * 设置陌生人问题
     */
    @PostMapping("/questions")
    public ResponseEntity question(@RequestBody Map<String,Object> map){
        String content = (String) map.get("content");
        settingService.saveQuestion(content);
        return ResponseEntity.ok(null);
    }

    /**
     * 分页查询黑名单列表
     */
    @GetMapping("/blacklist")
    public ResponseEntity blacklist(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        PageResult pr = settingService.blacklist(page,size);
        return ResponseEntity.ok(pr);
    }

    /**
     * 取消黑名单
     */
    @DeleteMapping("/blacklist/{uid}")
    public ResponseEntity deleteBlackList(@PathVariable("uid") Long blackUserId){
        settingService.deleteBlackList(blackUserId);
        return ResponseEntity.ok(null);
    }
}
