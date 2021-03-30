package npsprojects.darkweather

import android.widget.LinearLayout
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import npsprojects.darkweather.ui.theme.grey_600
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp
import npsprojects.darkweather.ui.theme.pink_400


@Composable
fun BannerAdView() {
val bannerId = "ca-app-pub-9340838273925003/1697078171"
    val testId = "ca-app-pub-3940256099942544/6300978111"
    AndroidView(factory = { ctx ->

        LinearLayout(ctx).apply {
            val adView = AdView(ctx)
            adView.adSize = AdSize.BANNER
            adView.adUnitId = bannerId
            this.orientation = LinearLayout.VERTICAL
            this.addView(adView)
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)

        }
    }, modifier = Modifier
        .fillMaxWidth()
        .height(50.dp))
}

@Composable
fun MainWeatherCard(locationData: WeatherResponse,units: WeatherUnits){
    var isExpanded:Boolean by remember { mutableStateOf(false)}


   Box(modifier = Modifier
       .background(
           brush = Brush.verticalGradient(
               colors = listOf(
                   getWeatherColor(locationData.currently.icon!!),
                   if (isSystemInDarkTheme()) Color(0xFF121212) else Color.White
               )
           )
       )
       .fillMaxSize()) {

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
             text = locationData.currently.summary!!,
             style = MaterialTheme.typography.body1.copy(shadow = Shadow(color = Color.Black,offset = Offset(0.5f,0.5f))),
             modifier = Modifier
                 .padding(horizontal = 20.dp, vertical = 10.dp),
             textAlign = TextAlign.Center
         )
     }
     item {
         Text(
             text = "${locationData.currently.temperature!!.roundToInt()}°",
             style = MaterialTheme.typography.h1.copy(shadow = Shadow(color = Color.Black,offset = Offset(0.5f,0.5f))),
             modifier = Modifier
                 .padding(horizontal = 20.dp, vertical = 10.dp)
         )
     }
     if(locationData.alerts.size>0) {
         item {
Box(modifier = Modifier

    .fillMaxWidth()
    .background(pink_400).animateContentSize().clickable {
     isExpanded = !isExpanded
    },
contentAlignment = Alignment.Center){
    Column(horizontalAlignment = Alignment.CenterHorizontally,verticalArrangement = Arrangement.SpaceEvenly,modifier = Modifier.padding(6.dp)) {
      Text(locationData.alerts[0].title,style = MaterialTheme.typography.body1.copy(color = Color.White))
      if(isExpanded) {
          Text(
              locationData.alerts[0].description,
              style = MaterialTheme.typography.caption.copy(color = Color.White),
              modifier = Modifier.padding(vertical = 5.dp)
          )
      }
        Text("Until: " +   DateTimeFormatter.ofPattern("EEEE dd  HH:mm").format(
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(1000 * locationData.alerts[0].expires.toLong()),
                ZoneId.systemDefault()
            )
        ) ,style = MaterialTheme.typography.body2.copy(color = Color.White),lineHeight = 14.sp)

    }
}
         }
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
                 Text(text = "${locationData.daily.data[0].temperatureHigh!!.roundToInt()}°", style = MaterialTheme.typography.body1.copy(shadow = Shadow(color = Color.Black,offset = Offset(0.5f,0.5f))))
             }
             Row() {
                 Icon(Icons.Filled.ArrowDownward, contentDescription = "")
                 Spacer(modifier = Modifier.width(5.dp))
                 Text(text = "${locationData.daily.data[0].temperatureLow!!.roundToInt()}°", style = MaterialTheme.typography.body1.copy(shadow = Shadow(color = Color.Black,offset = Offset(0.5f,0.5f))))
             }
             Row() {
                 Image(
                     painter = painterResource(id = R.drawable.raining), contentDescription = "",
                     colorFilter = ColorFilter.tint(color = Color.White)
                 )
                 Spacer(modifier = Modifier.width(5.dp))
                 Text(text = "${(100*locationData.daily.data[0].precipProbability!!).roundToInt()}%", style = MaterialTheme.typography.body1.copy(shadow = Shadow(color = Color.Black,offset = Offset(0.5f,0.5f))))
             }
             Row() {
                 Icon(Icons.Filled.Air, contentDescription = "")
                 Spacer(modifier = Modifier.width(5.dp))
                 Text(text = "${locationData.currently.windSpeed!!.roundToInt()} " + if(units == WeatherUnits.US) "mph" else "km/h", style = MaterialTheme.typography.body1.copy(shadow = Shadow(color = Color.Black,offset = Offset(0.5f,0.5f))))
             }
         }
     }
     item {
         BannerAdView()
     }
     item {
         LazyRow(modifier = Modifier
             .padding(16.dp)
             .padding(vertical = 20.dp)) {

                 locationData.hourly.data.forEach {
                     item {
                     Box(
                         modifier = Modifier
                             .height(140.dp)
                             .width(100.dp)
                             .padding(end = 10.dp)
                             .background(
                                 brush = Brush.verticalGradient(
                                     colors = listOf(
                                         Color.White.copy(
                                             alpha = 0.4f
                                         ),
                                         if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.15F) else Color.Black.copy(
                                             alpha = 0.15F
                                         )

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

                             Text(
                                 DateTimeFormatter.ofPattern("HH:mm").format(
                                     LocalDateTime.ofInstant(
                                         Instant.ofEpochMilli(1000 * it.time!!.toLong()),
                                         ZoneId.systemDefault()
                                     )
                                 ),
                                 style = MaterialTheme.typography.caption.copy(color = MaterialTheme.colors.primary)
                             )
                             Image(
                                 painter = painterResource(id = getWeatherIcon(it.icon!!)),
                                 contentDescription = "",
                                 modifier = Modifier
                                     .height(50.dp)
                                     .width(50.dp)
                                     .padding(vertical = 5.dp)
                             )
                             Text(
                                 "${it.temperature!!.roundToInt()}°",
                                 style = MaterialTheme.typography.body2.copy(shadow = Shadow(color = Color.Black))
                             )
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
       color = if (isSystemInDarkTheme()) Color(0xFF202020).copy(alpha = 0.5F) else Color.White.copy(alpha = 0.5F),
       contentColor = MaterialTheme.colors.primary
   ) {
       Column(modifier = Modifier
           .fillMaxWidth()
           .padding(vertical = 10.dp, horizontal = 16.dp)) {
           Text(
               "Today ${locationData.daily.data[0].temperatureMax!!.roundToInt()}° max temperature at ${DateTimeFormatter.ofPattern("HH:mm").format(LocalDateTime.ofInstant(Instant.ofEpochMilli(1000*locationData.daily.data[0].apparentTemperatureMaxTime!!.toLong()), ZoneId.systemDefault()))} and ${locationData.daily.data[0].temperatureMin!!.roundToInt()}° min temperature at ${DateTimeFormatter.ofPattern("HH:mm").format(LocalDateTime.ofInstant(Instant.ofEpochMilli(1000*locationData.daily.data[0].apparentTemperatureMinTime!!.toLong()), ZoneId.systemDefault()))}.",
               style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.primary),

               textAlign = TextAlign.Center,
               modifier = Modifier.padding(horizontal = 16.dp,vertical = 20.dp)
           )

           Box(modifier = Modifier.fillMaxWidth()) {
               Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceEvenly) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.5F)
                        .height(80.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text("Wind", style = MaterialTheme.typography.body2.copy(color = grey_600))
                    Text("${locationData.currently.windSpeed!!.roundToLong()} " + if(units == WeatherUnits.US) "mph" else "km/h" , style = MaterialTheme.typography.body1)
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.5F)
                        .height(80.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text("Humidity", style = MaterialTheme.typography.body2.copy(color = grey_600))
                    Text("${(100*locationData.currently.humidity!!).roundToInt()}%", style = MaterialTheme.typography.body1)
                }
            }
            Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceEvenly) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.5F)
                        .height(80.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text("UV Index", style = MaterialTheme.typography.body2.copy(color = grey_600))
                    Text("${locationData.currently.uvIndex}", style = MaterialTheme.typography.body1)
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.5F)
                        .height(80.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text("Feels like", style = MaterialTheme.typography.body2.copy(color = grey_600))
                    Text("${locationData.currently.apparentTemperature!!.roundToInt()}°", style = MaterialTheme.typography.body1)
                }
            }
                 Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceEvenly){
                     Column(
                         modifier = Modifier
                             .fillMaxWidth(0.5F)
                             .height(80.dp),
                         verticalArrangement = Arrangement.SpaceEvenly
                     ) {
                         Text("Sunrise", style = MaterialTheme.typography.body2.copy(color = grey_600))
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
                         Text("Sunset", style = MaterialTheme.typography.body2.copy(color = grey_600))
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
         WeeklyTimes(data = locationData.daily.data,units = units)
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