package com.github.ai.leetcodequiz.api;

import org.jetbrains.annotations.NotNull;

public record QuestionItemDto(
    @NotNull String id,
    int problemId,
    @NotNull String problemTitle,
    @NotNull String question,
    @NotNull String complexity
) {
}
