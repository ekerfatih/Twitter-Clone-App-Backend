package com.workitech.s19.challenge.service;

import com.workitech.s19.challenge.dto.retweet.RetweetResponse;

public interface TweetRetweetService {
    RetweetResponse retweet(Long tweetId);

    RetweetResponse cancelRetweet(Long tweetId);

}
