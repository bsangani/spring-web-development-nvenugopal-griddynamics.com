package com.gd.ecom.records;

public record LoginSuccessResponse(boolean isAuthenticated, String sessionId) {
}
