package com.workitech.s19.challenge.service;

import com.workitech.s19.challenge.dto.like.LikeResponse;
import com.workitech.s19.challenge.entity.TweetLike;
import com.workitech.s19.challenge.exceptions.TwitterNotFoundException;
import com.workitech.s19.challenge.repository.TweetLikeRepository;
import com.workitech.s19.challenge.repository.TweetRepository;
import com.workitech.s19.challenge.repository.UserRepository;
import com.workitech.s19.challenge.util.UserUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TweetLikeServiceImpl implements TweetLikeService {

    private final TweetLikeRepository likeRepository;
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public LikeResponse like(Long tweetId) {
        var user = UserUtil.getUser(userRepository);

        var tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new TwitterNotFoundException("Couldn't find the tweet"));

        if (likeRepository.existsByTweetIdAndUserId(tweetId, user.getId())) {
            long count = likeRepository.countByTweetId(tweetId);
            return new LikeResponse(tweetId, true, count);
        }

        TweetLike tl = new TweetLike();
        tl.setTweet(tweet);
        tl.setUser(user);
        tl.setCreatedAt(LocalDateTime.now());
        try {
            likeRepository.saveAndFlush(tl);
        } catch (DataIntegrityViolationException dup) {
        }

        long count = likeRepository.countByTweetId(tweetId);
        return new LikeResponse(tweetId, true, count);
    }

    @Override
    @Transactional
    public LikeResponse unlike(Long tweetId) {
        var user = UserUtil.getUser(userRepository);

        // tweet var mı?
        tweetRepository.findById(tweetId)
                .orElseThrow(() -> new TwitterNotFoundException("Couldn't find the tweet"));

        likeRepository.deleteByTweetAndUser(tweetId, user.getId());

        long count = likeRepository.countByTweetId(tweetId);
        return new LikeResponse(tweetId, false, count);
    }
}
