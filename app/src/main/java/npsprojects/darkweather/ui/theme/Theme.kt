package npsprojects.darkweather.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Color.White,
    background = Color(0xFF202020),
    primaryVariant = Color.Black,
    secondary = purple_600,
    onSurface = Color.White,
    onBackground = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White
)

private val LightColorPalette = lightColors(
    primary = Color.DarkGray,
    background = Color(0xFFEFEFEF),
    primaryVariant = Color.White,
    secondary = blue_500,
    onSurface = Color.Black,
    onBackground = Color.White,
    onPrimary = Color.DarkGray,
    onSecondary = Color.DarkGray
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