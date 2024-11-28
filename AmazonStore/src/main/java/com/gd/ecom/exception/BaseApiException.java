package com.gd.ecom.exception;

import org.springframework.http.HttpStatus;

public class BaseApiException extends RuntimeException {

    public HttpStatus httpStatus;

    public BaseApiException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
