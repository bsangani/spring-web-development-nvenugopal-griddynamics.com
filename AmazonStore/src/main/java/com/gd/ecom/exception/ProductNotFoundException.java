package com.gd.ecom.exception;

import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends BaseApiException {
    public ProductNotFoundException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}


