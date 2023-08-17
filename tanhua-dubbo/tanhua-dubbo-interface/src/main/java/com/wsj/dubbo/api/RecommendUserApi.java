package com.wsj.dubbo.api;

import com.wsj.mongo.RecommendUser;
import com.wsj.vo.PageResult;

import java.util.List;

public interface RecommendUserApi {
    RecommendUser queryWithMaxScore(Long toUserId);

    PageResult queryRecommendUserList(Integer page, Integer pageSize, Long toUserId);

    RecommendUser queryByUserId(Long userId, Long userId1);

    List<RecommendUser> queryCardsList(Long userId, int count);
}
