package com.github.ai.leetcodequiz.api;

import org.jetbrains.annotations.NotNull;

public record UserDto(
    @NotNull String name,
    @NotNull String email
) {
}
