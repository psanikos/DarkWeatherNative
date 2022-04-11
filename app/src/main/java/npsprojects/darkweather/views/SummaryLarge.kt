package npsprojects.darkweather.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.FontAwesomeIcons
import compose.icons.WeatherIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.*
import compose.icons.weathericons.Sunrise
import compose.icons.weathericons.Sunset
import npsprojects.darkweather.R
import npsprojects.darkweather.getWeatherImage
import npsprojects.darkweather.models.Current
import npsprojects.darkweather.models.Daily
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Composable
fun SummaryLargeCard(current: Current, dayDetails:String,daily: Daily,inSi:Boolean){
    BoxWithConstraints(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp)

        ) {
            Box(modifier = Modifier.fillMaxWidth().height(130.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Top
                ) {
                    Image(
                        painter = painterResource(id = getWeatherImage(current.weather.first().icon!!)),
                        contentDescription = "",
                        modifier = Modifier.padding(top = 0.dp).size(90.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Column(
                        modifier = Modifier.padding(top = 12.dp).width(100.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            current.weather.first().description!!,
                            style = MaterialTheme.typography.labelSmall.copy(if (isSystemInDarkTheme()) Color.White else Color.Black)
                        )
                        Text(
                            current.temp!!.roundToInt().toString() + "°",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                                fontSize = 34.sp
                            )
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            Icon(
                                FontAwesomeIcons.Solid.Umbrella, contentDescription = "",
                                modifier = Modifier.size(15.dp), tint = Color.Gray
                            )
                            Text(
                                ((current.pop?.times(100)) ?: 0.0).roundToInt().toString() + "%",
                                style = MaterialTheme.typography.labelMedium.copy(color = if (isSystemInDarkTheme()) Color.White else Color.Black)
                            )
                        }


                    }
                    Divider(
                        modifier = Modifier
                            .width(1.dp)
                            .height(100.dp)
                            .padding(2.dp)
                    )


                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        item {
                            DetailsItem(
                                icon = FontAwesomeIcons.Solid.Tint,
                                stringResource(id = R.string.humidity),
                                value = current.humidity.toString() + "%",
                                null
                            )
                        }
                        item {
                            DetailsItem(
                                icon = FontAwesomeIcons.Solid.Child,
                                stringResource(id = R.string.Feels),
                                value = current.feels_like!!.roundToInt().toString() + "°",
                                null
                            )
                        }
                        item {
                            DetailsItem(
                                icon = FontAwesomeIcons.Solid.ArrowAltCircleUp,
                                stringResource(id = R.string.highT),
                                value = daily.temp!!.max!!.roundToInt().toString() + "°",
                                null
                            )
                        }
                        item {
                            DetailsItem(
                                icon = FontAwesomeIcons.Solid.ArrowAltCircleDown,
                                stringResource(id = R.string.lowT),
                                value = daily.temp!!.min!!.roundToInt().toString() + "°",
                                null
                            )

                        }
                        item {
                            DetailsItem(
                                icon = FontAwesomeIcons.Solid.Sun,
                                stringResource(id = R.string.uv),
                                value = current.uvi!!.roundToInt().toString(),
                                null
                            )

                        }
                        item {
                            DetailsItem(
                                icon = Icons.Default.Navigation,
                                stringResource(id = R.string.airSpeed),
                                value = current.wind_speed!!.roundToInt()
                                    .toString() + if (inSi) "km/h" else "mph",
                                secondValue = current.wind_deg!!.toDouble()
                            )

                        }
                        item {
                            DetailsItem(
                                icon = WeatherIcons.Sunrise,
                                stringResource(id = R.string.sunrise),
                                value = DateTimeFormatter.ofPattern("HH:mm").format(
                                    LocalDateTime.ofInstant(
                                        Instant.ofEpochMilli(1000 * daily.sunrise!!),
                                        ZoneId.systemDefault()
                                    )
                                ),
                                null
                            )

                        }
                        item {
                            DetailsItem(
                                icon = WeatherIcons.Sunset,
                                stringResource(id = R.string.Sunset),
                                DateTimeFormatter.ofPattern("HH:mm").format(
                                    LocalDateTime.ofInstant(
                                        Instant.ofEpochMilli(1000 * daily.sunset!!),
                                        ZoneId.systemDefault()
                                    )
                                ),
                                null
                            )

                        }
                    }


                }
            }
            Text(
                dayDetails,
                style = MaterialTheme.typography.labelMedium.copy(color = if (isSystemInDarkTheme()) Color.White else Color.Black),
                modifier = Modifier.padding(10.dp)
            )

        }
    }

}