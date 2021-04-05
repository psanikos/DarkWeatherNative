package npsprojects.darkweather

import android.widget.LinearLayout
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import npsprojects.darkweather.ui.theme.*


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

@ExperimentalMaterialApi
@Composable
fun MainWeatherCard(data:WeatherViewModel,index:Int,units: WeatherUnits,updateIndex:(Int)->Unit){
    var isExpanded:Boolean by remember { mutableStateOf(false)}

val locationData = data.locations[index].data
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )
    val coroutineScope = rememberCoroutineScope()
    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {
          LazyColumn(modifier = Modifier
              .padding(10.dp)
              .fillMaxWidth(),verticalArrangement = Arrangement.spacedBy(30.dp)) {
             item {
                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
                    Box(modifier = Modifier.height(4.dp).width(50.dp).background(color = Color.Gray,
                        shape = RoundedCornerShape(40)))
                }
                 if (locationData.alerts.size > 0) {

                     Surface(
                         modifier = Modifier
                             .padding(vertical = 10.dp)
                             .fillMaxWidth()
                             .animateContentSize()
                             .clickable {
                                 isExpanded = !isExpanded
                             },
                         color = if (locationData.alerts[0].severity == "warning") red_500.copy(
                             alpha = 0.3f
                         ) else orange_500.copy(alpha = 0.3f),
                         shape = RoundedCornerShape(12.dp)
                     ) {
                         Row(
                             horizontalArrangement = Arrangement.Start,
                             verticalAlignment = Alignment.CenterVertically
                         ) {
                             Icon(
                                 Icons.Filled.Warning,
                                 contentDescription = "",
                                 modifier = Modifier
                                     .padding(10.dp)
                                     .size(40.dp),
                                 tint = if (locationData.alerts[0].severity == "warning") red_500 else orange_500
                             )
                             Column(
                                 horizontalAlignment = Alignment.Start,
                                 verticalArrangement = Arrangement.SpaceBetween,
                                 modifier = Modifier.padding(6.dp)
                             ) {
                                 Text(
                                     locationData.alerts[0].title,
                                     style = MaterialTheme.typography.body1
                                 )

                                 if (isExpanded) {
                                     Text(
                                         locationData.alerts[0].description,
                                         style = MaterialTheme.typography.caption,
                                         modifier = Modifier.padding(vertical = 5.dp),

                                         )
                                 } else {
                                     Text(
                                         locationData.alerts[0].description,
                                         style = MaterialTheme.typography.caption,
                                         modifier = Modifier.padding(vertical = 5.dp),
                                         maxLines = 3
                                     )
                                 }

                                 Text(
                                     "Until: " + DateTimeFormatter.ofPattern("EEEE dd  HH:mm")
                                         .format(
                                             LocalDateTime.ofInstant(
                                                 Instant.ofEpochMilli(1000 * locationData.alerts[0].expires.toLong()),
                                                 ZoneId.systemDefault()
                                             )
                                         ),
                                     style = MaterialTheme.typography.caption,
                                     lineHeight = 10.sp
                                 )


                             }
                         }
                     }
                 }
             }
              item {
                  Text("Hourly", style = MaterialTheme.typography.h4)
              }
              item {
                  LazyRow(
                      modifier = Modifier


                  ) {

                      locationData.hourly.data.forEach {
                          item {
                              Box(
                                  modifier = Modifier
                                      .height(120.dp)
                                      .width(90.dp)
                                      .padding(end = 10.dp)
                                      .background(
                                          brush = Brush.verticalGradient(
                                              colors = listOf(
                                                  if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.10F) else Color.Black.copy(
                                                      alpha = 0.10F
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
                                              .height(40.dp)
                                              .width(40.dp)

                                      )
                                      Text(
                                          "${it.temperature!!.roundToInt()}°",
                                          style = MaterialTheme.typography.body2.copy(
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
              item {
                  Text("Currently", style = MaterialTheme.typography.h4)
              }
              item {
                  Column(
                      modifier = Modifier
                          .fillMaxWidth()
                  ) {

                      Box(modifier = Modifier.fillMaxWidth()) {
                          Column(modifier = Modifier.fillMaxWidth()) {
                              Row(
                                  modifier = Modifier.fillMaxWidth(),
                                  horizontalArrangement = Arrangement.SpaceBetween
                              ) {
                                  Box(
                                      modifier = Modifier
                                          .height(90.dp)
                                          .width(80.dp)
                                          .background(
                                              color = blue_700.copy(alpha = 0.15f),
                                              shape = RoundedCornerShape(12.dp)
                                          )
                                  ) {
                                      Column(
                                          modifier = Modifier
                                              .padding(4.dp)
                                              .height(90.dp)
                                              .width(80.dp),
                                          verticalArrangement = Arrangement.SpaceEvenly
                                      ) {
                                          Text(
                                              "Wind",
                                              style = MaterialTheme.typography.button.copy(color = blue_700)
                                          )
                                          Text(
                                              "${locationData.currently.windSpeed!!.roundToLong()} " + if (units == WeatherUnits.US) "mph" else "km/h",
                                              style = MaterialTheme.typography.h4
                                          )
                                      }
                                  }
                                  Box(
                                      modifier = Modifier
                                          .height(90.dp)
                                          .width(80.dp)
                                          .background(
                                              color = blue_700.copy(alpha = 0.15f),
                                              shape = RoundedCornerShape(12.dp)
                                          )
                                  ) {
                                      Column(
                                          modifier = Modifier
                                              .padding(4.dp)
                                              .height(90.dp)
                                              .width(80.dp),
                                          verticalArrangement = Arrangement.SpaceEvenly
                                      ) {
                                          Text(
                                              "Humidity",
                                              style = MaterialTheme.typography.button.copy(color = blue_700)
                                          )
                                          Text(
                                              "${(100 * locationData.currently.humidity!!).roundToInt()}%",
                                              style = MaterialTheme.typography.h4
                                          )
                                      }
                                  }
                                  Box(
                                      modifier = Modifier
                                          .height(90.dp)
                                          .width(80.dp)
                                          .background(
                                              color = blue_700.copy(alpha = 0.15f),
                                              shape = RoundedCornerShape(12.dp)
                                          )
                                  ) {
                                      Column(
                                          modifier = Modifier
                                              .padding(4.dp)
                                              .height(90.dp)
                                              .width(80.dp),
                                          verticalArrangement = Arrangement.SpaceEvenly
                                      ) {
                                          Text(
                                              "UV Index",
                                              style = MaterialTheme.typography.button.copy(color = blue_700)
                                          )
                                          Text(
                                              "${locationData.currently.uvIndex}",
                                              style = MaterialTheme.typography.h4
                                          )
                                      }
                                  }

                                  Box(
                                      modifier = Modifier
                                          .height(90.dp)
                                          .width(80.dp)
                                          .background(
                                              color = blue_700.copy(alpha = 0.15f),
                                              shape = RoundedCornerShape(12.dp)
                                          )
                                  ) {
                                      Column(
                                          modifier = Modifier
                                              .padding(4.dp)
                                              .height(90.dp)
                                              .width(80.dp),
                                          verticalArrangement = Arrangement.SpaceEvenly
                                      ) {
                                          Text(
                                              "Feels like",
                                              style = MaterialTheme.typography.button.copy(color = blue_700)
                                          )
                                          Text(
                                              "${locationData.currently.apparentTemperature!!.roundToInt()}°",
                                              style = MaterialTheme.typography.h4
                                          )
                                      }
                                  }
                              }

//                   Row(
//                       modifier = Modifier.fillMaxWidth(),
//                       horizontalArrangement = Arrangement.SpaceEvenly
//                   ) {
//
//
//
//
//                       Box(
//                           modifier = Modifier.height(110.dp).width(90.dp).background(
//                               color = blue_700.copy(alpha = 0.15f),
//                               shape = RoundedCornerShape(12.dp)
//                           )
//                       ) {
//                           Column(
//                               modifier = Modifier.height(110.dp).width(90.dp),
//                               verticalArrangement = Arrangement.SpaceEvenly
//                           ) {
//                               Text(
//                                   "Sunrise",
//                                   style = MaterialTheme.typography.button.copy(color = grey_600)
//                               )
//                               Text(
//                                   DateTimeFormatter.ofPattern("HH:mm").format(
//                                       LocalDateTime.ofInstant(
//                                           Instant.ofEpochMilli(
//                                               (1000 * locationData.daily.data[0].sunriseTime!!).toLong()
//                                           ), ZoneId.systemDefault()
//                                       )
//                                   ), style = MaterialTheme.typography.body2
//                               )
//                           }
//                       }
//                       Box(
//                           modifier = Modifier.height(110.dp).width(90.dp).background(
//                               color = blue_700.copy(alpha = 0.15f),
//                               shape = RoundedCornerShape(12.dp)
//                           )
//                       ) {
//                           Column(
//                               modifier = Modifier.height(110.dp).width(90.dp),
//                               verticalArrangement = Arrangement.SpaceEvenly
//                           ) {
//                               Text(
//                                   "Sunset",
//                                   style = MaterialTheme.typography.button.copy(color = grey_600)
//                               )
//                               Text(
//                                   DateTimeFormatter.ofPattern("HH:mm").format(
//                                       LocalDateTime.ofInstant(
//                                           Instant.ofEpochMilli(
//                                               (1000 * locationData.daily.data[0].sunsetTime!!).toLong()
//                                           ), ZoneId.systemDefault()
//                                       )
//                                   ), style = MaterialTheme.typography.body2
//                               )
//                           }
//                       }
//                   }
                          }

                      }


                  }

              }
              item {
                  BannerAdView()
              }
              item {
                  Text("Rain probability", style = MaterialTheme.typography.h4)
              }
              item {
                  RainTimes(
                      rainProbability = locationData.hourly.data,
                      rainProbabilityDaily = locationData.daily.data
                  )
              }
              item {
                  BannerAdView()
              }
              item {
                  Text("Weekly forecast", style = MaterialTheme.typography.h4)
              }
              item {
                  WeeklyTimes(data = locationData.daily.data, units = units)
              }
              item {
                  Spacer(modifier = Modifier.height(40.dp))
              }
          }
        }, sheetPeekHeight = 350.dp,
       backgroundColor = getWeatherColor(locationData.currently.icon!!),
        sheetGesturesEnabled = true,
        sheetBackgroundColor = if(isSystemInDarkTheme()) Color(0xFF181818) else Color.White

   ) {
       Column(modifier = Modifier.fillMaxSize(),horizontalAlignment = Alignment.CenterHorizontally
       ) {
           Column(
               modifier = Modifier
                   .fillMaxWidth()
                   .fillMaxHeight(0.55f), horizontalAlignment = Alignment.CenterHorizontally,
               verticalArrangement = Arrangement.SpaceBetween
           ) {
               LazyRow(contentPadding = PaddingValues(start = 10.dp,end = 10.dp),verticalAlignment = Alignment.CenterVertically,
               modifier = Modifier.padding(top = 50.dp,start = 16.dp,end = 16.dp)) {
                 itemsIndexed(data.locations){
                     i,item ->
                  Box(modifier = Modifier
                      .background(
                          color = if (index == i) Color.White else Color.Transparent,
                          shape = RoundedCornerShape(50)
                      )
                      .clickable {
                          updateIndex(i)
                      },
                  contentAlignment = Alignment.Center) {
                      Text(item.name,
                          style = MaterialTheme.typography.button.copy(
                              color = if(index == i) Color.Black else Color.White
                          ),modifier = Modifier.padding(horizontal = 10.dp,vertical = 5.dp))
                  }
                 }
               }
               Column(
                   modifier = Modifier.fillMaxWidth(),
                   horizontalAlignment = Alignment.CenterHorizontally,

                   ) {


                   Row(verticalAlignment = Alignment.CenterVertically) {
                       Image(
                           painter = painterResource(id = getWeatherIcon(locationData.currently.icon)),
                           contentDescription = "",
                           contentScale = ContentScale.Fit,
                           modifier = Modifier
                               .height(60.dp)
                               .width(60.dp)
                       )
                       Text(
                           text = "${locationData.currently.temperature!!.roundToInt()}°",
                           style = MaterialTheme.typography.h1.copy(
                               shadow = Shadow(
                                   color = Color.Black,
                                   offset = Offset(0.5f, 0.5f)
                               )
                           ),
                           modifier = Modifier
                               .padding(horizontal = 10.dp, vertical = 10.dp)
                       )
                   }
                   Text(
                       text = locationData.currently.summary!!,
                       style = MaterialTheme.typography.body1.copy(
                           shadow = Shadow(
                               color = Color.Black,
                               offset = Offset(0.5f, 0.5f)
                           )
                       ),
                       modifier = Modifier
                           .padding(horizontal = 20.dp, vertical = 10.dp),
                       textAlign = TextAlign.Center
                   )
         Row(
             modifier = Modifier
                 .padding(horizontal = 20.dp, vertical = 30.dp)
                 .fillMaxWidth()
               ,
             horizontalArrangement = Arrangement.SpaceEvenly,

             ) {
             Row() {
                 Icon(Icons.Filled.ArrowUpward, contentDescription = "")
                 Spacer(modifier = Modifier.width(3.dp))
                 Text(text = "${locationData.daily.data[0].temperatureHigh!!.roundToInt()}°", style = MaterialTheme.typography.body2.copy(shadow = Shadow(color = Color.Black,offset = Offset(0.5f,0.5f))))
             }
             Row() {
                 Icon(Icons.Filled.ArrowDownward, contentDescription = "")
                 Spacer(modifier = Modifier.width(3.dp))
                 Text(text = "${locationData.daily.data[0].temperatureLow!!.roundToInt()}°", style = MaterialTheme.typography.body2.copy(shadow = Shadow(color = Color.Black,offset = Offset(0.5f,0.5f))))
             }
             Row() {
                 Image(
                     painter = painterResource(id = R.drawable.raining), contentDescription = "",
                     colorFilter = ColorFilter.tint(color = Color.White)
                 )
                 Spacer(modifier = Modifier.width(3.dp))
                 Text(text = "${(100*locationData.daily.data[0].precipProbability!!).roundToInt()}%", style = MaterialTheme.typography.body2.copy(shadow = Shadow(color = Color.Black,offset = Offset(0.5f,0.5f))))
             }
             Row() {
                 Icon(Icons.Filled.Air, contentDescription = "")
                 Spacer(modifier = Modifier.width(3.dp))
                 Text(text = "${locationData.currently.windSpeed!!.roundToInt()} " + if(units == WeatherUnits.US) "mph" else "km/h", style = MaterialTheme.typography.body2.copy(shadow = Shadow(color = Color.Black,offset = Offset(0.5f,0.5f))))
             }
         }
                   BannerAdView()

               }
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