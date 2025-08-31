package com.workitech.s19.challenge.exceptions;

import org.springframework.http.HttpStatus;

public class UniqueKeyTwitterException extends TwitterException {

    public UniqueKeyTwitterException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
