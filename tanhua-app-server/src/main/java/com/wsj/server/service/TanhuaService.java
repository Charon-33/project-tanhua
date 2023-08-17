package com.wsj.server.service;

import com.wsj.dto.RecommendUserDto;
import com.wsj.vo.NearUserVo;
import com.wsj.vo.PageResult;
import com.wsj.vo.TodayBest;

import java.util.List;

public interface TanhuaService {
    TodayBest todayBest();

    PageResult recommendation(RecommendUserDto dto);

    TodayBest personalInfo(Long userId);

    String strangerQuestions(Long userId);

    void replyQuestions(Long userId, String reply);

    List<TodayBest> queryCardsList();

    void likeUser(Long likeUserId);
    void notLikeUser(Long likeUserId);

    List<NearUserVo> queryNearUser(String gender, String distance);
}
