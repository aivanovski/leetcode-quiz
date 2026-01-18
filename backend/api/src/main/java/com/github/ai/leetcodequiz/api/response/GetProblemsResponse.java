package com.github.ai.leetcodequiz.api.response;

import com.github.ai.leetcodequiz.api.ProblemsItemDto;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record GetProblemsResponse(
    @NotNull List<ProblemsItemDto> problems
) {
}
