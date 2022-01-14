package npsprojects.darkweather.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import compose.icons.WeatherIcons
import compose.icons.weathericons.MoonFirstQuarter
import npsprojects.darkweather.moonDescription
import npsprojects.darkweather.moonIcon
import npsprojects.darkweather.ui.theme.DarkWeatherTheme
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun MoonView(phase:Double,moonrise:Long,moonset:Long){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(color = Color.White, shape = RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ){
            Column(horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Icon(
                        WeatherIcons.MoonFirstQuarter, contentDescription = "",
                        modifier = Modifier.size(15.dp), tint = Color.Gray)
                    Text("Moon", style = MaterialTheme.typography.caption.copy(color = Color.Gray))

                }
                Text(stringResource(id = moonDescription(phase)), style = MaterialTheme.typography.h2)

            }
          Image(painter = painterResource(id = moonIcon(phase)), contentDescription = "",
          modifier = Modifier.size(70.dp))
        }
        Divider()
        Text("The moon rises at ${ DateTimeFormatter.ofPattern("HH:mm").format(
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(1000 * moonrise),
                ZoneId.systemDefault()
            ))} and sets at ${ DateTimeFormatter.ofPattern("HH:mm").format(
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(1000 * moonset),
                ZoneId.systemDefault()
            ))}", style = MaterialTheme.typography.caption.copy(color = Color.Gray))

    }
}

//@Preview
//@Composable
//fun MoonViewPreview(){
//DarkWeatherTheme {
//    MoonView()
//}
//}