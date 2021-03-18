package com.npsappprojects.darkweather

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import kotlin.math.roundToLong

//locationData:WeatherResponse
@Composable
fun MainWeatherCard(locationData:WeatherResponse,locationName:String,isCurrent:Boolean){
   Surface(modifier = Modifier.fillMaxSize(),color = getWeatherColor("clear-night"),
       contentColor = Color.White) {

 LazyColumn(modifier = Modifier.fillMaxSize(),horizontalAlignment = Alignment.CenterHorizontally) {
   item {
       Row(
           modifier = Modifier
               .padding(horizontal = 20.dp, vertical = 40.dp)
               .fillMaxWidth(), horizontalArrangement = Arrangement.Center,
           verticalAlignment = Alignment.CenterVertically
       ) {
           Icon(Icons.Filled.LocationOn, contentDescription = "", modifier = Modifier.size(34.dp))
           Text(text = locationName, style = MaterialTheme.typography.h1)
       }
   }
     item {
         Image(
             painter = painterResource(id = getWeatherIcon(locationData.currently.icon!!)), contentDescription = "",
             modifier = Modifier
                 .height(200.dp)
                 .width(200.dp)
                 .padding(vertical = 20.dp)
         )
     }
     item {
         Text(
             text = "${locationData.currently.temperature!!.roundToInt()}°",
             style = MaterialTheme.typography.h1.copy(fontSize = 50.sp),
             modifier = Modifier
                 .padding(horizontal = 20.dp, vertical = 10.dp)
         )
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
         Row(
             modifier = Modifier
                 .fillMaxWidth()
                 .padding(vertical = 10.dp),
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
         }
     }
     item {
         LazyRow(modifier = Modifier.padding(16.dp)) {
             items(locationData.hourly.data.count()) {
                 locationData.hourly.data.forEach {
                     Box(
                         modifier = Modifier
                             .height(100.dp)
                             .width(60.dp)
                     ) {
                         Column(
                             modifier = Modifier.fillMaxSize(),
                             verticalArrangement = Arrangement.SpaceEvenly,
                             horizontalAlignment = Alignment.CenterHorizontally
                         ) {
                             Text("09:00", style = MaterialTheme.typography.caption)
                             Image(
                                 painter = painterResource(id = getWeatherIcon(it.icon!!)),
                                 contentDescription = "",
                                 modifier = Modifier
                                     .height(45.dp)
                                     .width(45.dp)
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
         Text(
             "Today ${locationData.daily.data[0].temperatureMax!!.roundToInt()}° max temperature at 02:33 and ${locationData.daily.data[0].temperatureMin!!.roundToInt()}° min temperature at 06:00",
             style = MaterialTheme.typography.body2,
             textAlign = TextAlign.Center,
             modifier = Modifier.padding(horizontal = 16.dp,vertical = 20.dp)
         )
     }
     item {
         Box(modifier = Modifier.fillMaxWidth()) {
             Column(modifier = Modifier.fillMaxWidth()) {
                 Row(
                     modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                         .fillMaxWidth(),
                     horizontalArrangement = Arrangement.SpaceBetween
                 ) {
                     Text("Wind", style = MaterialTheme.typography.body2)
                     Text("${locationData.currently.windSpeed!!.roundToLong()}km/h", style = MaterialTheme.typography.body2)
                 }
                 Row(
                     modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                         .fillMaxWidth(),
                     horizontalArrangement = Arrangement.SpaceBetween
                 ) {
                     Text("Humidity", style = MaterialTheme.typography.body2)
                     Text("${(100*locationData.currently.humidity!!).roundToInt()}%", style = MaterialTheme.typography.body2)
                 }
                 Row(
                     modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                         .fillMaxWidth(),
                     horizontalArrangement = Arrangement.SpaceBetween
                 ) {
                     Text("UV Index", style = MaterialTheme.typography.body2)
                     Text("${locationData.currently.uvIndex}", style = MaterialTheme.typography.body2)
                 }
                 Row(
                     modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                         .fillMaxWidth(),
                     horizontalArrangement = Arrangement.SpaceBetween
                 ) {
                     Text("Feels like", style = MaterialTheme.typography.body2)
                     Text("${locationData.currently.apparentTemperature!!.roundToInt()}°", style = MaterialTheme.typography.body2)
                 }
             }


         }
     }
    item {
        Spacer(modifier = Modifier.height(50.dp))
    }
 }
   }

}




//@Preview
//@Composable
//fun preview(){
//    DarkWeatherTheme() {
//        MainWeatherCard(locationName = "MyLocation", isCurrent = true )
//    }
//}