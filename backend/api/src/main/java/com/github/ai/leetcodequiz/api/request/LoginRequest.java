package com.github.ai.leetcodequiz.api.request;

public record LoginRequest(
    String email,
    String password
) {
}
