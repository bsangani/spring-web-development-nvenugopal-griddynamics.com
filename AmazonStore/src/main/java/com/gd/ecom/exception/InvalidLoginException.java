package com.gd.ecom.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


public class InvalidLoginException extends BaseApiException {
    public InvalidLoginException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}

