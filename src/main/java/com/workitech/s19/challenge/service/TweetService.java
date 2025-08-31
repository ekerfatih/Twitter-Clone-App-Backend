package com.workitech.s19.challenge.service;

import com.workitech.s19.challenge.dto.tweet.TweetPatchRequestDto;
import com.workitech.s19.challenge.dto.tweet.TweetRequestDto;
import com.workitech.s19.challenge.dto.tweet.TweetResponseDto;

import java.util.List;

public interface TweetService {

    List<TweetResponseDto> getAll();

    TweetResponseDto get(Long id);

    List<TweetResponseDto> getAllByUser(String username);

    List<TweetResponseDto> getAllCurrentUserTweets();

    TweetResponseDto create(TweetRequestDto tweetRequestDto);

    TweetResponseDto replaceOrCreate(Long id, TweetRequestDto tweetRequestDto);

    TweetResponseDto update(Long id, TweetPatchRequestDto tweetPatchRequestDto);

    void delete(Long id);

}
