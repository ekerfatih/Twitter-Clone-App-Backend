package com.workitech.s19.challenge.service;

import com.workitech.s19.challenge.dto.tweet.TweetPatchRequestDto;
import com.workitech.s19.challenge.dto.tweet.TweetRequestDto;
import com.workitech.s19.challenge.dto.tweet.TweetResponseDto;
import com.workitech.s19.challenge.entity.Tweet;
import com.workitech.s19.challenge.entity.User;
import com.workitech.s19.challenge.exceptions.TwitterException;
import com.workitech.s19.challenge.exceptions.TwitterNotFoundException;
import com.workitech.s19.challenge.mapper.TweetMapper;
import com.workitech.s19.challenge.repository.TweetRepository;
import com.workitech.s19.challenge.repository.UserRepository;
import com.workitech.s19.challenge.util.UserUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {
    private final TweetRepository tweetRepository;
    private final TweetMapper tweetMapper;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public List<TweetResponseDto> getAll() {
        User current = UserUtil.getUser(userRepository);
        return tweetRepository.getAllByTimeDESC().stream()
                .map(t -> tweetMapper.toResponseDto(t, current))
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public TweetResponseDto get(Long id) {
        User current = UserUtil.getUser(userRepository);
        Tweet t = tweetRepository.findById(id)
                .orElseThrow(() -> new TwitterNotFoundException("Tweet bulunamadı id: " + id));
        return tweetMapper.toResponseDto(t, current);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TweetResponseDto> getAllByUser(String username) {
        User current = UserUtil.getUser(userRepository);
        if (username == null || username.trim().isEmpty()) throw new IllegalArgumentException("Kullanıcı adı boş olamaz.");
        if (!userRepository.existsByUsername(username)) throw new EntityNotFoundException("Kullanıcı bulunamadı: " + username);
        return tweetRepository.getAllByUser(username).stream()
                .map(t -> tweetMapper.toResponseDto(t, current))
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<TweetResponseDto> getAllCurrentUserTweets() {
        User user = UserUtil.getUser(userRepository);
        return tweetRepository.getAllByUser(user.getUsername()).stream()
                .map(t -> tweetMapper.toResponseDto(t, user))
                .toList();
    }

    @Transactional
    @Override
    public TweetResponseDto create(TweetRequestDto dto) {
        User user = UserUtil.getUser(userRepository);
        Tweet tweet = tweetRepository.save(tweetMapper.toEntity(dto, user));
        return tweetMapper.toResponseDto(tweet, user);
    }

    @Transactional
    @Override
    public TweetResponseDto replaceOrCreate(Long id, TweetRequestDto dto) {
        User current = UserUtil.getUser(userRepository);
        return tweetRepository.findById(id).map(existing -> {
            if (!existing.getUser().getId().equals(current.getId())) throw new AccessDeniedException("Only owner can update this tweet");
            Tweet updated = tweetMapper.toEntity(dto, current);
            updated.setId(id);
            return tweetMapper.toResponseDto(tweetRepository.save(updated), current);
        }).orElseGet(() -> {
            Tweet created = tweetMapper.toEntity(dto, current);
            return tweetMapper.toResponseDto(tweetRepository.save(created), current);
        });
    }

    @Transactional
    @Override
    public TweetResponseDto update(Long id, TweetPatchRequestDto dto) {
        User current = UserUtil.getUser(userRepository);
        Tweet t = tweetRepository.findById(id).orElseThrow(() -> new TwitterNotFoundException("Twit bulunamadı id: " + id));
        if (!t.getUser().getId().equals(current.getId())) throw new AccessDeniedException("Only owner can update this tweet");
        t = tweetMapper.updateTweet(t, dto);
        return tweetMapper.toResponseDto(tweetRepository.save(t), current);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        User current = UserUtil.getUser(userRepository);
        Tweet t = tweetRepository.findById(id).orElseThrow(() -> new TwitterNotFoundException("Twit bulunamadı id: " + id));
        if (!t.getUser().getId().equals(current.getId())) throw new TwitterException("Only owner can delete this tweet", HttpStatus.FORBIDDEN);
        tweetRepository.hardDeleteById(id);
    }
}
