package com.aivanovski.leetcode.android.utils

import arrow.core.Either

fun <E, V> Either<E, V>.unwrap(): V {
    return getOrNull() as V
}