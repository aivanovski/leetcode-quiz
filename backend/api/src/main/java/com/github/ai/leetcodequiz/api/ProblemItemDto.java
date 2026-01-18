package com.github.ai.leetcodequiz.api;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ProblemItemDto(
    int id,
    @NotNull String title,
    @NotNull String content,
    @NotNull List<String> hints,
    @NotNull String categoryTitle,
    @NotNull String difficulty,
    @NotNull String url,
    int likes,
    int dislikes
) {
}
