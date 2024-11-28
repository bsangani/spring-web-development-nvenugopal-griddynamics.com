package com.gd.ecom.exception;

import org.springframework.http.HttpStatus;

public class SessionNotFoundException extends BaseApiException {
    public SessionNotFoundException(String message, HttpStatus httpStatus) {
        super(message,httpStatus);
    }
}

