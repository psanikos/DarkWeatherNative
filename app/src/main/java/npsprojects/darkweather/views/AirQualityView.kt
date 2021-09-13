package npsprojects.darkweather.views

import android.widget.ProgressBar
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.insets.LocalWindowInsets
import kotlinx.coroutines.delay
import npsprojects.darkweather.R
import npsprojects.darkweather.models.Daily
import npsprojects.darkweather.models.WeatherViewModel
import npsprojects.darkweather.ui.theme.*


val TriangleShape = GenericShape { size, _ ->
    // 1)

    moveTo(0f, 0f)

    // 2)
    lineTo(size.width/2, size.height)

    // 3)
    lineTo(size.width, 0f)
}

@Composable
fun AirQualityView(model:WeatherViewModel) {
    val index: Int by model.index.observeAsState(initial = 0)
    var aqi by  rememberSaveable {
        mutableStateOf(1)
    }
    LaunchedEffect(key1 = index + model.locations.size, block = {
        if (!model.locations.isEmpty() && (model.locations[index].airQuality != null)) {
            aqi = model.locations[index].airQuality!!.list!![0].main!!.aqi!!

        }
    })
    val color = when (aqi) {
        1 -> teal_500
        2 -> yellow_500
        3 -> orange_500
        4 -> pink_500
        else -> red_500
    }
    val name = when (aqi) {
        1 -> stringResource(id = R.string.Good)
        2 -> stringResource(id = R.string.Fair)
        3 -> stringResource(id = R.string.Moderate)
        4 -> stringResource(id = R.string.Poor)
        else -> stringResource(id = R.string.VeryPoor)
    }
    val summary = when (aqi) {
        1 -> stringResource(id = R.string.GoodText)
        2 -> stringResource(id = R.string.FairText)
        3 -> stringResource(id = R.string.ModerateText)
        4 -> stringResource(id = R.string.PoorText)
        else -> stringResource(id = R.string.VeryPoorText)
    }


    val configuration = LocalConfiguration.current
    val pointWidth = with(LocalDensity.current){configuration.smallestScreenWidthDp/5 - configuration.smallestScreenWidthDp/10}
    var toAnimate by rememberSaveable {
        mutableStateOf(false)
    }
    val value = animateDpAsState(targetValue = if(toAnimate) (aqi*pointWidth).dp else 0.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))

    LaunchedEffect(key1 = index + model.locations.size, block = {
       toAnimate = false
       delay(600)
        toAnimate = true
    } )

    if (model.locations.size  > 0 && model.locations[index].airQuality != null) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(20.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween,verticalAlignment = Alignment.CenterVertically) {

                Text(stringResource(R.string.AirQ), style = MaterialTheme.typography.h4.copy(color = Color.DarkGray))





                Text(
                    name,
                    style = MaterialTheme.typography.h4,

                )
            }
            Box() {
                Row(
                    modifier = Modifier
                        .height(16.dp)
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(50))
                ) {
//                        LinearProgressIndicator(
//                            modifier = Modifier.fillMaxWidth().height(18.dp)
//                                .clip(RoundedCornerShape(50)),backgroundColor = Color.Gray,
//                            progress = value.value,
//                            color = color
//                        )
                    Box(
                        modifier = Modifier
                            .height(16.dp)
                            .fillMaxWidth(0.2f)
                            .background(Color.Green.copy(alpha = 0.8f))
                    )
                    Box(
                        modifier = Modifier
                            .height(16.dp)
                            .fillMaxWidth(0.25f)
                            .background(Color.Yellow.copy(alpha = 0.8f))
                    )
                    Box(
                        modifier = Modifier
                            .height(16.dp)
                            .fillMaxWidth(0.33f)
                            .background(orange_500.copy(alpha = 0.8f))
                    )
                    Box(
                        modifier = Modifier
                            .height(16.dp)
                            .fillMaxWidth(0.5f)
                            .background(pink_500.copy(alpha = 0.8f))
                    )
                    Box(
                        modifier = Modifier
                            .height(16.dp)
                            .fillMaxWidth()
                            .background(red_500.copy(alpha = 0.8f))
                    )
                }
                Box(
                    Modifier
                        .offset(x = value.value, y = (-2).dp)
                        .size(18.dp)
                        .background(color = blue_grey_500, shape = TriangleShape))
            }

                        Text(
                            summary,
                            style = MaterialTheme.typography.body2

                        )






        }
    }
}

//interface Shape {
//
///**
// * Creates [Outline] of this shape for the given [size].
// *
// * @param size the size of the shape boundary.
// * @param density the current density of the screen.
// *
// * @return [Outline] of this shape for the given [size].
//*/
//fun createOutline(size: Size, density: Density): Outline
//}
//class CustomShape : Shape {
//    override fun createOutline(size: Size, density: Density): Outline {
//        val path = Path().apply {
//            moveTo(size.width / 2f, 0f)
//            lineTo(size.width, size.height)
//            lineTo(0f, size.height)
//            close()
//        }
//        return Outline.Generic(path)
//    }
//}