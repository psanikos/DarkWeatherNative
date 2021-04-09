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
    var isExpanded:Boolean by remember { mutableStateOf(false) }

    Scaffold(

       backgroundColor = getWeatherColor(locationData.currently.icon!!),



   ) {

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                   , horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = 50.dp)
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        data.getCurrentLocationWeather()
                    }) {
                        Box(
                            modifier = Modifier
                                .height(30.dp)
                                .width(30.dp)
                                .background(
                                    color = Color(0xFF202020),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Rounded.Repeat,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp),
                                contentDescription = ""
                            )
                        }
                    }

                    LazyRow(
                        contentPadding = PaddingValues(start = 10.dp, end = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,

                        modifier = Modifier.fillMaxWidth(0.9f)
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
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(30.dp)

                    ) {


item {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {


        Column(horizontalAlignment = Alignment.Start) {


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
            Text(
                text = "Feels like ${locationData.currently.apparentTemperature!!.toInt()}°",
                style = MaterialTheme.typography.body2.copy(
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(0.5f, 0.5f)
                    ),
                    color = pink_100
                ),
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                textAlign = TextAlign.Center
            )
        }

        Image(
            painter = painterResource(id = getWeatherIcon(locationData.currently.icon)),
            contentDescription = "",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .height(100.dp)
                .width(100.dp)
        )
    }
}
                    item {
                        Row(
                            modifier = Modifier
                                .padding(vertical = 20.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,

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
                    item {
                        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
                            Box(modifier = Modifier
                                .height(4.dp)
                                .width(50.dp)
                                .background(
                                    color = Color.Gray,
                                    shape = RoundedCornerShape(40)

                                )
                            )

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
                                            style = MaterialTheme.typography.h4.copy(fontSize = 14.sp)
                                        )

                                        if (isExpanded) {
                                            Text(
                                                locationData.alerts[0].description.toLowerCase(Locale.current),
                                                style = MaterialTheme.typography.button.copy(fontSize = 11.sp),
                                                modifier = Modifier.padding(vertical = 5.dp),

                                                )
                                        } else {
                                            Text(
                                                locationData.alerts[0].description.toLowerCase(Locale.current),
                                                style = MaterialTheme.typography.button.copy(fontSize = 11.sp),
                                                modifier = Modifier.padding(vertical = 5.dp),
                                                maxLines = 3
                                            )
                                        }

                                        Text(
                                            "Until: " +
                                                    SimpleDateFormat("EEEE dd  HH:mm").format(1000 * locationData.alerts[0].expires.toLong())

                                            ,
                                            style = MaterialTheme.typography.h4.copy(fontSize = 12.sp),
                                            lineHeight = 10.sp
                                        )


                                    }
                                }
                            }
                        }
                    }
                    item {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth()


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
                                                        if (isSystemInDarkTheme()) Color.White.copy(
                                                            alpha = 0.10F
                                                        ) else Color.Black.copy(
                                                            alpha = 0.10F
                                                        ),
                                                        if (isSystemInDarkTheme()) Color.White.copy(
                                                            alpha = 0.15F
                                                        ) else Color.Black.copy(
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
                                                style = MaterialTheme.typography.caption.copy(color = Color.White)
                                            )
                                            Image(
                                                painter = painterResource(id = getWeatherIcon(it.icon!!)),
                                                contentDescription = "",
                                                modifier = Modifier
                                                    .height(40.dp)
                                                    .width(40.dp)

                                            )
                                            Text(
                                                "${it.temperature!!.toInt()}°",
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
                    item{
                        BannerAdView()
                    }

                    item {
                        MyChartView(rainProbability = locationData.hourly.data)


                    }
                    item {

                        Box(modifier = Modifier.fillMaxWidth()) {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {



                                    Box(
                                        modifier = Modifier
                                            .height(100.dp)
                                            .width(80.dp)
                                            .background(
                                                color = blue_700.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .padding(4.dp)
                                                .height(100.dp)
                                                .width(80.dp),
                                            verticalArrangement = Arrangement.SpaceEvenly
                                            ,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Image(painter = painterResource(id = R.drawable.humidity),contentDescription = "",
                                                modifier = Modifier.size(25.dp))
                                            Text(
                                                "${(100 * locationData.currently.humidity!!).toInt()}%",
                                                style = MaterialTheme.typography.h4
                                            )
                                        }
                                    }


                                    Box(
                                        modifier = Modifier
                                            .height(100.dp)
                                            .width(80.dp)
                                            .background(
                                                color = when (locationData.currently.uvIndex) {
                                                    0, 1, 2 -> Color.Green.copy(alpha = 0.3f)
                                                    3, 4, 5 -> Color.Yellow.copy(alpha = 0.3f)
                                                    6, 7 -> orange_500.copy(alpha = 0.3f)
                                                    else -> Color.Red.copy(alpha = 0.3f)
                                                },
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .padding(4.dp)
                                                .height(100.dp)
                                                .width(80.dp),
                                            verticalArrangement = Arrangement.SpaceEvenly
                                            ,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Image(painter = painterResource(id = R.drawable.uv),contentDescription = "",
                                                modifier = Modifier.size(25.dp))
                                            Text(
                                                "${locationData.currently.uvIndex}",
                                                style = MaterialTheme.typography.h4
                                            )
                                        }
                                    }



                                    Box(
                                        modifier = Modifier
                                            .height(100.dp)
                                            .width(80.dp)
                                            .background(
                                                color = blue_700.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .padding(4.dp)
                                                .height(100.dp)
                                                .width(80.dp),
                                            verticalArrangement = Arrangement.SpaceEvenly
                                            ,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Image(painter = painterResource(id = R.drawable.sunrise),contentDescription = "",
                                                modifier = Modifier.size(25.dp))
                                            Text(

                                                SimpleDateFormat("HH:mm").format(1000*locationData.daily.data[0].sunriseTime!!.toLong())
                                                ,
                                                style = MaterialTheme.typography.h4
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .height(100.dp)
                                            .width(80.dp)
                                            .background(
                                                color = blue_700.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .padding(4.dp)
                                                .height(100.dp)
                                                .width(80.dp),
                                            verticalArrangement = Arrangement.SpaceEvenly
                                            ,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Image(painter = painterResource(id = R.drawable.sunset),contentDescription = "",
                                                modifier = Modifier.size(25.dp))
                                            Text(
                                                SimpleDateFormat("HH:mm").format(1000*locationData.daily.data[0].sunsetTime!!.toLong()),
                                                style = MaterialTheme.typography.h4
                                            )
                                        }
                                    }
                                }




                        }




                    }
                    item {
                        WeeklyTimes(data = locationData.daily.data, units = units)
                    }
                    item {
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }


//     BottomDrawer(drawerContent = {
//
//BottomDrawer(data = data, index = index, units = units)
//     },
//         drawerBackgroundColor = if(isSystemInDarkTheme()) Color(0xFF252525) else Color.White
//         ,drawerShape = RoundedCornerShape(0),
//     drawerState = bottomDrawerState,scrimColor = Color.Transparent,drawerElevation = 0.dp) {
//
//
//
//     }

   }

}






//@Preview
//@Composable
//fun preview(){
//    DarkWeatherTheme() {
//        MainWeatherCard(locationName = "MyLocation", isCurrent = true )
//    }
//}