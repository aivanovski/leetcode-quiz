package com.github.ai.leetcodequiz.api.request;

public record PostSubmissionRequest(
    String questionId,
    int answer
) {
}
