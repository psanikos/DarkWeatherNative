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
val Typography = Typography(
    body1 = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    body2 = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.Light,
        fontSize = 14.sp
    ),
    h1 = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.Black,
        fontSize = 60.sp
    ),
    h2 = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.Black,
        fontSize = 32.sp
    ),
    h3 = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.Bold    ,
        fontSize = 24.sp
    ),
    h4 = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    ),

    button = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp
    ),
    subtitle1 = TextStyle(
        fontFamily = appFont,
        fontWeight = FontWeight.SemiBold    ,
        fontSize = 16.sp
    )

)