package com.workitech.s19.challenge.dto.comment;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CommentRequestDTO(@NotBlank @Size(max = 250) String commentText, LocalDateTime time) {
}
