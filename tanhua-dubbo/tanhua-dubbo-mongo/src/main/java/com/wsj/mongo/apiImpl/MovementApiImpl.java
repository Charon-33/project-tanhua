package com.wsj.mongo.apiImpl;

import cn.hutool.core.collection.CollUtil;
import com.wsj.dubbo.api.MovementApi;
import com.wsj.mongo.Movement;
import com.wsj.mongo.MovementTimeLine;
import com.wsj.mongo.utils.IdWorker;
import com.wsj.mongo.utils.TimeLineService;
import com.wsj.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@DubboService
public class MovementApiImpl implements MovementApi {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private TimeLineService timeLineService;

    @Override
    public void publish(Movement movement) {
        try {
            //设置PID
            movement.setPid(idWorker.getNextId("movement"));
            //设置时间
            movement.setCreated(System.currentTimeMillis());
            //movement.setId(ObjectId.get());
            mongoTemplate.save(movement);
            //2、查询当前用户的好友数据
            //3、循环好友数据，构建时间线数据存入数据库
            timeLineService.saveTimeLine(movement.getUserId(), movement.getId());
        } catch (Exception e) {
            //忽略事务处理
            e.printStackTrace();
        }
    }

    @Override
    public PageResult findByUserId(Long userId, Integer page, Integer pageSize) {
        Criteria criteria = Criteria.where("userId").is(userId);
        Query query = Query.query(criteria).skip((page - 1) * pageSize)
                .limit(pageSize)
                .with(Sort.by(Sort.Order.desc("created")));
        List<Movement> movements = mongoTemplate.find(query, Movement.class);
        return new PageResult(page, pageSize, 0L, movements);
    }

    /**
     * 查询当前用户好友发布的所有动态
     *
     * @param friendId:当前操作用户id
     */
    @Override
    public List<Movement> findFriendMovements(Integer page, Integer pagesize, Long friendId) {
        //1、根据friendId查询时间线表
        Query query = Query.query(Criteria.where("firendId").is(friendId))
                .skip((page - 1) * pagesize).limit(pagesize)
                .with(Sort.by(Sort.Order.desc("created")));
        List<MovementTimeLine> lineList = mongoTemplate.find(query, MovementTimeLine.class);
        //2、提取动态id列表
        List<ObjectId> list = CollUtil.getFieldValues(lineList, "movementId", ObjectId.class);
        //3、根据动态id查询动态详情
        Query movementQuery = Query.query(Criteria.where("id").in(list));
        return mongoTemplate.find(movementQuery, Movement.class);
    }

    //根据pid查询
    @Override
    public List<Movement> findMovementsByPids(List<Long> pids) {
        Query query = Query.query(Criteria.where("pid").in(pids));
        return mongoTemplate.find(query, Movement.class);
    }

    //随机查询多条数据
    @Override
    public List<Movement> randomMoveMents(Integer counts) {
        //1、创建统计对象，设置统计参数
        TypedAggregation<Movement> aggregation = Aggregation.newAggregation(Movement.class, Aggregation.sample(counts));
        //2、调用mongoTemplate方法统计
        AggregationResults<Movement> results = mongoTemplate.aggregate(aggregation, Movement.class);
        //3、获取统计结果
        return results.getMappedResults();
    }

    @Override
    public Movement findById(String movementId){
        return mongoTemplate.findById(movementId, Movement.class);
    }
}
