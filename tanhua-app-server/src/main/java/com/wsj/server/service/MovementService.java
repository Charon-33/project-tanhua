package com.wsj.server.service;

import com.wsj.mongo.Movement;
import com.wsj.vo.MovementsVo;
import com.wsj.vo.PageResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MovementService {
    void publishMovement(Movement movement, MultipartFile[] imageContent) throws IOException;

    PageResult findByUserId(Long userId, Integer page, Integer pagesize);

    PageResult findFriendMovements(Integer page, Integer pagesize);

    PageResult findRecommendMovements(Integer page, Integer pagesize);

    MovementsVo findById(String movementId);
}
