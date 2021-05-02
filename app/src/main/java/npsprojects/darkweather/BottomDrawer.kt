package npsprojects.darkweather

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.common.util.CollectionUtils.listOf
import npsprojects.darkweather.ui.theme.blue_700
import npsprojects.darkweather.ui.theme.orange_500
import npsprojects.darkweather.ui.theme.red_500
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun BottomDrawer(data: WeatherViewModel, index: Int, units: WeatherUnits) {

    var isExpanded: Boolean by remember { mutableStateOf(false) }

    val locationData = data.locations[index].data

    LazyColumn(
        modifier = Modifier
            .padding(vertical = 10.dp, horizontal = 16.dp)
            .fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Box(
                    modifier = Modifier
                        .height(4.dp)
                        .width(50.dp)
                        .background(
                            color = Color.Gray,
                            shape = RoundedCornerShape(40)

                        )
                )

            }
            if (locationData.alerts.size > 0) {

                Surface(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .fillMaxWidth()
                        .animateContentSize()
                        .clickable {
                            isExpanded = !isExpanded
                        },
                    color = if (locationData.alerts[0].severity == "warning") red_500.copy(
                        alpha = 0.3f
                    ) else orange_500.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Warning,
                            contentDescription = "",
                            modifier = Modifier
                                .padding(10.dp)
                                .size(40.dp),
                            tint = if (locationData.alerts[0].severity == "warning") red_500 else orange_500
                        )
                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.padding(6.dp)
                        ) {
                            Text(
                                locationData.alerts[0].title,
                                style = MaterialTheme.typography.h4.copy(fontSize = 14.sp)
                            )

                            if (isExpanded) {
                                Text(
                                    locationData.alerts[0].description.toLowerCase(Locale.current),
                                    style = MaterialTheme.typography.button.copy(fontSize = 11.sp),
                                    modifier = Modifier.padding(vertical = 5.dp),

                                    )
                            } else {
                                Text(
                                    locationData.alerts[0].description.toLowerCase(Locale.current),
                                    style = MaterialTheme.typography.button.copy(fontSize = 11.sp),
                                    modifier = Modifier.padding(vertical = 5.dp),
                                    maxLines = 3
                                )
                            }

                            Text(
                                "Until: " +
                                        SimpleDateFormat("EEEE dd  HH:mm").format(1000 * locationData.alerts[0].expires.toLong()),
                                style = MaterialTheme.typography.h4.copy(fontSize = 12.sp),
                                lineHeight = 10.sp
                            )


                        }
                    }
                }
            }
        }
        item {
            Text("Hourly", style = MaterialTheme.typography.h4)
        }
        item {
            LazyRow(
                modifier = Modifier


            ) {

                locationData.hourly.data.forEach {
                    item {
                        Box(
                            modifier = Modifier
                                .height(120.dp)
                                .width(90.dp)
                                .padding(end = 10.dp)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            if (isSystemInDarkTheme()) Color.White.copy(
                                                alpha = 0.10F
                                            ) else Color.Black.copy(
                                                alpha = 0.10F
                                            ),
                                            if (isSystemInDarkTheme()) Color.White.copy(
                                                alpha = 0.15F
                                            ) else Color.Black.copy(
                                                alpha = 0.15F
                                            )

                                        )
                                    ), shape = RoundedCornerShape(20)
                                ),
                            contentAlignment = Alignment.Center

                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceEvenly,
                                horizontalAlignment = Alignment.CenterHorizontally,

                                ) {

                                Text(
                                    DateTimeFormatter.ofPattern("HH:mm").format(
                                        LocalDateTime.ofInstant(
                                            Instant.ofEpochMilli(1000 * it.time!!.toLong()),
                                            ZoneId.systemDefault()
                                        )
                                    ),
                                    style = MaterialTheme.typography.caption.copy(color = MaterialTheme.colors.primary)
                                )
                                Image(
                                    painter = painterResource(id = getWeatherIcon(it.icon!!)),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .height(40.dp)
                                        .width(40.dp)

                                )
                                Text(
                                    "${it.temperature!!.toInt()}°",
                                    style = MaterialTheme.typography.body2.copy(
                                        shadow = Shadow(
                                            color = Color.Black
                                        )
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
        item {
            BannerAdView()
        }
        item {
            Text("Currently", style = MaterialTheme.typography.h4)
        }
        item {

            Box(modifier = Modifier.fillMaxWidth()) {

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {


                    item {
                        Box(
                            modifier = Modifier
                                .height(100.dp)
                                .width(80.dp)
                                .background(
                                    color = blue_700.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .height(100.dp)
                                    .width(80.dp),
                                verticalArrangement = Arrangement.SpaceEvenly,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.humidity),
                                    contentDescription = "",
                                    modifier = Modifier.size(25.dp)
                                )
                                Text(
                                    "${(100 * locationData.currently.humidity!!).toInt()}%",
                                    style = MaterialTheme.typography.h4
                                )
                            }
                        }
                    }
                    item {
                        Box(
                            modifier = Modifier
                                .height(100.dp)
                                .width(80.dp)
                                .background(
                                    color = when (locationData.currently.uvIndex) {
                                        0, 1, 2 -> Color.Green.copy(alpha = 0.3f)
                                        3, 4, 5 -> Color.Yellow.copy(alpha = 0.3f)
                                        6, 7 -> orange_500.copy(alpha = 0.3f)
                                        else -> Color.Red.copy(alpha = 0.3f)
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                )
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .height(100.dp)
                                    .width(80.dp),
                                verticalArrangement = Arrangement.SpaceEvenly,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.uv),
                                    contentDescription = "",
                                    modifier = Modifier.size(25.dp)
                                )
                                Text(
                                    "${locationData.currently.uvIndex}",
                                    style = MaterialTheme.typography.h4
                                )
                            }
                        }

                    }
                    item {
                        Box(
                            modifier = Modifier
                                .height(100.dp)
                                .width(80.dp)
                                .background(
                                    color = blue_700.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .height(100.dp)
                                    .width(80.dp),
                                verticalArrangement = Arrangement.SpaceEvenly,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.hot),
                                    contentDescription = "",
                                    modifier = Modifier.size(25.dp)
                                )
                                Text(
                                    "${locationData.currently.apparentTemperature!!.toInt()}°",
                                    style = MaterialTheme.typography.h4
                                )
                            }
                        }
                    }
                    item {
                        Box(
                            modifier = Modifier
                                .height(100.dp)
                                .width(80.dp)
                                .background(
                                    color = blue_700.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .height(100.dp)
                                    .width(80.dp),
                                verticalArrangement = Arrangement.SpaceEvenly,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.sunrise),
                                    contentDescription = "",
                                    modifier = Modifier.size(25.dp)
                                )
                                Text(

                                    SimpleDateFormat("HH:mm").format(1000 * locationData.daily.data[0].sunriseTime!!.toLong()),
                                    style = MaterialTheme.typography.h4
                                )
                            }
                        }
                    }
                    item {
                        Box(
                            modifier = Modifier
                                .height(100.dp)
                                .width(80.dp)
                                .background(
                                    color = blue_700.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .height(100.dp)
                                    .width(80.dp),
                                verticalArrangement = Arrangement.SpaceEvenly,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.sunset),
                                    contentDescription = "",
                                    modifier = Modifier.size(25.dp)
                                )
                                Text(
                                    SimpleDateFormat("HH:mm").format(1000 * locationData.daily.data[0].sunsetTime!!.toLong()),
                                    style = MaterialTheme.typography.h4
                                )
                            }
                        }
                    }
                }


            }


        }

        item {
            Text("Rain probability", style = MaterialTheme.typography.h4)
        }
        item {
            RainTimes(
                rainProbability = locationData.hourly.data,
                rainProbabilityDaily = locationData.daily.data
            )
        }
        item {
            BannerAdView()
        }
        item {
            Text("Weekly forecast", style = MaterialTheme.typography.h4)
        }
        item {
            WeeklyTimes(data = locationData.daily.data, units = units)
        }
        item {
            Spacer(modifier = Modifier.height(40.dp))
        }

    }
}