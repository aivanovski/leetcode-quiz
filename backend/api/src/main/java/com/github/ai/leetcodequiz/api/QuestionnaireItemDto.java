package com.github.ai.leetcodequiz.api;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record QuestionnaireItemDto(
        @NotNull String id,
        boolean isComplete,
        @NotNull List<QuestionItemDto> nextQuestions
) {
}
