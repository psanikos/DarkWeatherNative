package npsprojects.darkweather.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Color.White,
    background = Color(0xFF252525),
    primaryVariant = Color.Black,
    secondary = Color.LightGray,
    onSurface = Color.White,
    onBackground = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    surface = Color(0xFF202020),
)

private val LightColorPalette = lightColors(
    primary = Color.Black,
    background = Color(0xFFFEFEFE),
    primaryVariant = Color.White,
    secondary = Color.DarkGray,
    onSurface = Color.Black,
    onBackground = Color.Black,
    onPrimary = Color.DarkGray,
    onSecondary = Color.DarkGray,
    surface = Color(0xFFEFEFEF)
    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun DarkWeatherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}