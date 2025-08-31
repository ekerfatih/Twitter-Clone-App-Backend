package com.workitech.s19.challenge.dto.comment;

import java.time.LocalDateTime;

public record CommentResponseDTO(
        Long id,
        String commentText,
        String username,
        LocalDateTime time
) {}
