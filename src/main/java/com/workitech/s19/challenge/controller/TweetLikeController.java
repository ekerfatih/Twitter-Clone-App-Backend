package com.workitech.s19.challenge.controller;

import com.workitech.s19.challenge.dto.like.LikeResponse;
import com.workitech.s19.challenge.dto.retweet.RetweetResponse;
import com.workitech.s19.challenge.service.TweetLikeService;
import com.workitech.s19.challenge.service.TweetRetweetService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
public class TweetLikeController {

    private final TweetLikeService tls;

    @PostMapping("/{id}")
    public ResponseEntity<LikeResponse> like(@Positive @PathVariable Long id) {
        LikeResponse lr = tls.like(id);
        return new ResponseEntity<>(lr, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<LikeResponse> unlike(@Positive @PathVariable Long id) {
        LikeResponse lr = tls.unlike(id);
        return new ResponseEntity<>(lr, HttpStatus.NO_CONTENT);
    }
}
