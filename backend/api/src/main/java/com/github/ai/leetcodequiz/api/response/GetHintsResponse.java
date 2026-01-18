package com.github.ai.leetcodequiz.api.response;

import com.github.ai.leetcodequiz.api.QuestionItemDto;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record GetHintsResponse(
    @NotNull List<QuestionItemDto> questions
) {
}
