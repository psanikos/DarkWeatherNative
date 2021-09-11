package npsprojects.darkweather.views

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import npsprojects.darkweather.R
import npsprojects.darkweather.models.WeatherViewModel
import npsprojects.darkweather.ui.theme.*

@Composable
fun UVView(model: WeatherViewModel) {
    val index: Int by model.index.observeAsState(initial = 0)
    var input by  rememberSaveable {
        mutableStateOf(0.0)
    }
    LaunchedEffect(key1 = index + model.locations.size, block = {
        if (!model.locations.isEmpty() ) {
            input = if(model.locations[index].data.current.uvi > 10.0) 10.0 else model.locations[index].data.current.uvi

        }
    })

    val name = when (input.toInt()) {
        in 0 .. 3 -> "Low"
        in 4..6 -> "Medium"
        in 7..9 -> "High"
        else -> "Very High"
    }
//    val summary = when (aqi) {
//        1 -> stringResource(id = R.string.GoodText)
//        2 -> stringResource(id = R.string.FairText)
//        3 -> stringResource(id = R.string.ModerateText)
//        4 -> stringResource(id = R.string.PoorText)
//        else -> stringResource(id = R.string.VeryPoorText)
//    }


    val configuration = LocalConfiguration.current
    val pointWidth = with(LocalDensity.current){configuration.smallestScreenWidthDp/16}
    var toAnimate by rememberSaveable {
        mutableStateOf(false)
    }
    val value = animateDpAsState(targetValue = if(toAnimate) (input*pointWidth).dp else 0.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    LaunchedEffect(key1 = index + model.locations.size, block = {
        toAnimate = false
        delay(600)
        toAnimate = true
    } )

    if (model.locations.size  > 0) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(20.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween,verticalAlignment = Alignment.CenterVertically) {


    Text("UV Radiation", style = MaterialTheme.typography.h4)

    Text(
        name,
        style = MaterialTheme.typography.h3,
        modifier = Modifier.padding(bottom = 10.dp)
    )
}
            Box() {
                Row(
                    modifier = Modifier
                        .height(18.dp)
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
                            .height(18.dp)
                            .fillMaxWidth(0.25f)
                            .background(teal_500.copy(alpha = 0.8f))
                    )
                    Box(
                        modifier = Modifier
                            .height(18.dp)
                            .fillMaxWidth(0.33f)
                            .background(yellow_600.copy(alpha = 0.8f))
                    )
                    Box(
                        modifier = Modifier
                            .height(18.dp)
                            .fillMaxWidth(0.5f)
                            .background(orange_600.copy(alpha = 0.8f))
                    )
                    Box(
                        modifier = Modifier
                            .height(18.dp)
                            .fillMaxWidth()
                            .background(red_600.copy(alpha = 0.8f))
                    )

                }
                Box(
                    Modifier
                        .offset(x = value.value, y = (-2).dp)
                        .size(20.dp)
                        .background(color = blue_grey_500, shape = TriangleShape))
            }

        }
    }
}