package com.gd.ecom.records;

public record ErrorResponse(int status, String message, long timestamp) {
}
