package com.github.ai.leetcodequiz.api;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record ErrorMessageDto(
    @NotNull String message,
    @NotNull String exception,
    @NotNull String stacktraceBase64,
    @NotNull List<String> stacktraceLines
) {
}
