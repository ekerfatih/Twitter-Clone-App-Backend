package com.workitech.s19.challenge.dto.tweet;

import java.time.LocalDateTime;

public record TweetResponseDto(
        String tweetText,
        String username,
        Long id,
        LocalDateTime time,
        long commentCount,
        long likeCount,
        long retweetCount,
        boolean likedByMe,
        boolean retweetByMe
) {
}
