package com.gd.ecom.exception;

import org.springframework.http.HttpStatus;

public class ProductServiceException extends BaseApiException {
    public ProductServiceException(String message, HttpStatus httpStatus) {
        super(message,httpStatus);
    }
}

