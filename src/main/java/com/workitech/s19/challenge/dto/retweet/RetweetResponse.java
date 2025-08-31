package com.workitech.s19.challenge.dto.retweet;


public record RetweetResponse(Long tweetId, boolean retweet, long retweetCount) {
}