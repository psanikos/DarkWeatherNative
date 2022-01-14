package npsprojects.darkweather.views

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import npsprojects.darkweather.R
import npsprojects.darkweather.models.WeatherViewModel
import npsprojects.darkweather.ui.theme.*

//@Composable
//fun UVView(model: WeatherViewModel) {
//    val index: Int by model.index.observeAsState(initial = 0)
//    var input by  rememberSaveable {
//        mutableStateOf(0.0)
//    }
//    LaunchedEffect(key1 = index + model.locations.size, block = {
//        if (model.locations.isNotEmpty() && model.locations.size > index) {
//            input = if(model.locations[index].data.current.uvi > 10.0) 10.0 else model.locations[index].data.current.uvi
//
//        }
//    })
//
//    val name = when (input.toInt()) {
//        in 0 .. 3 -> stringResource(id = R.string.Good)
//        in 4..6 -> stringResource(id = R.string.Fair)
//        in 7..9 -> stringResource(id = R.string.high)
//        else -> stringResource(id = R.string.vHigh)
//    }
////    val summary = when (aqi) {
////        1 -> stringResource(id = R.string.GoodText)
////        2 -> stringResource(id = R.string.FairText)
////        3 -> stringResource(id = R.string.ModerateText)
////        4 -> stringResource(id = R.string.PoorText)
////        else -> stringResource(id = R.string.VeryPoorText)
////    }
//
//
//    val configuration = LocalConfiguration.current
//    val isLarge = configuration.smallestScreenWidthDp > 400
//    val pointWidth = with(LocalDensity.current){configuration.smallestScreenWidthDp/(if(isLarge) 32 else 16)}
//    var toAnimate by rememberSaveable {
//        mutableStateOf(false)
//    }
//    val value = animateDpAsState(targetValue = if(toAnimate) (input*pointWidth).dp else 0.dp,
//        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
//    )
//
//    LaunchedEffect(key1 = index + model.locations.size, block = {
//        toAnimate = false
//        delay(600)
//        toAnimate = true
//    } )
//
//    if (model.locations.size  > 0) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .wrapContentHeight()
//              ,
//            horizontalAlignment = Alignment.Start,
//            verticalArrangement = Arrangement.spacedBy(10.dp)
//        ) {
//
//                Text(
//                    stringResource(R.string.uv),
//                    style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.ExtraBold))
//
//            Column(
//                modifier = Modifier
//                    .clip(RoundedCornerShape(12.dp))
//                    .background(color = if(isSystemInDarkTheme()) Color.DarkGray.copy(alpha = 0.6f) else frosted.copy(alpha = 0.6f))
//                    .padding(horizontal = 10.dp,vertical = 15.dp)
//                    .fillMaxWidth()
//                    .wrapContentHeight()
//                   ,
//
//                horizontalAlignment = Alignment.Start,
//                verticalArrangement = Arrangement.spacedBy(20.dp)
//            ) {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.Start,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//
//
//                    Text(
//                        name,
//                        style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.SemiBold,color = Color.Gray),
//
//                        )
//                }
//                Box() {
//                    Row(
//                        modifier = Modifier
//                            .height(16.dp)
//                            .fillMaxWidth()
//                            .background(
//                                brush = Brush.linearGradient(
//                                    colors = listOf(
//                                        green_500, yellow_500, orange_500, pink_500, red_500
//                                    )
//                                ),
//                                shape = RoundedCornerShape(50)
//                            )
//                    ) {
//
//                    }
//                    Box(
//                        Modifier
//                            .offset(x = value.value, y = (-2).dp)
//                            .size(18.dp)
//                            .background(color = blue_grey_500, shape = TriangleShape)
//                    )
//                }
//
//            }
//        }
//    }
//}