package npsprojects.darkweather

import android.widget.LinearLayout
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import npsprojects.darkweather.ui.theme.*
import java.text.SimpleDateFormat




@ExperimentalMaterialApi
@Composable
fun MainWeatherCard(data:WeatherViewModel,index:Int,units: WeatherUnits,updateIndex:(Int)->Unit,controller: NavController){

val locationData = data.locations[index].data
    val bottomDrawerState = rememberBottomDrawerState(initialValue = BottomDrawerValue.Open,confirmStateChange = {
      when(it){
          BottomDrawerValue.Closed -> false
          BottomDrawerValue.Open -> true
          BottomDrawerValue.Expanded -> true
      }
    })

    Scaffold(

       backgroundColor = getWeatherColor(locationData.currently.icon!!),



   ) {
     BottomDrawer(drawerContent = {

BottomDrawer(data = data, index = index, units = units)
     },
         drawerBackgroundColor = if(isSystemInDarkTheme()) Color(0xFF252525) else Color.White
         ,drawerShape = RoundedCornerShape(0),
     drawerState = bottomDrawerState,scrimColor = Color.Transparent,drawerElevation = 0.dp) {


         Column(
             modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
         ) {
             Column(
                 modifier = Modifier
                     .fillMaxWidth()
                     .fillMaxHeight(0.48f), horizontalAlignment = Alignment.CenterHorizontally,
                 verticalArrangement = Arrangement.SpaceBetween
             ) {
                 Row(
                     modifier = Modifier
                         .padding(top = 50.dp, start = 16.dp, end = 16.dp)
                         .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                     verticalAlignment = Alignment.CenterVertically
                 ) {
                     IconButton(onClick = {
                         controller.navigate("Settings")
                     }) {
                         Icon(
                             Icons.Rounded.Sort,
                             tint = Color.White,
                             modifier = Modifier.size(18.dp),
                             contentDescription = ""
                         )
                     }
                     LazyRow(
                         contentPadding = PaddingValues(start = 10.dp, end = 10.dp),
                         verticalAlignment = Alignment.CenterVertically,
                         modifier = Modifier.fillMaxWidth(0.8f)
                     ) {
                         itemsIndexed(data.locations) { i, item ->
                             Box(
                                 modifier = Modifier
                                     .background(
                                         color = if (index == i) Color.White else Color.Transparent,
                                         shape = RoundedCornerShape(50)
                                     )
                                     .clickable {
                                         updateIndex(i)
                                     },
                                 contentAlignment = Alignment.Center
                             ) {
                                 Row() {
                                     if (item.isCurrent) {
                                         Icon(
                                             Icons.Rounded.LocationOn,
                                             tint = if (index == i) Color.Black else Color.White,
                                             contentDescription = "",
                                             modifier = Modifier.size(25.dp)
                                         )
                                     }
                                     Text(
                                         item.name,
                                         style = MaterialTheme.typography.button.copy(
                                             color = if (index == i) Color.Black else Color.White
                                         ),
                                         modifier = Modifier.padding(
                                             horizontal = 10.dp,
                                             vertical = 5.dp
                                         )
                                     )
                                 }
                             }
                         }
                     }
                     IconButton(onClick = {
                         controller.navigate("Add")
                     }) {
                         Icon(
                             Icons.Rounded.LocationCity,
                             tint = Color.White,
                             modifier = Modifier.size(18.dp),
                             contentDescription = ""
                         )
                     }
                 }
                 Column(
                     modifier = Modifier.fillMaxWidth(),
                     horizontalAlignment = Alignment.CenterHorizontally,

                     ) {

                                if(index == 0 && data.currentLocationData != null){
                                    IconButton(onClick = {
                                        data.getCurrentLocationWeather()
                                    }) {
                                        Box(modifier = Modifier
                                            .height(40.dp)
                                            .width(40.dp)
                                            .background(
                                                color = Color(0xFF202020),
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center) {
                                            Icon(
                                                Icons.Rounded.Repeat,
                                                tint = Color.White,
                                                modifier = Modifier.size(25.dp),
                                                contentDescription = ""
                                            )
                                        }
                                    }
                                }
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
                             .fillMaxWidth(),
                         horizontalArrangement = Arrangement.SpaceEvenly,

                         ) {
                         Row() {
                             Icon(Icons.Filled.ArrowUpward, contentDescription = "")
                             Spacer(modifier = Modifier.width(3.dp))
                             Text(
                                 text = "${locationData.daily.data[0].temperatureHigh!!.roundToInt()}°",
                                 style = MaterialTheme.typography.body2.copy(
                                     shadow = Shadow(
                                         color = Color.Black,
                                         offset = Offset(0.5f, 0.5f)
                                     )
                                 )
                             )
                         }
                         Row() {
                             Icon(Icons.Filled.ArrowDownward, contentDescription = "")
                             Spacer(modifier = Modifier.width(3.dp))
                             Text(
                                 text = "${locationData.daily.data[0].temperatureLow!!.roundToInt()}°",
                                 style = MaterialTheme.typography.body2.copy(
                                     shadow = Shadow(
                                         color = Color.Black,
                                         offset = Offset(0.5f, 0.5f)
                                     )
                                 )
                             )
                         }
                         Row() {
                             Image(
                                 painter = painterResource(id = R.drawable.raining),
                                 contentDescription = "",
                                 colorFilter = ColorFilter.tint(color = Color.White)
                             )
                             Spacer(modifier = Modifier.width(3.dp))
                             Text(
                                 text = "${(100 * locationData.daily.data[0].precipProbability!!).roundToInt()}%",
                                 style = MaterialTheme.typography.body2.copy(
                                     shadow = Shadow(
                                         color = Color.Black,
                                         offset = Offset(0.5f, 0.5f)
                                     )
                                 )
                             )
                         }
                         Row() {
                             Icon(Icons.Filled.Air, contentDescription = "")
                             Spacer(modifier = Modifier.width(3.dp))
                             Text(
                                 text = "${locationData.currently.windSpeed!!.roundToInt()} " + if (units == WeatherUnits.US) "mph" else "km/h",
                                 style = MaterialTheme.typography.body2.copy(
                                     shadow = Shadow(
                                         color = Color.Black,
                                         offset = Offset(0.5f, 0.5f)
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

}






//@Preview
//@Composable
//fun preview(){
//    DarkWeatherTheme() {
//        MainWeatherCard(locationName = "MyLocation", isCurrent = true )
//    }
//}