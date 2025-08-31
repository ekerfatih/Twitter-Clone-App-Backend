package com.workitech.s19.challenge.controller;

import com.workitech.s19.challenge.dto.retweet.RetweetResponse;
import com.workitech.s19.challenge.service.TweetRetweetService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/retweet")
@RequiredArgsConstructor
public class TweetRetweetController {

    private final TweetRetweetService trs;

    @PostMapping("/{id}")
    public ResponseEntity<RetweetResponse> retweet(@Positive @PathVariable Long id) {
        RetweetResponse rr = trs.retweet(id);
        return new ResponseEntity<>(rr, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RetweetResponse> cancelRetweet(@Positive @PathVariable Long id) {
        RetweetResponse rr = trs.cancelRetweet(id);
        return new ResponseEntity<>(rr, HttpStatus.NO_CONTENT);
    }
}
