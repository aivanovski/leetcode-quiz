package com.github.ai.leetcodequiz.api.response;

import com.github.ai.leetcodequiz.api.QuestionDto;

import java.util.List;

public record GetQuestionsResponse(
        List<QuestionDto> questions
) {
}
