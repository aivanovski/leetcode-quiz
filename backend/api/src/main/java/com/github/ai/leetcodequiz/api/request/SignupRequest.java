package com.github.ai.leetcodequiz.api.request;

public record SignupRequest(
    String name,
    String email,
    String password
) {
}
