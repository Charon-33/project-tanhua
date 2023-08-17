package com.wsj.server.controller;

import cn.hutool.core.lang.Range;
import com.wsj.dto.RecommendUserDto;
import com.wsj.server.service.TanhuaService;
import com.wsj.vo.PageResult;
import com.wsj.vo.TodayBest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/tanhua")
public class TanhuaController {

    @Autowired
    private TanhuaService tanhuaService;

    //今日佳人
    @GetMapping("/todayBest")
    public ResponseEntity todayBest(){
        TodayBest vo = tanhuaService.todayBest();
        return ResponseEntity.ok(vo);
    }

    @GetMapping("/recommendation")
    public ResponseEntity recommendation(RecommendUserDto dto){
        PageResult pr = tanhuaService.recommendation(dto);
        return ResponseEntity.ok(pr);
    }

    /**
     * 查询佳人信息
     */
    @GetMapping("/{id}/personalInfo")
    public ResponseEntity personalInfo(@PathVariable("id") Long userId){
        TodayBest best = tanhuaService.personalInfo(userId);
        return ResponseEntity.ok(best);
    }

    /**
     * 查看陌生人问题
     */
    @GetMapping("/strangerQuestions")
    public ResponseEntity strangetQuestions(Long userId){
        String questions = tanhuaService.strangerQuestions(userId);
        return ResponseEntity.ok(questions);
    }

    /**
     * 回复陌生人问题
     */
    @PostMapping("/strangerQuestions")
    public ResponseEntity replyQuestions(@RequestBody Map map){
        String obj = map.get("userId").toString();
        Long userId = Long.valueOf(obj);
        String reply = map.get("reply").toString();
        tanhuaService.replyQuestions(userId,reply);
        return ResponseEntity.ok(null);
    }

    /**
     * 探花-推荐用户列表
     */
    @GetMapping("/cards")
    public ResponseEntity queryCardsList(){
        List<TodayBest> list = this.tanhuaService.queryCardsList();
        return ResponseEntity.ok(list);
    }

    /**
     * 喜欢
     */
    @GetMapping("{id}/love")
    public ResponseEntity likeUser(@PathVariable("id") Long likeUserId){
        tanhuaService.likeUser(likeUserId);
        return ResponseEntity.ok(null);
    }

    /**
     * 不喜欢
     */
    @GetMapping("{id}/unlove")
    public ResponseEntity notLikeUser(@PathVariable("id") Long likeUserId){
        tanhuaService.notLikeUser(likeUserId);
        return ResponseEntity.ok(null);
    }
}
