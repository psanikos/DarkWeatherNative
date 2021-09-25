package npsprojects.darkweather.views

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.unit.dp
import npsprojects.darkweather.ui.theme.iceBlack


fun Modifier.cardModifier() = this
    .padding(12.dp)
    .fillMaxWidth()
    .height(300.dp)
fun Modifier.frosted(isDark:Boolean=false) = this.background(Brush.linearGradient(colors = if(isDark) listOf(
    iceBlack.copy(alpha = 0.7f),
    Color.Black.copy(alpha = 0.4f),
    Color.DarkGray.copy(alpha = 0.5f),
    Color.Black.copy(alpha = 0.6f),
    iceBlack.copy(alpha = 0.4f)
)
else
    listOf(
        npsprojects.darkweather.ui.theme.frosted.copy(alpha = 0.7f),
        Color.White.copy(alpha = 0.4f),
        Color.LightGray.copy(alpha = 0.5f),
        Color.White.copy(alpha = 0.6f),
        npsprojects.darkweather.ui.theme.frosted.copy(alpha = 0.4f)
    )
))

@Composable
fun ColoredIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
) {
    Box(
        modifier = modifier
            .background(
                color = tint.copy(alpha = 0.15f),
                shape = CircleShape
            )
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material.Icon(
            painter = rememberVectorPainter(imageVector),
            contentDescription = contentDescription,
            modifier = modifier,
            tint = tint
        )
    }
}

