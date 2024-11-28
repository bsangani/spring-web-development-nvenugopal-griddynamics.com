package com.gd.ecom.exception;

import org.springframework.http.HttpStatus;

public class EmptyCartException extends BaseApiException {
    public EmptyCartException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
