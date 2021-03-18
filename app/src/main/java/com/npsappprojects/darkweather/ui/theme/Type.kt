package com.npsappprojects.darkweather.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.npsappprojects.darkweather.R


private val appFont = FontFamily(
    fonts = listOf(
        Font(
            resId = R.font.latobold,
            weight = FontWeight.Bold

        ),
        Font(
            resId = R.font.latoblack,
            weight = FontWeight.Black
        ),
        Font(
            resId = R.font.latolight,
            weight = FontWeight.Light
        ),
        Font(
            resId = R.font.latoregular,
            weight = FontWeight.Medium
        ),
        Font(
            resId = R.font.latothin,
            weight = FontWeight.Thin
        ),
    )
)

// Set of Material typography styles to start with
val Typography = Typography(
    body1 = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        letterSpacing = 1.sp
    ),
    body2 = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        letterSpacing = 1.sp
    ),
    h1 = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.Black,
        fontSize = 34.sp,
        letterSpacing = 1.05.sp
    ),
    h2 = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.Black,
        fontSize = 28.sp,
        letterSpacing = 1.05.sp
    ),
    h3 = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.Black,
        fontSize = 24.sp,
        letterSpacing = 1.sp
    ),

    button = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )

)