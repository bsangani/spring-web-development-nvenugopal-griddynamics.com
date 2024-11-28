package com.gd.ecom.exception;

import org.springframework.http.HttpStatus;

public class InsufficientStockException extends BaseApiException {
    public InsufficientStockException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
