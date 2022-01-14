package npsprojects.darkweather.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
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
        .background(color = Color.White, shape = RoundedCornerShape(16.dp))
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
            .height(45.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(    DateTimeFormatter.ofPattern("EEE").format(
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(1000 * day.dt),
                ZoneId.systemDefault()
            )
        ), style = MaterialTheme.typography.caption.copy(color = Color.Gray))
        Image(painter = painterResource(id = getWeatherImage("02n")), contentDescription = "",
            modifier = Modifier.size(40.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(day.temp.max.roundToInt().toString() + "°", style = MaterialTheme.typography.body1)
            Text(day.temp.min.roundToInt().toString() + "°", style = MaterialTheme.typography.body2.copy(color = Color.Gray))
            Divider(modifier = Modifier
                .width(1.dp)
                .height(25.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                ColoredIcon(
                    Icons.Filled.Navigation,
                    contentDescription = "",
                    modifier = Modifier
                        .size(15.dp)
                        .rotate(day.wind_deg.toFloat()),
                    tint = Color.Gray,

                    )

                Text(
                    text = "${day.wind_speed.roundToInt()} " + if (inSi) "km/h" else "mph" ,
                    style = MaterialTheme.typography.caption
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