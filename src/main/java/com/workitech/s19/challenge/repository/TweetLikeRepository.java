package com.workitech.s19.challenge.repository;

import com.workitech.s19.challenge.entity.TweetLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TweetLikeRepository extends JpaRepository<TweetLike, Long> {
    boolean existsByTweetIdAndUserId(Long tweetId, Long userId);

    long countByTweetId(Long tweetId);

    @Modifying
    @Query("delete from TweetLike l where l.tweet.id=:tweetId and l.user.id=:userId")
    void deleteByTweetAndUser(@Param("tweetId") Long tweetId, @Param("userId") Long userId);
}