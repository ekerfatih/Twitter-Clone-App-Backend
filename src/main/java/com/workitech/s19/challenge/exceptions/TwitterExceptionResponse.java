package com.workitech.s19.challenge.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TwitterExceptionResponse {
    private String message;
    private int statusCode;
    private LocalDateTime occurredTime;
}
