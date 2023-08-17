package com.wsj.mongo.apiImpl;

import cn.hutool.core.collection.CollUtil;
import com.wsj.dubbo.api.RecommendUserApi;
import com.wsj.mongo.RecommendUser;
import com.wsj.mongo.UserLike;
import com.wsj.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Collections;
import java.util.List;

@DubboService
public class RecommendUserApiImpl implements RecommendUserApi {
    @Autowired
    private MongoTemplate mongoTemplate;

    //查询今日佳人
    @Override
    public RecommendUser queryWithMaxScore(Long toUserId) {
        //根据toUserId查询，根据评分score排序，获取第一条
        //构建Criteria
        Criteria criteria = Criteria.where("userId").is(toUserId);
        //构建Query对象
        Query query = Query.query(criteria)
                .with(Sort.by(Sort.Order.desc("score")))
                .limit(1);
        //调用mongoTemplate查询
        return mongoTemplate.findOne(query, RecommendUser.class);
    }

    @Override
    public PageResult queryRecommendUserList(Integer page, Integer pageSize, Long toUserId) {
        //1、构建Criteria对象
        Criteria criteria = Criteria.where("toUserId").is(toUserId);
        //2、创建Query对象
        Query query = Query.query(criteria)
                .with(Sort.by(Sort.Order.desc("score")))
                .limit(pageSize)
                .skip((long) (page - 1) * pageSize);
        //3、调用mongoTemplate查询
        List<RecommendUser> list = mongoTemplate.find(query, RecommendUser.class);
        Long count = mongoTemplate.count(query, RecommendUser.class);
        //4、构建返回值PageResult
        return new PageResult(page, pageSize, count, list);
    }

    /**
     * 查询用户之间的推荐信息
     */
    @Override
    public RecommendUser queryByUserId(Long userId, Long toUserId) {
        Criteria criteria = Criteria.where("toUserId").is(toUserId)
                .and("userId").is(userId);
        Query query = Query.query(criteria);
        RecommendUser user = mongoTemplate.findOne(query, RecommendUser.class);
        if(user == null){
            user = new RecommendUser();
            user.setUserId(userId);
            user.setToUserId(toUserId);
            //构建缘分值
            user.setScore(90d);
        }
        return user;
    }

    @Override
    public List<RecommendUser> queryCardsList(Long userId, int count) {
        //1、查询喜欢不喜欢的用户ID
        List<UserLike> likeList = mongoTemplate.find(Query.query(Criteria.where("userId").is(userId)), UserLike.class);
        //2、构造查询推荐用户的条件
        List<Long> likeUserIds = CollUtil.getFieldValues(likeList,"likeUserId", Long.class);
        Criteria criteria = Criteria.where("toUserId").is(userId).and("userId").nin(likeUserIds);
        //3、使用统计函数，随机获取推荐的用户列表  //指定查询条件
        TypedAggregation<RecommendUser> newAggregation = TypedAggregation.newAggregation(RecommendUser.class,
                Aggregation.match(criteria),Aggregation.sample(count));
        AggregationResults<RecommendUser> results = mongoTemplate.aggregate(newAggregation, RecommendUser.class);
        //4、构造返回
        return results.getMappedResults();
    }
}
