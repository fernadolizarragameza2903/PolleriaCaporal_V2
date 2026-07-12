package com.polleriacaporal.dto;

public record AuthResponse(
        String token,
        String username,
        String rol
) {
}
