package com.workitech.s19.challenge.service;

import com.workitech.s19.challenge.dto.retweet.RetweetResponse;
import com.workitech.s19.challenge.entity.Tweet;
import com.workitech.s19.challenge.entity.TweetRetweet;
import com.workitech.s19.challenge.entity.User;
import com.workitech.s19.challenge.exceptions.TwitterNotFoundException;
import com.workitech.s19.challenge.repository.TweetRepository;
import com.workitech.s19.challenge.repository.TweetRetweetRepository;
import com.workitech.s19.challenge.repository.UserRepository;
import com.workitech.s19.challenge.util.UserUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TweetRetweetServiceImpl implements TweetRetweetService {

    private final TweetRetweetRepository tweetRetweetRepository;
    private final UserRepository userRepository;
    private final TweetRepository tweetRepository;

    @Override
    @Transactional
    public RetweetResponse retweet(Long tweetId) {
        User user = UserUtil.getUser(userRepository);

        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new TwitterNotFoundException("The tweet with id: " + tweetId + " does not exist"));


        if (tweetRetweetRepository.existsByTweetIdAndUserId(tweetId, user.getId())) {
            long countExists = tweetRetweetRepository.countByTweetId(tweetId);
            return new RetweetResponse(tweetId, true, countExists);
        }

        TweetRetweet entity = new TweetRetweet();
        entity.setTweet(tweet);
        entity.setUser(user);
        entity.setCreatedAt(LocalDateTime.now());

        try {
            tweetRetweetRepository.saveAndFlush(entity);
        } catch (DataIntegrityViolationException ignored) {

        }

        long count = tweetRetweetRepository.countByTweetId(tweetId);
        return new RetweetResponse(tweetId, true, count);
    }

    @Override
    @Transactional
    public RetweetResponse cancelRetweet(Long tweetId) {
        User user = UserUtil.getUser(userRepository);

        Tweet t = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new TwitterNotFoundException("The tweet with id: " + tweetId + " does not exist"));
      
        tweetRetweetRepository.deleteByTweetAndUser(tweetId, user.getId());

        long count = tweetRetweetRepository.countByTweetId(tweetId);
        return new RetweetResponse(tweetId, false, count);
    }
}
