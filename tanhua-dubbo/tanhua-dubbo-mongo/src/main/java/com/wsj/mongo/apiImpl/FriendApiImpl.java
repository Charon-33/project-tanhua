package com.wsj.mongo.apiImpl;

import com.wsj.dubbo.api.FriendApi;
import com.wsj.mongo.Friend;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@DubboService
public class FriendApiImpl implements FriendApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(Long userId, Long friendId) {
        //1、保存自己的好友数据
        Query query1 = Query.query(Criteria.where("userId").is(userId)
                .and("friendId").is(friendId));
        //1.1 判断好友关系是否存在
        if (!mongoTemplate.exists(query1, Friend.class)) {
            //1.2 如果不存在，保存
            Friend friend1 = new Friend();
            friend1.setUserId(userId);
            friend1.setFriendId(friendId);
            friend1.setCreated(System.currentTimeMillis());
            mongoTemplate.save(friend1);
        }
        //2、保存好友的数据
        Query query2 = Query.query(Criteria.where("userId").is(friendId)
                .and("friendId").is(userId));
        //2.1 判断好友关系是否存在
        if (!mongoTemplate.exists(query2, Friend.class)) {
            //2.2 如果不存在，保存
            Friend friend2 = new Friend();
            friend2.setUserId(friendId);
            friend2.setFriendId(userId);
            friend2.setCreated(System.currentTimeMillis());
            mongoTemplate.save(friend2);
        }

    }

    //查询好友列表
    @Override
    public List<Friend> findByUserId(Long userId, Integer page, Integer pagesize) {
        Criteria criteria = Criteria.where("userId").is(userId);
        Query query = Query.query(criteria).skip((page - 1) * pagesize)
                .limit(pagesize)
                .with(Sort.by(Sort.Order.desc("created")));
        return mongoTemplate.find(query, Friend.class);
    }
}
