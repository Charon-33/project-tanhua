package com.wsj.dubbo.api;

import com.wsj.enums.CommentType;
import com.wsj.mongo.Comment;

import java.util.List;

public interface CommentApi {

    //分页查询
    List<Comment> findComments(String movementId, CommentType commentType, Integer page, Integer pagesize);

    //发布评论，并获取评论数量
    Integer save(Comment comment);

    /**
     * 判断comment数据是否存在
     */
    Boolean hasComment(String movementId, Long userId, CommentType commentType);

    //删除comment数据
    Integer delete(Comment comment);
}