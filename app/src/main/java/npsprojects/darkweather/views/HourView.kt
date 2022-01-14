package npsprojects.darkweather.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Umbrella
import npsprojects.darkweather.getWeatherImage
import npsprojects.darkweather.models.Current
import npsprojects.darkweather.ui.theme.DarkWeatherTheme
import npsprojects.darkweather.ui.theme.indigo_100
import npsprojects.darkweather.ui.theme.indigo_500
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Composable
fun HourView(hourly:List<Current>,inSi:Boolean){
   LazyRow(content = {
       items(hourly){ hour->
           HourItem(isFirst = hourly.indexOf(hour) == 0, hour = hour, inSi = inSi)
       }
   }, modifier = Modifier
       .fillMaxWidth()
       .height(160.dp).clip(RoundedCornerShape(16.dp)), horizontalArrangement = Arrangement.spacedBy(0.dp),
   verticalAlignment = Alignment.CenterVertically)

}

@Composable
fun HourItem(isFirst:Boolean,hour:Current,inSi: Boolean){
    Column(modifier = Modifier
        .fillMaxHeight()
        .width(90.dp).background(color = if(isFirst) Color.White else indigo_500),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            DateTimeFormatter.ofPattern("HH:mm").format(
                                LocalDateTime.ofInstant(
                                    Instant.ofEpochMilli(1000 * hour.dt),
                                    ZoneId.systemDefault()
                                )), style = MaterialTheme.typography.caption.copy(color = if(isFirst) Color.Gray else Color.LightGray))
        Text(hour.temp.roundToInt().toString() + "°", style = MaterialTheme.typography.h3.copy(color = if(isFirst) Color.Black else Color.White))
        Image(painter = painterResource(id = getWeatherImage(hour.weather.first().icon)), contentDescription ="",
        modifier = Modifier.size(30.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Icon(
                FontAwesomeIcons.Solid.Umbrella, contentDescription = "",
                modifier = Modifier.size(15.dp), tint = if(isFirst) Color.Gray else Color.LightGray)
            Text(((hour.pop ?: 0.0)*100).roundToInt().toString() + "%", style = MaterialTheme.typography.caption.copy(color = if(isFirst) Color.Gray else Color.LightGray))
        }
        Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {


                        ColoredIcon(
                            Icons.Filled.Navigation,
                            contentDescription = "",
                            modifier = Modifier
                                .size(15.dp)
                                .rotate(22.0F),
                            tint = if(isFirst) Color.Gray else Color.LightGray,

                            )

                        Text(
                            text = "${hour.wind_speed.roundToInt()} " + if (inSi) "km/h" else "mph" ,
                            style = MaterialTheme.typography.caption.copy(color = if(isFirst) Color.Gray else Color.LightGray)
                        )
                    }

    }
}

//@Preview
//@Composable
//fun HourViewPreview(){
//    DarkWeatherTheme {
//        HourView()
//    }
//}