package com.aivanovski.leetcode.android.presentation.core.compose.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

val Purple40 = Color(0xFF_6650a4)
val PurpleGrey40 = Color(0xFF_625b71)
val Pink40 = Color(0xFF_7D5260)
val White = Color(0xFF_ffffff)

@Immutable
data class AppColors(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val background: Color,
    val secondaryBackground: Color,
    val primaryIcon: Color,
    val primaryText: Color,
    val secondaryText: Color,
    val errorText: Color,
    val primaryButton: Color,
    val dividerOnPrimary: Color,
    val cardPrimaryBackground: Color
)

val LightAppColors = AppColors(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = White,
    primaryIcon = Color.Black,
    primaryText = Color(0xFF_00000d),
    secondaryText = Color(0xFF_888888),
    errorText = Color(0xFF_f2473b),
    primaryButton = Color(0xFF_1c7c92),
    dividerOnPrimary = Color(0xFF_e0e0e0),
    cardPrimaryBackground = Color(0xFF_ffffff),
    secondaryBackground = Color(0xFF_ececed)
)