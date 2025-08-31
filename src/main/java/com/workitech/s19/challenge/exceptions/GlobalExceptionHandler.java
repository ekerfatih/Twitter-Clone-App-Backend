package com.workitech.s19.challenge.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler
    public ResponseEntity<TwitterExceptionResponse> handleException(TwitterException twitterException) {
        TwitterExceptionResponse response = new TwitterExceptionResponse(
                twitterException.getMessage(),
                twitterException.getHttpStatus().value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, twitterException.getHttpStatus());
    }

    @ExceptionHandler
    public ResponseEntity<TwitterExceptionResponse> handleException(Exception exception) {
        TwitterExceptionResponse response = new TwitterExceptionResponse(
                exception.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
