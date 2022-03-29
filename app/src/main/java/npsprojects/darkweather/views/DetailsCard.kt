package npsprojects.darkweather.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import compose.icons.FontAwesomeIcons
import compose.icons.WeatherIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.*
import compose.icons.weathericons.Sunrise
import compose.icons.weathericons.Sunset
import npsprojects.darkweather.R
import npsprojects.darkweather.models.Current
import npsprojects.darkweather.models.Daily
import npsprojects.darkweather.ui.theme.DarkWeatherTheme
import npsprojects.darkweather.ui.theme.frosted
import npsprojects.darkweather.ui.theme.iceBlack
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DetailsCard(current: Current,daily: Daily,inSi:Boolean){
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(170.dp)
        .background(color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(16.dp))
        .padding(12.dp), contentAlignment = Alignment.Center) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DetailsCardCompact(current: Current,daily: Daily,inSi:Boolean){

    Column(modifier = Modifier
        .fillMaxWidth()
        .height(170.dp)
        .background(color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(16.dp))
        .padding(12.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top

    ) {

            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Box(modifier = Modifier.width(100.dp)) {
                    DetailsItemCompact(
                        icon = FontAwesomeIcons.Solid.ArrowAltCircleUp,
                        stringResource(id = R.string.highT),
                        value = daily.temp!!.max!!.roundToInt().toString() + "°",
                        null
                    )
                }
                Box(modifier = Modifier.width(100.dp)) {
                    DetailsItemCompact(
                        icon = FontAwesomeIcons.Solid.ArrowAltCircleDown,
                        stringResource(id = R.string.lowT),
                        value = daily.temp!!.min!!.roundToInt().toString() + "°",
                        null
                    )
                }
                Box(modifier = Modifier.width(100.dp)) {

                    DetailsItemCompact(
                        icon = FontAwesomeIcons.Solid.Sun,
                        stringResource(id = R.string.uv),
                        value = current.uvi!!.roundToInt().toString(),
                        null
                    )
                }
            }
            Divider()
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Box(modifier = Modifier.width(100.dp)) {

                    DetailsItemCompact(
                        icon = Icons.Default.Navigation,
                        stringResource(id = R.string.airSpeed),
                        value = current.wind_speed!!.roundToInt()
                            .toString() + if (inSi) "km/h" else "mph",
                        secondValue = current.wind_deg!!.toDouble()
                    )
                }
                Box(modifier = Modifier.width(100.dp)) {

                    DetailsItemCompact(
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
                Box(modifier = Modifier.width(100.dp)) {

                    DetailsItemCompact(
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


@Composable
fun DetailsItem(icon:ImageVector,title:String,value:String,secondValue:Double?){
    Column(modifier = Modifier
        .height(70.dp)
        .fillMaxWidth()
        .padding(5.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Icon(
                icon, contentDescription = "",
                modifier = Modifier
                    .size(15.dp)
                    .rotate((secondValue ?: 0.0).toFloat()), tint = Color.Gray)
            Text(title, style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray))

        }
        Text(value, style = MaterialTheme.typography.displayLarge.copy(color = if(isSystemInDarkTheme()) Color.White else Color.Black))
    }
}
@Composable
fun DetailsItemCompact(icon:ImageVector,title:String,value:String,secondValue:Double?){
    Column(modifier = Modifier
        .height(50.dp)
        .fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Icon(
                icon, contentDescription = "",
                modifier = Modifier
                    .size(15.dp)
                    .rotate((secondValue ?: 0.0).toFloat()), tint = Color.Gray)
            Text(title, style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray))

        }
        Text(value, style = MaterialTheme.typography.displayLarge.copy(color = if(isSystemInDarkTheme()) Color.White else Color.Black))
    }
}

//@Preview
//@Composable
//fun DetailsPreview(){
//    DarkWeatherTheme {
//        DetailsCard()
//    }
//}