package com.wsj.dubbo.api;

import com.wsj.mongo.Movement;
import com.wsj.vo.PageResult;

import java.util.List;

public interface MovementApi {
    //发布动态
    void publish(Movement movement);

    //根据用户id，查询此用户发布的动态数据列表
    PageResult findByUserId(Long userId, Integer page, Integer pageSize);

    List<Movement> findFriendMovements(Integer page, Integer pagesize, Long friendId);

    List<Movement> randomMoveMents(Integer counts);

    List<Movement> findMovementsByPids(List<Long> pids);

    Movement findById(String movementId);

}
