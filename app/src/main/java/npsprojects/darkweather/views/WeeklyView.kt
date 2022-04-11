package npsprojects.darkweather.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import npsprojects.darkweather.getWeatherImage
import npsprojects.darkweather.models.Daily
import npsprojects.darkweather.ui.theme.DarkWeatherTheme
import npsprojects.darkweather.ui.theme.frosted
import npsprojects.darkweather.ui.theme.iceBlack
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Composable
fun WeeklyView(days:List<Daily>,inSi:Boolean){

    Box(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
      .background(color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(16.dp))
        .padding(16.dp)){

      Column() {
          days.forEach{
              WeeklyItem(it,inSi)
          }
      }
    }


}

@Composable
fun WeeklyItem(day:Daily,inSi: Boolean) {
    Row(
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(    DateTimeFormatter.ofPattern("EEE").format(
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(1000 * day.dt!!),
                ZoneId.systemDefault()
            )
        ), style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray), modifier = Modifier.width(80.dp))
        Image(painter = painterResource(id = getWeatherImage(day.weather.first().icon ?: "02n")), contentDescription = "",
            modifier = Modifier.size(40.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
             modifier = Modifier.width(170.dp)
        ) {

            Text(day.temp!!.max!!.roundToInt().toString() + "°", style = MaterialTheme.typography.bodyLarge.copy(color = if(isSystemInDarkTheme()) Color.White else Color.Black))
            Text(day.temp!!.min!!.roundToInt().toString() + "°", style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray))

            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                ColoredIcon(
                    Icons.Filled.Navigation,
                    contentDescription = "",
                    modifier = Modifier
                        .size(15.dp)
                        .rotate(day.wind_deg!!.toFloat()),
                    tint = Color.Gray,

                    )

                Text(
                    text = "${day.wind_speed!!.roundToInt()} " + if (inSi) "km/h" else "mph" ,
                    style = MaterialTheme.typography.labelMedium.copy(color = if(isSystemInDarkTheme()) Color.White else Color.Black)
                )
            }
        }
    }
}

//@Preview
//@Composable
//fun WeeklyPreview(){
//    DarkWeatherTheme {
//        WeeklyView()
//    }
//}