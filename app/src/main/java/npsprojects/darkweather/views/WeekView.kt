package npsprojects.darkweather.views
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import npsprojects.darkweather.WeatherUnits
import npsprojects.darkweather.getWeatherIcon
import npsprojects.darkweather.models.Daily
import npsprojects.darkweather.models.WeatherViewModel
import npsprojects.darkweather.round
import npsprojects.darkweather.ui.theme.frosted
import npsprojects.darkweather.ui.theme.light_blue_500
import npsprojects.darkweather.ui.theme.pink_500
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WeekView(model: WeatherViewModel){
    val index:Int by  model.index.observeAsState(initial = 0)
    var week:List<Daily> by rememberSaveable {
        mutableStateOf(listOf())
    }

        LaunchedEffect(key1 = index + model.locations.size, block = {
            if (model.locations.isNotEmpty() && model.locations.size > index) {
                week = model.locations[index].data.daily
            }
        })
    if(week.isNotEmpty()) {


    Column(modifier = Modifier
        .clip(RoundedCornerShape(12.dp))
        .background(color = if(isSystemInDarkTheme()) Color.DarkGray.copy(alpha = 0.6f) else frosted.copy(alpha = 0.6f))
        .padding(horizontal = 10.dp,vertical = 15.dp)
        .fillMaxWidth()
        .wrapContentHeight(),
    verticalArrangement = Arrangement.spacedBy(10.dp)) {


        week.forEach {
            WeekViewItem(model = model, day = it)

        }
    }
    }
}

@Composable
fun WeekViewItem(model:WeatherViewModel,day:Daily) {


    Row(
        modifier = Modifier
            .height(40.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(75.dp)
        ) {
            Text(
                SimpleDateFormat("EEE", Locale.getDefault()).format(
                    Date(1000 * day.dt)
                ), style = MaterialTheme.typography.h3.copy(fontSize = 16.sp)
            )


            Box() {
        Icon(getWeatherIcon(day.weather[0].id,day.weather[0].icon.contains("n")),contentDescription = "",modifier = Modifier.size(30.dp))


            }
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 10.dp)
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {


                ColoredIcon(
                    Icons.Filled.Navigation,
                    contentDescription = "",
                    modifier = Modifier
                        .size(16.dp)
                        .rotate(day.wind_deg.toFloat()),
                    tint = if (isSystemInDarkTheme()) Color.White else Color.Black,

                    )

                Text(
                    text = "${day.wind_speed.round(1)} " + if (model.units == WeatherUnits.SI) "km/h" else "mph",
                    style = MaterialTheme.typography.body2
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "${day.temp.min.toUInt()}°",
                    style = MaterialTheme.typography.body2
                )
                val width = 5 * (day.temp.max.toUInt() - day.temp.min.toUInt()).toDouble()
                val boxWidth = if (width > 40.0) (if (width > 100.0) 100.0 else width) else 40.0
                Box(
                    modifier = Modifier
                        .height(16.dp)
                        .width(width = boxWidth.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    light_blue_500,
                                    pink_500
                                )
                            ), shape = RoundedCornerShape(50)
                        )
                )
                Text(
                    text = "${day.temp.max.toUInt()}°",
                    style = MaterialTheme.typography.body2
                )

            }


        }


    }
}
//LocalDateTime.ofInstant(
//Instant.ofEpochMilli(1000 * it.dt),
//ZoneId.systemDefault()
//)