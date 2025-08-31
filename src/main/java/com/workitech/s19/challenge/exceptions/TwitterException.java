package com.workitech.s19.challenge.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public class TwitterException extends RuntimeException {
    private HttpStatus httpStatus;

    public TwitterException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
