package npsprojects.darkweather.views

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.unit.dp


fun Modifier.cardModifier() = this.padding(12.dp).fillMaxWidth().height(300.dp)

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

