package com.github.ai.leetcodequiz.api.response;

import com.github.ai.leetcodequiz.api.UserDto;
import org.jetbrains.annotations.NotNull;

public record SignupResponse(
    @NotNull String token,
    @NotNull UserDto user
) {
}
