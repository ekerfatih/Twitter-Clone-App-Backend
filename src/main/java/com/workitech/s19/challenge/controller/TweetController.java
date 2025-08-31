package com.workitech.s19.challenge.controller;

import com.workitech.s19.challenge.dto.tweet.TweetPatchRequestDto;
import com.workitech.s19.challenge.dto.tweet.TweetRequestDto;
import com.workitech.s19.challenge.dto.tweet.TweetResponseDto;
import com.workitech.s19.challenge.service.TweetService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tweet")
@RequiredArgsConstructor
public class TweetController {

    private final TweetService tweetService;

    @GetMapping
    public List<TweetResponseDto> getAll() {
        return tweetService.getAll();
    }

    @GetMapping("/{id}")
    public TweetResponseDto get(@Positive @PathVariable("id") Long id) {
        return tweetService.get(id);
    }

    @PutMapping("/{id}")
    public TweetResponseDto replaceOrCreate(@Positive @PathVariable Long id, @Validated @RequestBody TweetRequestDto tweetRequestDto) {
        return tweetService.replaceOrCreate(id, tweetRequestDto);
    }

    @PatchMapping("/{id}")
    public TweetResponseDto update(@Positive @PathVariable Long id, @Validated @RequestBody TweetPatchRequestDto tweetPatchRequestDto) {
        return tweetService.update(id, tweetPatchRequestDto);
    }

    @GetMapping("/user/{username}")
    public List<TweetResponseDto> getProfile(@PathVariable String username) {
        return tweetService.getAllByUser(username);
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public TweetResponseDto create(@Validated @RequestBody TweetRequestDto tweetRequestDto) {
        return tweetService.create(tweetRequestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Positive @PathVariable("id") Long id) {
        tweetService.delete(id);
    }
}
