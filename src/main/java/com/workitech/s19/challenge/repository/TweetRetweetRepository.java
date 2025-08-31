package com.workitech.s19.challenge.repository;

import com.workitech.s19.challenge.entity.TweetRetweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TweetRetweetRepository extends JpaRepository<TweetRetweet, Long> {

    boolean existsByTweetIdAndUserId(Long tweetId, Long userId);

    long countByTweetId(Long tweetId);

    @Modifying
    @Query("delete from TweetRetweet t where t.tweet.id=:tweetId and t.user.id=:userId")
    void deleteByTweetAndUser(@Param("tweetId") Long tweetId, @Param("userId") Long userId);
}
