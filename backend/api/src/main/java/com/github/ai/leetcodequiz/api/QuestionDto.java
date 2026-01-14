package com.github.ai.leetcodequiz.api;

import java.util.List;

public record QuestionDto(
        int id,
        String title,
        String content,
        List<String> hints,
        int likes,
        int dislikes,
        String categoryTitle,
        String difficulty,
        String url
//        pub stats: QuestionStats,
) {
}
