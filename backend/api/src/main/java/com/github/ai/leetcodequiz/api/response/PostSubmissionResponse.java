package com.github.ai.leetcodequiz.api.response;

import com.github.ai.leetcodequiz.api.QuestionnaireItemDto;
import org.jetbrains.annotations.NotNull;

public record PostSubmissionResponse(
    @NotNull QuestionnaireItemDto questionnaire
) {
}
