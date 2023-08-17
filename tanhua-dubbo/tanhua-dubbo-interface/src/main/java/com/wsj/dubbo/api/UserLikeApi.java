package com.wsj.dubbo.api;

public interface UserLikeApi {
    Boolean saveOrUpdate(Long userId, Long likeUserId, boolean isLike);
}
