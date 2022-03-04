package npsprojects.darkweather.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import compose.icons.FontAwesomeIcons
import compose.icons.WeatherIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.ArrowAltCircleDown
import compose.icons.fontawesomeicons.solid.Cloud
import compose.icons.fontawesomeicons.solid.Sun
import compose.icons.weathericons.Cloud
import compose.icons.weathericons.Cloudy
import compose.icons.weathericons.DayCloudy
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import npsprojects.darkweather.models.WeatherViewModel
import npsprojects.darkweather.ui.theme.DarkWeatherTheme
import npsprojects.darkweather.ui.theme.grey_100
import npsprojects.darkweather.ui.theme.yellow_600
import npsprojects.darkweather.ui.theme.yellow_700

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LoadingAnimationScreen(model:WeatherViewModel) {

    var startRotation by remember {
        mutableStateOf(false)
    }
    val rotation = animateFloatAsState(
        targetValue = if (startRotation) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 400,
                easing = LinearEasing
            ), repeatMode = RepeatMode.Restart
        )
    )

    var showRefresh by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isRefreshing by model.isLoading.observeAsState(initial = false)

    LaunchedEffect(key1 = isRefreshing, block = {
      startRotation = isRefreshing
        scope.launch {
            delay(4000)
            showRefresh = true
        }
    })


    Column(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(200.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                modifier = Modifier.size(130.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Icon(
                    FontAwesomeIcons.Solid.Sun,
                    contentDescription = "",
                    tint = yellow_700,
                    modifier = Modifier
                        .size(70.dp)
                        .rotate(rotation.value)

                )
                Icon(
                    FontAwesomeIcons.Solid.Cloud,
                    contentDescription = "", modifier = Modifier.size(120.dp),
                    tint = Color.Gray
                )

            }
            Text(
                "LOOKING OUTSIDE FOR YOU...",
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.tertiary)
            )
            AnimatedVisibility(visible = showRefresh) {
                IconButton(onClick = {
                    scope.launch {
                        model.initActions(context = context)
                    }
                }) {
                    Icon(
                        Icons.Default.Refresh, contentDescription = "",
                        tint = Color.Gray,
                        modifier = Modifier.size(35.dp)
                    )
                }
            }
        }

    }
}


//@Preview
//@Composable
//fun LoadingAnimationScreenPreview(){
//DarkWeatherTheme {
//    LoadingAnimationScreen()
//}
//}