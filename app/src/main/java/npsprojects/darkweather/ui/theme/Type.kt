package npsprojects.darkweather.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import npsprojects.darkweather.R


private val appFont = FontFamily(
    fonts = listOf(
        Font(
            resId = R.font.montserratbold,
            weight = FontWeight.Bold

        ),
        Font(
            resId = R.font.montserratblack,
            weight = FontWeight.Black
        ),
        Font(
            resId = R.font.montserratregular,
            weight = FontWeight.Light
        ),
        Font(
            resId = R.font.montserratsemibold,
            weight = FontWeight.SemiBold
        ),
        Font(
            resId = R.font.montserratmedium,
            weight = FontWeight.Medium
        ),
        Font(
            resId = R.font.montserratthin,
            weight = FontWeight.Thin
        ))
)

// Set of Material typography styles to start with
val Typography = androidx.compose.material3.Typography(
    bodyMedium = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    displayLarge = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.SemiBold    ,
        fontSize = 18.sp),
    displayMedium = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.SemiBold  ,
        fontSize = 16.sp),
    displaySmall = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.SemiBold    ,
        fontSize = 14.sp),
    titleLarge = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.Bold   ,
        fontSize = 20.sp),
    titleMedium = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.Bold   ,
        fontSize = 18.sp),
    titleSmall = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.Bold   ,
        fontSize = 16.sp),
    labelLarge = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.Black,
        fontSize = 40.sp
    ),
    labelMedium = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.Black,
        fontSize = 30.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.Black,
        fontSize = 26.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.Black,
        fontSize = 24.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),


)
