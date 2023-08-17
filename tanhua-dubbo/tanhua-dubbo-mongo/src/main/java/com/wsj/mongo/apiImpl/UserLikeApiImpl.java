package com.wsj.mongo.apiImpl;

import com.wsj.dubbo.api.UserLikeApi;
import com.wsj.mongo.UserLike;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.CodecRegistryProvider;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@DubboService
public class UserLikeApiImpl implements UserLikeApi {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public Boolean saveOrUpdate(Long userId, Long likeUserId, boolean isLike) {
        try {
            Query query = Query.query(Criteria.where("userId").is(userId).and("likeUserId").is(likeUserId));
            UserLike userLike = mongoTemplate.findOne(query, UserLike.class);
            if(userLike == null){
                userLike = new UserLike();
                userLike.setUserId(userId);
                userLike.setLikeUserId(likeUserId);
                userLike.setCreated(System.currentTimeMillis());
                userLike.setUpdated(System.currentTimeMillis());
                userLike.setIsLike(isLike);
                mongoTemplate.save(userLike);
            }else{
                Update update = Update.update("isLike",isLike).set("update",System.currentTimeMillis());
                mongoTemplate.updateFirst(query,update, UserLike.class);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
