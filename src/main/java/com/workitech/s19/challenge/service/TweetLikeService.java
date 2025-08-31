package com.workitech.s19.challenge.service;

import com.workitech.s19.challenge.dto.like.LikeResponse;

public interface TweetLikeService {
    LikeResponse like(Long tweedId);

    LikeResponse unlike(Long tweedId);

}
