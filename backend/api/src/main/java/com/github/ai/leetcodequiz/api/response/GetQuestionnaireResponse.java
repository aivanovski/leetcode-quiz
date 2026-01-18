package com.github.ai.leetcodequiz.api.response;

import com.github.ai.leetcodequiz.api.QuestionnaireItemDto;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record GetQuestionnaireResponse(
    @NotNull QuestionnaireItemDto questionnaire
) {
}
