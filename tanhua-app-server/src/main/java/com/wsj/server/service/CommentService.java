package com.wsj.server.service;

import com.wsj.vo.PageResult;

public interface CommentService {
    PageResult findComments(String movementId, Integer page, Integer pagesize);

    void saveComments(String movementId, String comment);

    Integer likeComment(String movementId);

    Integer disLikeComment(String movementId);

    Integer loveComment(String movementId);

    Integer disloveComment(String movementId);
}
