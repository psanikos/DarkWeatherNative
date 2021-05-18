package npsprojects.darkweather

import android.text.Layout
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.twotone.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import npsprojects.darkweather.ui.theme.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class ChartData constructor(
    val index: Int,
    val offsets: Offset,
    val time: Long,
    val displayedValue: String
)



@Composable
fun RainTimeAlert(rainProbability: List<DataX>) {
    var category: RainTimeCategory by remember { mutableStateOf(RainTimeCategory.HOURLY) }
    var timeUntilRain: Long? = null
    var timeUntilEnd: Long? = null
    var firstRainTimeIndex: Int? = rainProbability.indexOfFirst { it.precipProbability!! >= 0.5 }
    var precip = "rain"
    if (firstRainTimeIndex != null && firstRainTimeIndex >= 0) {
        timeUntilRain =
            (1000 * rainProbability[firstRainTimeIndex].time!!.toLong() - Calendar.getInstance().timeInMillis)
        precip = rainProbability[firstRainTimeIndex].precipType ?: "rain"
    }
    if (firstRainTimeIndex != null && firstRainTimeIndex >= 0) {
        rainProbability.forEachIndexed { index, item ->
            if (index > firstRainTimeIndex) {
                if (timeUntilEnd == null) {
                    if (item.precipProbability!! <= 0.4) {
                        timeUntilEnd =
                            1000 * item.time!!.toLong() - Calendar.getInstance().timeInMillis
                        precip = item.precipType ?: "rain"
                    }
                }
            }
        }
    }

        if (timeUntilRain != null) {
            Box(
                modifier = Modifier
                    .width(310.dp)
                    .height(80.dp)
                    .background(
                        color = indigo_500.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {


                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.TwoTone.Warning,
                            tint = indigo_800,
                            contentDescription = "",
                            modifier = Modifier
                                .padding(6.dp)
                                .size(30.dp),
                        )
                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .padding(6.dp)
                                .height(90.dp)
                        ) {
                            Text(
                                if(precip == "rain") stringResource(id = R.string.RainAlert) else stringResource(
                                    id = R.string.SnowAlert
                                ),
                                style = MaterialTheme.typography.h4.copy(fontSize = 14.sp)
                            )
                        if (timeUntilRain > 0) {
                            Text(
                                (if(precip == "rain") stringResource(id = R.string.RainStarts) else stringResource(id = R.string.SnowStarts)) +
                                        String.format(
                                            "%d h, %d min",
                                            TimeUnit.MILLISECONDS.toHours(timeUntilRain),
                                            TimeUnit.MILLISECONDS.toMinutes(timeUntilRain) -
                                                    TimeUnit.HOURS.toMinutes(
                                                        TimeUnit.MILLISECONDS.toHours(
                                                            timeUntilRain
                                                        )
                                                    )
                                        ),
                                style = MaterialTheme.typography.button.copy(fontSize = 11.sp),
                                modifier = Modifier.padding(vertical = 5.dp),

                            )
                        } else {
                            if (timeUntilEnd != null) {
                                Text(
                                    (if(precip == "rain") stringResource(id = R.string.RainEnds) else stringResource(id = R.string.RainEnds)) +
                                            String.format(
                                                "%d h, %d min",
                                                TimeUnit.MILLISECONDS.toHours(timeUntilEnd!!),
                                                TimeUnit.MILLISECONDS.toMinutes(timeUntilEnd!!) -
                                                        TimeUnit.HOURS.toMinutes(
                                                            TimeUnit.MILLISECONDS.toHours(
                                                                timeUntilEnd!!
                                                            )
                                                        )
                                            ),
                                    style = MaterialTheme.typography.button.copy(fontSize = 11.sp),
                                    modifier = Modifier.padding(vertical = 5.dp),
                                )
                            } else {
                                Text(
                                    if(precip == "rain") stringResource(id = R.string.RainContinue) else stringResource(id = R.string.RainContinue),
                                    style = MaterialTheme.typography.body2,
                                    modifier = Modifier.padding(vertical = 5.dp)
                                )
                            }
                        }
                            Text("")
                    }
                }
            }
        }

}


@Composable
fun HourlyView(model: WeatherViewModel,index: Int){
    val cardColor =  if (isSystemInDarkTheme()) Color(0xFF101010) else Color.White

    LazyRow(
        modifier = Modifier.fillMaxWidth()


    ) {

        model.locations[index].data.hourly.data.forEach {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if(DateTimeFormatter.ofPattern("HH:mm").format(
                            LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(1000 * it.time!!.toLong()),
                                ZoneId.systemDefault()
                            )
                        ) == "00:00"){
                        Text(DateTimeFormatter.ofPattern("EEEE").format(
                            LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(1000 * it.time!!.toLong()),
                                ZoneId.systemDefault()
                            )
                        ),style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold))
                    }

                    Box(
                        modifier = Modifier
                            .height(140.dp)
                            .width(100.dp)
                            .padding(end = 10.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        if (!isSystemInDarkTheme()) Color.White
                                        else Color.Black,
                                        if (!isSystemInDarkTheme()) Color.White.copy(
                                            alpha = 0.55F
                                        ) else Color.Black.copy(
                                            alpha = 0.55F
                                        )

                                    )
                                ), shape = RoundedCornerShape(20.dp)
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
                                style = MaterialTheme.typography.caption
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.drop),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .size(16.dp),
                                    colorFilter = ColorFilter.tint(color = indigo_500)
                                )
                                Text(
                                    "${(100 * it.precipProbability!!).roundToInt()}%",
                                    style = MaterialTheme.typography.caption
                                )
                            }
                            Image(
                                painter = painterResource(id = getWeatherIcon(it.icon!!)),
                                contentDescription = "",
                                modifier = Modifier
                                    .height(40.dp)
                                    .width(40.dp)

                            )
                            Text(
                                "${it.temperature!!.toInt()}°",
                                style = MaterialTheme.typography.body1.copy(
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
}

@Composable
fun WeatherMain(model: WeatherViewModel,index:Int){
    val cardColor =  if (isSystemInDarkTheme()) Color(0xFF101010) else Color.White

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(
                color = cardColor,
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {


            Box(modifier = Modifier.height(120.dp)) {
                Column(horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxHeight()) {


                    Text(
                        text = "${model.locations[index].data.currently.temperature!!.toInt()}°",
                        style = MaterialTheme.typography.h1.copy(
                            fontSize = 34.sp,
                            shadow = Shadow(
                                color = Color.Black,
                                offset = Offset(0.5f, 0.5f)
                            )
                        ),
                        modifier = Modifier
                            .padding(vertical = 5.dp),
                    )

                    Text(
                        text = model.locations[index].data.currently.summary!!,
                        style = MaterialTheme.typography.body2.copy(
                            shadow = Shadow(
                                color = Color.Black,
                                offset = Offset(0.5f, 0.5f)
                            )
                        ),
                        modifier = Modifier
                            .padding(vertical = 5.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.Feels) + " ${model.locations[index].data.currently.apparentTemperature!!.toInt()}°",
                        style = MaterialTheme.typography.caption.copy(
                            shadow = Shadow(
                                color = Color.Black,
                                offset = Offset(0.5f, 0.5f)
                            ),
                            color = pink_100
                        ),

                        textAlign = TextAlign.Center
                    )
                    Row(

                        modifier = Modifier.width(200.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Row() {
                            Icon(
                                Icons.Filled.ArrowUpward,
                                contentDescription = "",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${model.locations[index].data.daily.data[0].temperatureHigh!!.roundToInt()}°",
                                style = MaterialTheme.typography.caption
                            )
                        }
                        Row() {
                            Icon(
                                Icons.Filled.ArrowDownward,
                                contentDescription = "",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${model.locations[index].data.daily.data[0].temperatureLow!!.roundToInt()}°",
                                style = MaterialTheme.typography.caption
                            )
                        }
                        Row() {
                            Icon(Icons.Filled.Opacity, contentDescription = "", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${(100 * model.locations[index].data.daily.data[0].precipProbability!!).roundToInt()}%",
                                style = MaterialTheme.typography.caption
                            )
                        }
                        Row() {
                            Icon(
                                Icons.Filled.Air,
                                contentDescription = "",

                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${model.locations[index].data.currently.windSpeed!!.roundToInt()} " + if (model.units == WeatherUnits.US) "mph" else "km/h",
                                style = MaterialTheme.typography.caption
                            )
                        }
                    }
                }
            }


            Image(
                painter = painterResource(
                    id = getWeatherIcon(
                        model.locations[index].data.currently.icon ?: ""
                    )
                ),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(90.dp)
                    .width(90.dp)
            )
        }
    }
}