package com.github.ai.leetcodequiz.api.response;

import com.github.ai.leetcodequiz.api.ProblemItemDto;
import org.jetbrains.annotations.NotNull;

public record GetProblemResponse(
    @NotNull ProblemItemDto problem
) {
}
