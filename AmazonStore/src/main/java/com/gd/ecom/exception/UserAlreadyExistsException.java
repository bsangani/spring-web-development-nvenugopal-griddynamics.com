package com.gd.ecom.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends BaseApiException {
    public UserAlreadyExistsException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}