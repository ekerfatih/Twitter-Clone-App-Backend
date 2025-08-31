package com.workitech.s19.challenge.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter

public class TwitterNotFoundException extends TwitterException {
    public TwitterNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
