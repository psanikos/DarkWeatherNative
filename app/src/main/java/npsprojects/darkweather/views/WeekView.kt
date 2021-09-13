package npsprojects.darkweather.views
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import npsprojects.darkweather.getWeatherIcon
import npsprojects.darkweather.models.Daily
import npsprojects.darkweather.models.WeatherViewModel
import npsprojects.darkweather.round
import npsprojects.darkweather.ui.theme.blue_500
import npsprojects.darkweather.ui.theme.blue_grey_500
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@Composable
fun WeekView(model: WeatherViewModel){
    val index:Int by  model.index.observeAsState(initial = 0)
    var week:List<Daily> by rememberSaveable {
        mutableStateOf(listOf())
    }

        LaunchedEffect(key1 = index + model.locations.size, block = {
            if (!model.locations.isEmpty()) {
                week = model.locations[model.index.value!!].data.daily
            }
        })
    if(week.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(
                    color = blue_grey_500.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(0)
                )
                .padding(20.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

   //         Text("Week", style = MaterialTheme.typography.h2.copy(fontSize = 12.sp,color = Color.DarkGray))
            week.forEach {
                WeekViewItem(day = it)
            }
        }
    }
}

@Composable
fun WeekViewItem(day:Daily) {
    Column( modifier = Modifier
        .height(70.dp)
        .fillMaxWidth(),
    horizontalAlignment = Alignment.Start,
    verticalArrangement = Arrangement.SpaceBetween) {

        Text(
            SimpleDateFormat("EEEE", Locale.getDefault()).format(
                Date(1000 * day.dt)
            ), style = MaterialTheme.typography.h4.copy(fontSize = 16.sp)
        )
        Row(
            modifier = Modifier
                .height(60.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxHeight()

            ) {

                Box() {
                    Image(
                        painter = painterResource(id = getWeatherIcon(day.weather[0].icon)),
                        contentDescription = "",
                        modifier = Modifier
                            .offset(x = 1.dp, y = 2.dp)
                            .height(60.dp)
                            .width(60.dp),
                        colorFilter = ColorFilter.tint(color = Color.Gray.copy(alpha = 0.5f))

                    )

                    Image(
                        painter = painterResource(id = getWeatherIcon(day.weather[0].icon)),
                        contentDescription = "",
                        modifier = Modifier
                            .height(60.dp)
                            .width(60.dp)

                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    day.weather[0].description,
                    style = MaterialTheme.typography.body2,
                    maxLines = 1
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
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
                            text = "${day.wind_speed.round(1)} mph",
                            style = MaterialTheme.typography.body2
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        ColoredIcon(
                            Icons.Filled.ArrowUpward,
                            contentDescription = "",
                            modifier = Modifier.size(16.dp),
                            tint = Color.Red
                        )

                        Text(
                            text = "${day.temp.max.toUInt()}°",
                            style = MaterialTheme.typography.body2
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        ColoredIcon(
                            Icons.Filled.ArrowDownward,
                            contentDescription = "",
                            modifier = Modifier.size(16.dp),
                            tint = Color.Blue
                        )

                        Text(
                            text = "${day.temp.min.toUInt()}°",
                            style = MaterialTheme.typography.body2
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ColoredIcon(
                            Icons.Filled.Umbrella,
                            contentDescription = "",
                            modifier = Modifier.size(20.dp),
                            tint = blue_500
                        )

                        Text(
                            text = "${(100 * (day.pop ?: 0.0)).toUInt()}%",
                            style = MaterialTheme.typography.body2
                        )
                    }
                }

            }
        }
    }
}
//LocalDateTime.ofInstant(
//Instant.ofEpochMilli(1000 * it.dt),
//ZoneId.systemDefault()
//)