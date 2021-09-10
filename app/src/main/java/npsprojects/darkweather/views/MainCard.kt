package npsprojects.darkweather.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.swipeable
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import npsprojects.darkweather.models.WeatherModel
import npsprojects.darkweather.models.WeatherViewModel
import npsprojects.darkweather.round
import npsprojects.darkweather.timeAgo
import npsprojects.darkweather.ui.theme.DarkWeatherTheme
import npsprojects.darkweather.ui.theme.blue_500
import java.time.Instant
import java.util.*

@ExperimentalCoilApi
@Composable
fun MainCard(model:WeatherViewModel) {

        var icon by remember {
            mutableStateOf("02d")
        }
    var temp by remember {
        mutableStateOf("N/A°")
    }
    var tempHigh by remember {
        mutableStateOf("N/A°")
    }
    var tempLow by remember {
        mutableStateOf("N/A°")
    }
    var description by remember {
        mutableStateOf("N/A")
    }
    var angle by remember {
        mutableStateOf(0f)
    }
    var air by remember {
        mutableStateOf(0.0)
    }
    var pop by remember {
        mutableStateOf(0)
    }
val index:Int by  model.index.observeAsState(initial = 0)

    LaunchedEffect(key1 = index, block ={
        if(model.locations.size > 0){
          icon = model.locations[model.index.value!!].data.current.weather[0].icon
            temp = model.locations[model.index.value!!].data.current.temp.toUInt().toString() + "°"
            tempHigh = model.locations[model.index.value!!].data.daily[0].temp.max.toUInt().toString() + "°"
            tempLow = model.locations[model.index.value!!].data.daily[0].temp.min.toUInt().toString() + "°"
            description = model.locations[model.index.value!!].data.current.weather[0].description
            angle = model.locations[model.index.value!!].data.current.wind_deg.toFloat()
            air = model.locations[model.index.value!!].data.current.wind_speed.round(1)
            pop = (100*(model.locations[model.index.value!!].data.current.pop ?: 0.0)).toInt()
        }
    })
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(

                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(

                    verticalArrangement = Arrangement.spacedBy(0.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box() {
                        Image(
                            painter = rememberImagePainter(data = "https://openweathermap.org/img/wn/${icon}@4x.png"),
                            contentDescription = "weather image",
                            modifier = Modifier
                                .offset(x = 2.dp, y = 4.dp)
                                .size(150.dp),
                            contentScale = ContentScale.Fit,
                            colorFilter = ColorFilter.tint(color = Color.LightGray.copy(alpha = 0.6f))
                        )

                        Image(
                            painter = rememberImagePainter(data = "https://openweathermap.org/img/wn/${icon}@4x.png"),
                            contentDescription = "weather image",
                            modifier = Modifier.size(150.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Text(text = temp, style = MaterialTheme.typography.h1.copy(fontSize = 46.sp))
                }
                Text(description, style = MaterialTheme.typography.body1)
            }
            Row(
                modifier= Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {


                        ColoredIcon(
                            Icons.Filled.Navigation,
                            contentDescription = "",
                            modifier = Modifier
                                .size(20.dp)
                                .rotate(angle),
                            tint = if(isSystemInDarkTheme()) Color.White else Color.Black,

                        )

                    Text(text = "$air mph", style = MaterialTheme.typography.body1)
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                        ColoredIcon(
                            Icons.Filled.ArrowUpward,
                            contentDescription = "",
                            modifier = Modifier.size(20.dp),
                            tint = Color.Red
                        )

                    Text(text = tempHigh, style = MaterialTheme.typography.body1)
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    ColoredIcon(
                        Icons.Filled.ArrowDownward,
                        contentDescription = "",
                        modifier = Modifier.size(20.dp),
                        tint = Color.Blue
                    )

                    Text(text = tempLow, style = MaterialTheme.typography.body1)
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                            ColoredIcon(Icons.Filled.Umbrella, contentDescription = "", modifier = Modifier.size(20.dp),tint = blue_500)

                            Text(
                                text = "${pop}%",
                                style = MaterialTheme.typography.body1
                            )
                        }
                }

        }
    }




//@Preview
//@Composable
//fun MainPreview(){
//    MaterialTheme {
//      MainCard()
//    }
//}
