package npsprojects.darkweather

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import npsprojects.darkweather.ui.theme.*
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


enum class RainTimeCategory {
    HOURLY, DAILY
}

@Composable
fun WeeklyTimes(data: List<Data>, units: WeatherUnits) {

Box(modifier = Modifier
    .fillMaxWidth()
    .background(
        color = if (isSystemInDarkTheme()) Color(0xFF101010) else Color.White,
        shape = RoundedCornerShape(20.dp)
    )){
    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {

        data.forEach {
            WeeklyTile(data = it, units = units)
        }
    }

    }
}

@Composable
fun WeeklyTile(data: Data, units: WeatherUnits) {
    Box() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                SimpleDateFormat("EEEE").format(1000 * data.time!!.toLong()),
                style = MaterialTheme.typography.body2,
                modifier = Modifier.fillMaxWidth(0.25F)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.85F)
                    .height(40.dp), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = getWeatherIcon(data.icon!!)),
                    contentDescription = "",
                    modifier = Modifier
                        .height(25.dp)
                        .width(25.dp)
                )
                Icon(
                    Icons.Filled.ArrowUpward,
                    contentDescription = "",
                    tint = Color.Red,
                    modifier = Modifier.scale(0.7f)
                )
                Text(
                    data.temperatureHigh!!.toInt().toString(),
                    style = MaterialTheme.typography.caption.copy(color = Color.Red)
                )
                Icon(
                    Icons.Filled.ArrowDownward,
                    contentDescription = "",
                    tint = light_blue_100,
                    modifier = Modifier.scale(0.7f)
                )

                Text(
                    data.temperatureLow!!.toInt().toString(),
                    style = MaterialTheme.typography.caption.copy(color = light_blue_100)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Image(
                    painter = painterResource(id = R.drawable.raining), contentDescription = "",
                    colorFilter = ColorFilter.tint(color = Color.LightGray),
                    modifier = Modifier.size(16.dp)
                )

                Text(
                    "${(100 * data.precipProbability!!).toInt()}%",
                    style = MaterialTheme.typography.caption
                )

                Icon(
                    Icons.Filled.Air,
                    contentDescription = "",
                    tint = Color.LightGray,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    "${data.windSpeed!!.round(1)} " + if (units == WeatherUnits.US) "mph" else "km/h",
                    style = MaterialTheme.typography.caption
                )

            }
        }
    }
}

@Composable
fun AirQualityView(index:Int){
    val color = when(index){
        1-> teal_500
        2-> yellow_500
        3-> orange_500
        4-> pink_500
        else -> red_500
    }
    val name = when(index){
        1-> stringResource(id = R.string.Good)
        2-> stringResource(id = R.string.Fair)
        3-> stringResource(id = R.string.Moderate)
        4-> stringResource(id = R.string.Poor)
        else -> stringResource(id = R.string.VeryPoor)
    }
    val summary = when(index){
        1-> stringResource(id = R.string.GoodText)
        2-> stringResource(id = R.string.FairText)
        3-> stringResource(id = R.string.ModerateText)
        4-> stringResource(id = R.string.PoorText)
        else -> stringResource(id = R.string.VeryPoorText)
    }
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(160.dp)
        .background(
            color = if (isSystemInDarkTheme()) Color.Black else Color.White,
            shape = RoundedCornerShape(20.dp)
        )){
        Column(modifier = Modifier.fillMaxSize(),horizontalAlignment = Alignment.Start,verticalArrangement = Arrangement.SpaceBetween) {
          Row(modifier = Modifier
              .padding(top = 10.dp, start = 10.dp, end = 10.dp)
              .fillMaxWidth(),verticalAlignment = Alignment.CenterVertically,horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(Icons.Filled.Air,contentDescription = "")
            Text(stringResource(R.string.AirQ),style = MaterialTheme.typography.caption)
          }
            Row(modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth(),verticalAlignment = Alignment.CenterVertically,horizontalArrangement = Arrangement.Start) {

                    Text("$index",style = MaterialTheme.typography.h1.copy(fontWeight = FontWeight.Black,color = color),
                        modifier = Modifier.padding(end = 10.dp))

                Column(horizontalAlignment = Alignment.Start,verticalArrangement = Arrangement.Top) {

                    Text(name, style = MaterialTheme.typography.body1.copy(color = Color.Gray,fontWeight = FontWeight.Bold),modifier = Modifier.padding(bottom = 4.dp))

                    Text(
                        summary,
                        style = MaterialTheme.typography.caption,
                        letterSpacing = 1.1.sp,lineHeight = 14.sp

                    )

                } }
            Text("")

        }
    }
}
@Preview
@Composable fun Previews() {
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .background(color = Color(0xFFEFEFEF))) {
        AirQualityView(index = 5)
    }
}