package com.github.ai.leetcodequiz.api;

import org.jetbrains.annotations.NotNull;

public record ProblemsItemDto(
    int id,
    @NotNull String title,
    @NotNull String categoryTitle,
    @NotNull String difficulty,
    @NotNull String url,
    int dislikes,
    int likes
) {
}
