package com.workitech.s19.challenge.dto.tweet;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record TweetRequestDto(
        @NotBlank
        @Size(max = 300)
        String tweetText,
        LocalDateTime time
) {
}
