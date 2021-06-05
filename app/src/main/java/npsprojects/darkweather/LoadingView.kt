package npsprojects.darkweather

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.whenStarted
import com.google.android.gms.common.util.CollectionUtils.listOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import npsprojects.darkweather.MyApp.context
import npsprojects.darkweather.ui.theme.red_500
import java.lang.Math.*
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import npsprojects.darkweather.ui.theme.orange_500
import npsprojects.darkweather.ui.theme.yellow_500

enum class WeatherError {
    NONETWORK, NOGPS, NOPERMISSION, NONE
}


@Composable
fun LoadingView(model: WeatherViewModel) {


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = if (isSystemInDarkTheme()) Color(0xFF202020) else Color(0xFFF5F5F5)),

        ) {

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
            when (model.error) {
                WeatherError.NONE ->
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,


                        ) {
                        Box(
                            modifier = Modifier
                                .padding(bottom = 100.dp)
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {

                            LoadingAnimation()

                        }

                    }

                WeatherError.NOPERMISSION ->
                    Column(
                        Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Icon(
                            Icons.TwoTone.Warning,
                            modifier = Modifier.size(50.dp),
                            contentDescription = "",
                            tint = yellow_500
                        )
                          Text(stringResource(id = R.string.LocationDenied),style = MaterialTheme.typography.h4,textAlign = TextAlign.Center)


                    }
                WeatherError.NOGPS ->
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,


                        ) {
                        Column(
                            Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {

                            Icon(
                                Icons.TwoTone.Warning,
                                modifier = Modifier.size(50.dp),
                                contentDescription = "",
                                tint = orange_500
                            )
                            Text(stringResource(id = R.string.NoLocationNoAccess),style = MaterialTheme.typography.h4,textAlign = TextAlign.Center)


                        }
                    }

                WeatherError.NONETWORK -> Column(
                    Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Icon(
                        Icons.TwoTone.Warning,
                        modifier = Modifier.size(50.dp),
                        contentDescription = "",
                        tint = red_500
                    )
                   Text(stringResource(id = R.string.NoInternet),style = MaterialTheme.typography.h4,textAlign = TextAlign.Center)


                }

            }
        }

    }
}



@Composable
fun LoadingAnimation() {
    var enabled by remember { mutableStateOf(false) }

    val images = listOf<Int>(R.drawable.sun, R.drawable.clouds, R.drawable.rain, R.drawable.snow)

    val currentImage = animateIntAsState(
        targetValue = if (enabled) 4 else 0,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    LaunchedEffect(key1 = "Animate") {
        enabled = true
    }




    Crossfade(
        targetState = currentImage.value,
        animationSpec = tween(1600, easing = LinearEasing)
    ) { animImage ->
        when (animImage) {
            0 -> LoadingImage(image = images[0])

            1 -> LoadingImage(image = images[1])
            2 -> LoadingImage(image = images[2])
            3 -> LoadingImage(image = images[3])
        }


    }
}

@Composable
fun LoadingImage(image: Int) {
    var enabled by remember { mutableStateOf(false) }
    val currentSize = animateIntAsState(
        targetValue = if (enabled) 90 else 50,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800),
            repeatMode = RepeatMode.Reverse
        )
    )
    val opacity = animateFloatAsState(
        targetValue = if (enabled) 1f else 0f, animationSpec = tween(600, easing = LinearEasing)
    )

    LaunchedEffect(key1 = "Animate") {
        enabled = true
    }
    Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = image), contentDescription = "",
            modifier = Modifier.size(currentSize.value.dp),
            alpha = opacity.value
        )
    }
}
