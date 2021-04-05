package npsprojects.darkweather

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import npsprojects.darkweather.ui.theme.blue_500
import npsprojects.darkweather.ui.theme.blue_grey_500
import npsprojects.darkweather.ui.theme.red_800
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
import kotlin.math.roundToLong

enum class RainTimeCategory{
    HOURLY,DAILY
}

@Composable
fun WeeklyTimes(data:List<Data>,units: WeatherUnits) {



    Surface(
        contentColor = MaterialTheme.colors.primary, modifier = Modifier
            .fillMaxWidth()
         ,
        color = if (isSystemInDarkTheme()) Color(0xFF202020).copy(alpha = 0.5F) else Color.White.copy(alpha = 0.5F)

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {


            Column() {
                data.forEach {
                    WeeklyTile(data = it,units = units)
                }
            }
        }
    }
}

@Composable
fun WeeklyTile(data: Data,units: WeatherUnits){
    Box(){
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(45.dp),horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text(
                DateTimeFormatter.ofPattern("EEEE").format(
                    LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(
                (1000*data.sunsetTime!!).toLong()
            ), ZoneId.systemDefault())), style = MaterialTheme.typography.body2,modifier = Modifier.fillMaxWidth(0.25F))
            Row(modifier = Modifier
                .fillMaxWidth(0.85F)
                .height(40.dp),horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically){
                Image(
                    painter = painterResource(id = getWeatherIcon(data.icon!!)), contentDescription = "",
                    modifier = Modifier
                        .height(25.dp)
                        .width(25.dp)
                )
                Icon(Icons.Filled.ArrowUpward,contentDescription = "",tint = Color.Red,modifier = Modifier.scale(0.7f))
                Text(data.temperatureHigh!!.roundToInt().toString(), style = MaterialTheme.typography.caption.copy(color = Color.Red))
                Icon(Icons.Filled.ArrowDownward,contentDescription = "",tint = blue_grey_500,modifier = Modifier.scale(0.7f))

                Text(data.temperatureLow!!.roundToInt().toString(), style = MaterialTheme.typography.caption.copy(color = blue_grey_500))
                Spacer(modifier = Modifier.width(5.dp))
                Image(
                    painter = painterResource(id = R.drawable.raining), contentDescription = "",
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colors.primary),
                    modifier = Modifier.size(16.dp)
                )

                Text("${(100*data.precipProbability!!).roundToInt()}%", style = MaterialTheme.typography.caption)

                Icon(Icons.Filled.Air,contentDescription = "",tint = MaterialTheme.colors.primary, modifier = Modifier.size(16.dp))
                Text("${data.windSpeed!!.roundToLong()} " + if(units == WeatherUnits.US) "mph" else "km/h", style = MaterialTheme.typography.caption)

            }
        }
    }
}

@Composable
fun RainTimes(rainProbability:List<DataX>, rainProbabilityDaily:List<Data>){

    var category: RainTimeCategory by remember { mutableStateOf(RainTimeCategory.HOURLY) }

    Surface(
         modifier = Modifier

            .fillMaxWidth()
           ,
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

            Row(modifier = Modifier
                .padding(vertical = 5.dp)
                .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start) {
                Button(onClick = {
                    category = RainTimeCategory.HOURLY
                },
                    modifier = Modifier
                        .width(140.dp)
                        .height(34.dp),shape = RoundedCornerShape(30),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if(category == RainTimeCategory.HOURLY) if(isSystemInDarkTheme())  Color.Black else Color.DarkGray else Color.LightGray
                    ,contentColor = if(category == RainTimeCategory.HOURLY)  Color.White else Color.DarkGray)

                ) {

                    Text("Hourly",style = MaterialTheme.typography.button)


                }
                Spacer(modifier = Modifier.width(10.dp))
                Button( onClick = {
                    category = RainTimeCategory.DAILY
                },
                    modifier = Modifier
                        .width(140.dp)
                        .height(34.dp),
                    shape = RoundedCornerShape(30),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if(category == RainTimeCategory.DAILY) if(isSystemInDarkTheme())  Color.Black else Color.DarkGray else Color.LightGray,
                        contentColor =    if(category == RainTimeCategory.DAILY)  Color.White else Color.DarkGray,

                        )

                ) {

                    Text("Daily", style = MaterialTheme.typography.button)

                }
            }



            when(category){
                RainTimeCategory.HOURLY ->          LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,

                    ) {
                    itemsIndexed(rainProbability) { index, item ->
                        RainMeter(index = index, value = item.precipProbability!!,time = item.time!!.toLong(),weekly = false)
                    }
                }
                RainTimeCategory.DAILY ->          LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,

                    ) {
                    itemsIndexed(rainProbabilityDaily) { index, item ->
                        RainMeter(index = index, value = item.precipProbability!!,time = item.time!!.toLong(),weekly = true)
                    }
                }
            }
        }

    }
}

@Composable
fun RainMeter(index:Int,value:Double,time:Long,weekly:Boolean){

    val target = (value*140).roundToInt()

    var isLoaded by remember { mutableStateOf(false) }
    val height: Dp by animateDpAsState(targetValue = if (isLoaded) target.dp else 0.dp,
        animationSpec = tween(durationMillis = 500 + (index*150), easing = FastOutSlowInEasing)
    )

    var barColor = remember { Animatable(blue_500) }



    LaunchedEffect(key1 = "Meter"){
        delay(500)
        isLoaded = true
        delay(800)
        barColor.animateTo(if (value > 0.65) red_800 else blue_500)
    }

    Column(modifier = Modifier
        .height(220.dp)
        .padding(end = 10.dp),verticalArrangement = Arrangement.SpaceEvenly) {
        Box(contentAlignment = Alignment.Center,modifier = Modifier
            .height(140.dp)
            .width(28.dp)
            .background(color = Color.LightGray, shape = RoundedCornerShape(30))
            .clip(shape = RoundedCornerShape(30))) {
            Column(modifier = Modifier.fillMaxHeight(),verticalArrangement = Arrangement.Bottom) {
                Box(modifier = Modifier
                    .height(height)
                    .width(28.dp)
                    .background(
                        color = barColor.value,
                        shape = RoundedCornerShape(bottomEndPercent = 30, bottomStartPercent = 30)

                    ))
            }

            Text("${(100*value).roundToInt()}%",style = MaterialTheme.typography.caption.copy(Color.DarkGray,fontSize = 11.sp))

        }
        Text(if (weekly) DateTimeFormatter.ofPattern("EEE").format(LocalDateTime.ofInstant(Instant.ofEpochMilli(1000*time), ZoneId.systemDefault())) else DateTimeFormatter.ofPattern("HH:mm").format(
            LocalDateTime.ofInstant(Instant.ofEpochMilli(1000*time), ZoneId.systemDefault())), style = MaterialTheme.typography.caption)

    }
}
