package npsprojects.darkweather.views

import android.graphics.drawable.Icon
import android.graphics.drawable.ShapeDrawable
import androidx.annotation.StringRes
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.runtime.internal.enableLiveLiterals
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.room.RoomSQLiteQuery
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileOverlay
import npsprojects.darkweather.*
import npsprojects.darkweather.R
import npsprojects.darkweather.models.WeatherModel
import npsprojects.darkweather.models.Weather
import npsprojects.darkweather.models.WeatherViewModel
import npsprojects.darkweather.ui.theme.*
import java.lang.reflect.Array.get
import java.lang.reflect.Array.set
import java.time.Instant
import java.util.*

//weatherModel: WeatherModel


enum class TabCategory{
    NOW,TIMELINE,DAILY
}


@Composable
fun WeatherTabView(model: WeatherViewModel,onTabChange:(TabCategory)->Unit){
  var category by remember {
      mutableStateOf(TabCategory.NOW)
  }
   fun isSelected(cat:TabCategory):Boolean{
       return cat == category
   }

BoxWithConstraints(modifier = Modifier
        .background(color = Color.White)
        .padding(20.dp)
        .height(80.dp)
        .fillMaxWidth(),contentAlignment = Alignment.TopStart) {
    val width = this.maxWidth
    val offset = animateDpAsState(
        targetValue = when (category) {
            TabCategory.NOW -> 0.dp
            TabCategory.TIMELINE -> (width/3)
            TabCategory.DAILY -> (0.66*width.value).dp
        },animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )
        Column() {


            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(color = Color.LightGray.copy(alpha = 0.4f))

                )
                Box(
                    modifier = Modifier
                        .offset(x = offset.value)
                        .fillMaxWidth(0.33f)
                        .height(4.dp)
                        .background(color = Color.DarkGray)

                )
            }



            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp), horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .height(50.dp)
                        .width(70.dp)
                        .clickable {
                            category = TabCategory.NOW
                        },
                    contentAlignment = Alignment.CenterStart
                    ) {

                    Text(
                        "Now",
                        style = MaterialTheme.typography.body1.copy(
                            color = if (isSelected(TabCategory.NOW)) MaterialTheme.colors.onPrimary else Color.Gray
                        )
                    )
                }
                Box(
                    modifier = Modifier
                        .height(50.dp)
                        .width(90.dp)
                        .clickable {
                            category = TabCategory.TIMELINE
                        },
                    contentAlignment = Alignment.CenterStart
                    ) {

                    Text(
                        "Timeline",
                        style = MaterialTheme.typography.body1.copy(
                            color = if (isSelected(TabCategory.TIMELINE)) MaterialTheme.colors.onPrimary else Color.Gray
                        )
                    )
                }
                Box(
                    modifier = Modifier
                        .height(50.dp)
                        .width(70.dp)
                        .clickable {
                            category = TabCategory.DAILY
                        },
                    contentAlignment = Alignment.CenterStart
                    ) {

                    Text(
                        "Daily",
                        style = MaterialTheme.typography.body1.copy(
                            color = if (isSelected(TabCategory.DAILY)) MaterialTheme.colors.onPrimary else Color.Gray
                        )
                    )
                }
            }
        }
    }
}


@Composable
fun TopBarView(model: WeatherViewModel, controller: NavController){

    var dropExtended by remember {
        mutableStateOf(false)
    }
    Row(modifier = Modifier
        .padding(top = 30.dp)
        .padding(horizontal = 20.dp)
        .fillMaxWidth()
        .height(80.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth(0.75f)) {

            Box() {
                if (model.locations.isEmpty()) {
                    Text("N/A", style = MaterialTheme.typography.h3)
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable(onClick = { dropExtended = !dropExtended  })
                    ) {
                        if (model.locations[model.index].isCurrent) {
                            Icon(Icons.Default.LocationOn, contentDescription = "")
                        }
                        Text(
                            model.locations[model.index].name,
                            style = MaterialTheme.typography.h3
                        )
                    }
                }
                DropdownMenu(expanded = dropExtended, onDismissRequest = { /*TODO*/ }) {


                        model.locations.forEachIndexed { index, item ->
                            DropdownMenuItem(onClick = {
                                model.index = index
                                dropExtended = false
                            },
                            modifier = Modifier.width(200.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (item.isCurrent) {
                                        Icon(Icons.Default.LocationOn, contentDescription = "")
                                    }
                                    Text(
                                        item.name,
                                        style = MaterialTheme.typography.body1
                                    )
                                }
                            }
                        }

                }
            }

          Text(if(model.locations.isEmpty()) "No data" else
              Date.from(Instant.ofEpochSecond(model.locations[model.index].data.current.dt)).timeAgo(),
              style =  MaterialTheme.typography.body2.copy(color = Color.Gray),modifier = Modifier.padding(start = 5.dp))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp),verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                controller.navigate("AddPage")
            }) {
                Icon(Icons.Default.Add, contentDescription = "",
                modifier = Modifier.size(30.dp))
            }
            IconButton(onClick = {
                controller.navigate("Settings")
            }) {
                Icon(Icons.Default.Menu, contentDescription = "",
                    modifier = Modifier.size(30.dp))
            }
        }
    }
}

@ExperimentalCoilApi
@Composable
fun TopWeatherCard(model: WeatherViewModel){


    Row(modifier = Modifier
        .padding(horizontal = 20.dp)
        .height(120.dp)
        .fillMaxWidth()
        ,
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically) {
        if(!model.locations.isEmpty()) {
            Image(
                painter = rememberImagePainter(data = "https://openweathermap.org/img/wn/${model.locations[model.index].data.current.weather[0].icon}@4x.png"),
                contentDescription = "weather image",
                modifier = Modifier.size(120.dp),
                contentScale = ContentScale.Fit
            )
        }
        Column(modifier = Modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.SpaceEvenly) {

            Text(if(model.locations.isEmpty()) "__°" else (model.locations[model.index].data.current.temp.toUInt().toString() + "°"),style = MaterialTheme.typography.h1.copy(fontSize = 40.sp))
            Text(if(model.locations.isEmpty()) "N/A" else model.locations[model.index].data.current.weather[0].description,style = MaterialTheme.typography.body2)
        }

    }
}

@ExperimentalCoilApi
@Composable
fun NewMainView(model: WeatherViewModel, controller: NavController) {
    val map = rememberMapViewWithLifecycle()
    val mapType by remember { mutableStateOf("clouds_new") }
    var coordinates by remember {
        mutableStateOf(
            if (model.locations.isNotEmpty()) LatLng(
                model.locations[model.index].data.lat,
                model.locations[model.index].data.lon
            ) else LatLng(37.9838, 23.7275)
        )
    }
    var zoom by rememberSaveable(map) { mutableStateOf(InitialZoom) }

    val overlays: MutableList<TileOverlay> by remember { mutableStateOf(ArrayList<TileOverlay>()) }

    Scaffold(
        topBar = {

            TopBarView(model = model, controller = controller)

        },
        bottomBar = {
            WeatherTabView(model = model, onTabChange = {})
        },

        // backgroundColor = if(model.locations.isEmpty()) Color(235, 220, 180) else getWeatherColor(model.locations[model.index].data.current.weather[0].icon)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {

            WeatherSynopsis(model = model)


        }
    }
}

@ExperimentalCoilApi
@Composable
fun WeatherSynopsis(model: WeatherViewModel){
    val map = rememberMapViewWithLifecycle()
    val mapType by remember { mutableStateOf("clouds_new") }
    var coordinates by remember {
        mutableStateOf(
            if (model.locations.isNotEmpty()) LatLng(
                model.locations[model.index].data.lat,
                model.locations[model.index].data.lon
            ) else LatLng(37.9838, 23.7275)
        )
    }
    var zoom by rememberSaveable(map) { mutableStateOf(InitialZoom) }

    val overlays: MutableList<TileOverlay> by remember { mutableStateOf(ArrayList<TileOverlay>()) }

        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {

 Box(  modifier = Modifier
     .padding(bottom = 10.dp)
     .size(200.dp),contentAlignment = Alignment.BottomCenter){
     if(!model.locations.isEmpty()) {
         Image(
             painter = rememberImagePainter(data = "https://openweathermap.org/img/wn/${model.locations[model.index].data.current.weather[0].icon}@4x.png"),
             contentDescription = "weather image",
             modifier = Modifier
                 .offset(y = (-50).dp)
                 .size(180.dp),
             contentScale = ContentScale.Fit
         )
     }
     Column( horizontalAlignment = Alignment.CenterHorizontally,
         verticalArrangement = Arrangement.spacedBy(8.dp)) {

         Text(
             if (model.locations.isEmpty()) "__°" else (model.locations[model.index].data.current.temp.toUInt()
                 .toString() + "°"), style = MaterialTheme.typography.h1.copy(fontSize = 40.sp)
         )
         Text(
             if (model.locations.isEmpty()) "N/A" else model.locations[model.index].data.current.weather[0].description,
             style = MaterialTheme.typography.body2
         )
     }
 }



            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically) {
              Row(modifier = Modifier.fillMaxWidth(0.5f),
              horizontalArrangement = Arrangement.spacedBy(10.dp),
              verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier
                    .size(34.dp)
                    .background(
                        color = Color.LightGray.copy(alpha = 0.5f),
                        shape = CircleShape
                    ),contentAlignment = Alignment.Center){
                    Icon(Icons.Default.Thermostat,contentDescription = "",
                    modifier = Modifier.size(20.dp),tint = Color.DarkGray)
                }
                  Column(horizontalAlignment = Alignment.Start,
                  verticalArrangement = Arrangement.SpaceBetween) {
                      Text("Feels like",style = MaterialTheme.typography.body2.copy(color=Color.Gray))
                      Text(if(model.locations.isEmpty()) "__°" else (model.locations[model.index].data.current.feels_like.toUInt().toString() + "°"),style = MaterialTheme.typography.body1)
                  }
              }
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier
                        .size(34.dp)
                        .background(
                            color = Color.LightGray.copy(alpha = 0.5f),
                            shape = CircleShape
                        ),contentAlignment = Alignment.Center){
                        Icon(Icons.Default.Air,contentDescription = "",
                            modifier = Modifier.size(20.dp),tint = Color.DarkGray)
                    }
                    Column(horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.SpaceBetween) {
                        Text("Wind",style = MaterialTheme.typography.body2.copy(color=Color.Gray))
                        Text(if(model.locations.isEmpty()) "__" else (model.locations[model.index].data.current.wind_speed.round(1).toString() + "mph"),style = MaterialTheme.typography.body1)
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically) {
                Row(modifier = Modifier.fillMaxWidth(0.5f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier
                        .size(34.dp)
                        .background(
                            color = Color.LightGray.copy(alpha = 0.5f),
                            shape = CircleShape
                        ),contentAlignment = Alignment.Center){
                        Icon(Icons.Default.Umbrella,contentDescription = "",
                            modifier = Modifier.size(20.dp),tint = Color.DarkGray)
                    }
                    Column(horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.SpaceBetween) {
                        Text("Precipitation",style = MaterialTheme.typography.body2.copy(color=Color.Gray))
                        Text(if(model.locations.isEmpty()) "__%" else ((model.locations[model.index].data.current.pop ?: 0).toString() + "%"),style = MaterialTheme.typography.body1)
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier
                        .size(34.dp)
                        .background(
                            color = Color.LightGray.copy(alpha = 0.5f),
                            shape = CircleShape
                        ),contentAlignment = Alignment.Center){
                        Image(painter= painterResource(id = R.drawable.drop),
                            modifier = Modifier.size(20.dp),contentDescription = "",
                            colorFilter = ColorFilter.tint(color = Color.DarkGray),
                        contentScale = ContentScale.Fit)
                    }
                    Column(horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.SpaceBetween) {
                        Text("Humidity",style = MaterialTheme.typography.body2.copy(color=Color.Gray))
                        Text(if(model.locations.isEmpty()) "__%" else (model.locations[model.index].data.current.humidity.toUInt().toString() + "%"),style = MaterialTheme.typography.body1)
                    }
                }
            }
    Box(modifier = Modifier
        .height(220.dp)
        .fillMaxWidth()
        .clip(RoundedCornerShape(12))){
        MapViewContainer(
            map = map,
            latitude = coordinates.latitude,
            longitude = coordinates.longitude,
            mapType = mapType,
            model = model,
            index = model.index
        )
    }
        }

}


//@Preview
//@Composable
//fun WeatherTabPreview(){
//    DarkWeatherTheme {
//        NewMainView()
//       // TopBarView()
//        //WeatherTabView(onTabChange = {})
//    }
//}