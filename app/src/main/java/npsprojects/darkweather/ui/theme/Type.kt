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
        ))
)

// Set of Material typography styles to start with
val Typography = Typography(
    body1 = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.5.sp
    ),
    body2 = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.5.sp
    ),
    h1 = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.Black,
        fontSize = 60.sp,
        letterSpacing = 0.sp
    ),
    h2 = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.Black,
        fontSize = 32.sp,
        letterSpacing = 0.5.sp
    ),
    h3 = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.Bold    ,
        fontSize = 24.sp,
        letterSpacing = 0.5.sp
    ),
    h4 = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        letterSpacing = 0.5.sp
    ),

    button = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        letterSpacing = 0.sp
    )

)