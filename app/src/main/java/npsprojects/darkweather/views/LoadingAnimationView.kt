package npsprojects.darkweather.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding
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
import npsprojects.darkweather.ui.theme.DarkWeatherTheme
import npsprojects.darkweather.ui.theme.grey_100
import npsprojects.darkweather.ui.theme.yellow_600
import npsprojects.darkweather.ui.theme.yellow_700

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LoadingAnimationScreen(){

    var startRotation by remember {
        mutableStateOf(false)
    }
  val rotation = animateFloatAsState(targetValue = if(startRotation) 360f else 0f,
      animationSpec = infiniteRepeatable(animation = tween(durationMillis = 1400, easing = LinearEasing), repeatMode = RepeatMode.Restart))

    var showRefresh by remember {
        mutableStateOf(false)
          }
    val scope = rememberCoroutineScope()
LaunchedEffect(key1 = "Animation", block = {
    startRotation = true
  scope.launch {
      delay(5000)
      showRefresh = true
  }
})

        Column(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxSize(),verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(60.dp))
       AnimatedVisibility(visible = showRefresh ) {
           Column(

               horizontalAlignment = Alignment.CenterHorizontally,
               verticalArrangement = Arrangement.spacedBy(10.dp)
           ) {
               Text(
                   "Please pull to refresh.",
                   style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.tertiaryContainer)
               )
               ColoredIcon(
                   imageVector = FontAwesomeIcons.Solid.ArrowAltCircleDown,
                   contentDescription = "",
                   modifier = Modifier.size(40.dp),
                   padding = 6.dp
               )
           }
       }
            Spacer(modifier = Modifier.height(100.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
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
            }

        }



}


@Preview
@Composable
fun LoadingAnimationScreenPreview(){
DarkWeatherTheme {
    LoadingAnimationScreen()
}
}