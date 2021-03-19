package com.npsappprojects.darkweather

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.npsappprojects.darkweather.ui.theme.blue_500
import com.npsappprojects.darkweather.ui.theme.blue_grey_200
import com.npsappprojects.darkweather.ui.theme.blue_grey_500
import com.npsappprojects.darkweather.ui.theme.red_800
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
import kotlin.math.roundToLong


@Composable
fun MainWeatherCard(locationData:WeatherResponse,locationName:String,isCurrent:Boolean){
   Surface(modifier = Modifier.fillMaxSize(),color = getWeatherColor("clear-night"),
       contentColor = Color.White) {

 LazyColumn(modifier = Modifier.fillMaxSize(),horizontalAlignment = Alignment.CenterHorizontally
     ,contentPadding = PaddingValues(top = 20.dp)) {

     item {
         Box(modifier = Modifier.padding(vertical = 10.dp)) {
             Image(
                 painter = painterResource(id = getWeatherIcon(locationData.currently.icon!!)),
                 contentDescription = "",
                 modifier = Modifier
                     .height(180.dp)
                     .width(180.dp)

             )
         }
     }
     item {
         Text(
             text = "${locationData.currently.summary!!}",
             style = MaterialTheme.typography.body1,
             modifier = Modifier
                 .padding(horizontal = 20.dp, vertical = 10.dp),
             textAlign = TextAlign.Center
         )
     }
     item {
         Text(
             text = "${locationData.currently.temperature!!.roundToInt()}°",
             style = MaterialTheme.typography.h1,
             modifier = Modifier
                 .padding(horizontal = 20.dp, vertical = 10.dp)
         )
     }

     item {
         Row(
             modifier = Modifier
                 .fillMaxWidth()
                 .padding(vertical = 20.dp),
             horizontalArrangement = Arrangement.SpaceEvenly,

             ) {
             Row() {
                 Icon(Icons.Filled.ArrowUpward, contentDescription = "")
                 Spacer(modifier = Modifier.width(5.dp))
                 Text(text = "${locationData.daily.data[0].temperatureHigh!!.roundToInt()}°", style = MaterialTheme.typography.body1)
             }
             Row() {
                 Icon(Icons.Filled.ArrowDownward, contentDescription = "")
                 Spacer(modifier = Modifier.width(5.dp))
                 Text(text = "${locationData.daily.data[0].temperatureLow!!.roundToInt()}°", style = MaterialTheme.typography.body1)
             }
             Row() {
                 Image(
                     painter = painterResource(id = R.drawable.raining), contentDescription = "",
                     colorFilter = ColorFilter.tint(color = Color.White)
                 )
                 Spacer(modifier = Modifier.width(5.dp))
                 Text(text = "${(100*locationData.daily.data[0].precipProbability!!).roundToInt()}%", style = MaterialTheme.typography.body1)
             }
             Row() {
                 Icon(Icons.Filled.Air, contentDescription = "")
                 Spacer(modifier = Modifier.width(5.dp))
                 Text(text = "${locationData.currently.windSpeed!!.roundToInt()} km/h", style = MaterialTheme.typography.body1)
             }
         }
     }
     item {
         LazyRow(modifier = Modifier
             .padding(16.dp)
             .padding(vertical = 20.dp)) {
             items(locationData.hourly.data.count()) {
                 locationData.hourly.data.forEach {
                     Box(
                         modifier = Modifier
                             .height(140.dp)
                             .width(100.dp)
                             .padding(end = 10.dp)
                             .background(
                                 brush = Brush.verticalGradient(
                                     colors = listOf(
                                         Color.White.copy(
                                             alpha = 0.3f
                                         ), Color.White.copy(alpha = 0.1f)
                                     )
                                 ), shape = RoundedCornerShape(20)
                             ),
                         contentAlignment = Alignment.Center

                     ) {
                         Column(
                             modifier = Modifier.fillMaxSize(),
                             verticalArrangement = Arrangement.SpaceEvenly,
                             horizontalAlignment = Alignment.CenterHorizontally,

                         ) {

                             Text(DateTimeFormatter.ofPattern("HH:mm").format(LocalDateTime.ofInstant(Instant.ofEpochMilli(1000*it.time!!.toLong()), ZoneId.systemDefault())), style = MaterialTheme.typography.caption.copy(color = Color.LightGray))
                             Image(
                                 painter = painterResource(id = getWeatherIcon(it.icon!!)),
                                 contentDescription = "",
                                 modifier = Modifier
                                     .height(50.dp)
                                     .width(50.dp)
                                     .padding(vertical = 5.dp)
                             )
                             Text("${it.temperature!!.roundToInt()}°", style = MaterialTheme.typography.body2)
                         }
                     }
                 }
             }
         }
     }
     item {
         Spacer(modifier = Modifier.height(40.dp))
     }
     item {
   Surface(
       color = Color.White.copy(alpha = 0.1F)
   ) {
       Column(modifier = Modifier
           .fillMaxWidth()
           .padding(vertical = 10.dp, horizontal = 16.dp)) {
           Text(
               "Today ${locationData.daily.data[0].temperatureMax!!.roundToInt()}° max temperature at ${DateTimeFormatter.ofPattern("HH:mm").format(LocalDateTime.ofInstant(Instant.ofEpochMilli(1000*locationData.daily.data[0].apparentTemperatureMaxTime!!.toLong()), ZoneId.systemDefault()))} and ${locationData.daily.data[0].temperatureMin!!.roundToInt()}° min temperature at ${DateTimeFormatter.ofPattern("HH:mm").format(LocalDateTime.ofInstant(Instant.ofEpochMilli(1000*locationData.daily.data[0].apparentTemperatureMinTime!!.toLong()), ZoneId.systemDefault()))}.",
               style = MaterialTheme.typography.body2,
               textAlign = TextAlign.Center,
               modifier = Modifier.padding(horizontal = 16.dp,vertical = 20.dp)
           )

           Box(modifier = Modifier.fillMaxWidth()) {
               Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.5F)
                        .height(80.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text("Wind", style = MaterialTheme.typography.body2.copy(color = Color.LightGray))
                    Text("${locationData.currently.windSpeed!!.roundToLong()}km/h", style = MaterialTheme.typography.body1)
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.5F)
                        .height(80.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text("Humidity", style = MaterialTheme.typography.body2.copy(color = Color.LightGray))
                    Text("${(100*locationData.currently.humidity!!).roundToInt()}%", style = MaterialTheme.typography.body1)
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.5F)
                        .height(80.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text("UV Index", style = MaterialTheme.typography.body2.copy(color = Color.LightGray))
                    Text("${locationData.currently.uvIndex}", style = MaterialTheme.typography.body1)
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.5F)
                        .height(80.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text("Feels like", style = MaterialTheme.typography.body2.copy(color = Color.LightGray))
                    Text("${locationData.currently.apparentTemperature!!.roundToInt()}°", style = MaterialTheme.typography.body1)
                }
            }
                 Row(modifier = Modifier.fillMaxWidth()){
                     Column(
                         modifier = Modifier
                             .fillMaxWidth(0.5F)
                             .height(80.dp),
                         verticalArrangement = Arrangement.SpaceEvenly
                     ) {
                         Text("Sunrise", style = MaterialTheme.typography.body2.copy(color = Color.LightGray))
                         Text(
                             DateTimeFormatter.ofPattern("HH:mm").format(LocalDateTime.ofInstant(Instant.ofEpochMilli(
                                 (1000*locationData.daily.data[0].sunriseTime!!).toLong()
                             ), ZoneId.systemDefault())), style = MaterialTheme.typography.body1)
                     }
                     Column(
                         modifier = Modifier
                             .fillMaxWidth(0.5F)
                             .height(80.dp),
                         verticalArrangement = Arrangement.SpaceEvenly
                     ) {
                         Text("Sunset", style = MaterialTheme.typography.body2.copy(color = Color.LightGray))
                         Text(DateTimeFormatter.ofPattern("HH:mm").format(LocalDateTime.ofInstant(Instant.ofEpochMilli(
                             (1000*locationData.daily.data[0].sunsetTime!!).toLong()
                         ), ZoneId.systemDefault())), style = MaterialTheme.typography.body1)
                     }
                 }
               }


           }
       }
   }
     }
     item {
         Spacer(modifier = Modifier.height(50.dp))
     }
     item {
         RainTimes(rainProbability = locationData.hourly.data,
             rainProbabilityDaily = locationData.daily.data)

     }
     item {
         WeeklyTimes(data = locationData.daily.data)
     }
     

 }
   }

}

enum class RainTimeCategory{
    HOURLY,DAILY
}

@Composable
fun WeeklyTimes(data:List<Data>) {

   

    Surface(
        contentColor = Color.White, modifier = Modifier


            .fillMaxWidth()
            .padding(vertical = 20.dp),
        color = Color.White.copy(alpha = 0.1F)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

            Text("Weekly forecast", style = MaterialTheme.typography.body2.copy(color = Color.White))
            
            Column(modifier = Modifier.padding(top = 20.dp)) {
             data.forEach { 
                 WeeklyTile(data = it)
             }   
            }
        }
    }
}

@Composable
fun WeeklyTile(data:Data){
    Box(){
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(45.dp),horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text(DateTimeFormatter.ofPattern("EEEE").format(LocalDateTime.ofInstant(Instant.ofEpochMilli(
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
             Icon(Icons.Filled.ArrowDownward,contentDescription = "",tint = blue_grey_200,modifier = Modifier.scale(0.7f))

             Text(data.temperatureLow!!.roundToInt().toString(), style = MaterialTheme.typography.caption.copy(color = blue_grey_500))
             Spacer(modifier = Modifier.width(5.dp))
             Image(
                 painter = painterResource(id = R.drawable.raining), contentDescription = "",
                 colorFilter = ColorFilter.tint(color = Color.White)
             )

             Text("${(100*data.precipProbability!!).roundToInt()}%", style = MaterialTheme.typography.caption.copy(color = Color.White))

             Icon(Icons.Filled.Air,contentDescription = "",tint = Color.White)
             Text("${data.windSpeed!!.roundToLong()} km/h", style = MaterialTheme.typography.caption.copy(color = Color.White))

         }
        }
    }
}

@Composable
fun RainTimes(rainProbability:List<DataX>,rainProbabilityDaily:List<Data>){

        var category: RainTimeCategory by remember { mutableStateOf(RainTimeCategory.HOURLY) }

        Surface(
            contentColor = Color.White, modifier = Modifier
                .height(340.dp)

                .fillMaxWidth()
                .padding(vertical = 10.dp),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {

                   Text("Rain probability", style = MaterialTheme.typography.body2.copy(color = Color.White),modifier = Modifier
                       .padding(bottom = 20.dp))
                Row(modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxWidth(0.8F),
                horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(onClick = {
                        category = RainTimeCategory.HOURLY
                    },
                        modifier = Modifier
                            .width(140.dp)
                            .height(34.dp),shape = RoundedCornerShape(30),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if(category == RainTimeCategory.HOURLY) Color.DarkGray else Color.LightGray
                        )

                    ) {

                        Text("Hourly",style = MaterialTheme.typography.button)


                    }
                    Button( onClick = {
                        category = RainTimeCategory.DAILY
                    },
                        modifier = Modifier
                            .width(140.dp)
                            .height(34.dp),
                        shape = RoundedCornerShape(30),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if(category == RainTimeCategory.DAILY) Color.DarkGray else Color.LightGray
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

    var isLoaded by remember { mutableStateOf(false)}
    val height: Dp by animateDpAsState(targetValue = if (isLoaded) target.dp else 0.dp,
        animationSpec = tween(durationMillis = 500 + (index*150), easing = FastOutSlowInEasing))

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

        Text("${(100*value).roundToInt()}%",style = MaterialTheme.typography.caption.copy(Color.DarkGray))

    }
    Text(if (weekly) DateTimeFormatter.ofPattern("EEE").format(LocalDateTime.ofInstant(Instant.ofEpochMilli(1000*time), ZoneId.systemDefault())) else DateTimeFormatter.ofPattern("HH:mm").format(LocalDateTime.ofInstant(Instant.ofEpochMilli(1000*time), ZoneId.systemDefault())), style = MaterialTheme.typography.caption.copy(color = Color.White))

}
}


//@Preview
//@Composable
//fun preview(){
//    DarkWeatherTheme() {
//        MainWeatherCard(locationName = "MyLocation", isCurrent = true )
//    }
//}