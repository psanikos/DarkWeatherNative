package npsprojects.darkweather.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import compose.icons.FontAwesomeIcons
import compose.icons.WeatherIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.ArrowAltCircleDown
import compose.icons.fontawesomeicons.solid.ArrowAltCircleUp
import compose.icons.fontawesomeicons.solid.Sun
import compose.icons.fontawesomeicons.solid.Umbrella
import compose.icons.weathericons.Sunrise
import compose.icons.weathericons.Sunset
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
    Box(modifier = Modifier.fillMaxWidth().height(170.dp)
        .background(color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(16.dp))
        .padding(12.dp), contentAlignment = Alignment.Center) {
        LazyVerticalGrid(cells = GridCells.Fixed(3), content = {
            item {
                DetailsItem(icon = FontAwesomeIcons.Solid.ArrowAltCircleUp, "High", value = daily.temp.max.roundToInt().toString() + "°",null)
            }
            item {
                DetailsItem(icon = FontAwesomeIcons.Solid.ArrowAltCircleDown, "Low",value = daily.temp.min.roundToInt().toString() + "°",null)

            }
            item {
                DetailsItem(icon = FontAwesomeIcons.Solid.Sun, "UV level", value = current.uvi.roundToInt().toString(),null)

            }
            item {
                DetailsItem(icon = Icons.Default.Navigation, "Air speed", value = current.wind_speed.roundToInt().toString() + if (inSi) "km/h" else "mph", secondValue = current.wind_deg.toDouble())

            }
            item {
                DetailsItem(icon = WeatherIcons.Sunrise, "Sunrise", value = DateTimeFormatter.ofPattern("HH:mm").format(
                                LocalDateTime.ofInstant(
                                    Instant.ofEpochMilli(1000 * daily.sunrise),
                                    ZoneId.systemDefault()
                                )
                            ),null)

            }
            item {
                DetailsItem(icon = WeatherIcons.Sunset, "Sunset",DateTimeFormatter.ofPattern("HH:mm").format(
                    LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(1000 * daily.sunset),
                        ZoneId.systemDefault()
                    )
                ),null)

            }
        })
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
                modifier = Modifier.size(15.dp).rotate((secondValue ?: 0.0).toFloat()), tint = Color.Gray)
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