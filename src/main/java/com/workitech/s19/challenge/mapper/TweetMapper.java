package com.workitech.s19.challenge.mapper;

import com.workitech.s19.challenge.dto.tweet.TweetPatchRequestDto;
import com.workitech.s19.challenge.dto.tweet.TweetRequestDto;
import com.workitech.s19.challenge.dto.tweet.TweetResponseDto;
import com.workitech.s19.challenge.entity.Tweet;
import com.workitech.s19.challenge.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TweetMapper {
    public Tweet toEntity(TweetRequestDto tweetRequestDto, User user) {
        Tweet tweet = new Tweet();
        tweet.setTweetText(tweetRequestDto.tweetText());
        tweet.setUser(user);
        tweet.setTime(LocalDateTime.now());
        return tweet;
    }

    public TweetResponseDto toResponseDto(Tweet tweet, User currentUser) {
        return new TweetResponseDto(
                tweet.getTweetText(),
                tweet.getUser().getUsername(),
                tweet.getId(), tweet.getTime(),
                tweet.getComments().size(),
                tweet.getLikeCount(),
                tweet.getRetweetCount(),
                tweet.isLikedBy(currentUser),
                tweet.isRetweetedBy(currentUser));
    }

    public Tweet updateTweet(Tweet tweetToUpdate, TweetPatchRequestDto tweetPatchRequestDto) {
        if (tweetToUpdate.getTweetText() != null) {
            tweetToUpdate.setTweetText(tweetPatchRequestDto.tweetText());
        }
        return tweetToUpdate;
    }
}
