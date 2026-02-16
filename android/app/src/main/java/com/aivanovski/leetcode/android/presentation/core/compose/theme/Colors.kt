package com.aivanovski.leetcode.android.presentation.core.compose.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class AppColors(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val selection: Color,
    val background: Color,
    val secondaryBackground: Color,
    val primaryIcon: Color,
    val primaryText: Color,
    val secondaryText: Color,
    val textOnSecondary: Color,
    val errorText: Color,
    val primaryButton: Color,
    val dividerOnPrimary: Color,
    val cardPrimaryBackground: Color,
    val cardPrimarySelectedBackground: Color
)

val LightAppColors = AppColors(
    primary = Color(0xFF_3366CC),
    secondary = Color(0xFF_72777d),
    tertiary = Color(0xFF_f2f4f7),
    selection = Color(0xFF_c4d6f9),
    background = Color.White,
    secondaryBackground = Color(0xFF_ececed),
    primaryIcon = Color.Black,
    primaryText = Color(0xFF_00000d),
    secondaryText = Color(0xFF_888888),
    textOnSecondary = Color(0xFF_FFFFFF),
    errorText = Color(0xFF_f2473b),
    primaryButton = Color(0xFF_3366cc),
    dividerOnPrimary = Color(0xFF_e0e0e0),
    cardPrimaryBackground = Color(0xFF_FFFFFF),
    cardPrimarySelectedBackground = Color(0xFF_DDDDDD)
)