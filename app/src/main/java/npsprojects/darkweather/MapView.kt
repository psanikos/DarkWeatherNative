package npsprojects.darkweather

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.location.Address
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.FloatRange
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Autorenew
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.twotone.CheckBox
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import npsprojects.darkweather.MyApp.context
import java.net.MalformedURLException
import java.net.URL
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.scale
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import kotlinx.coroutines.launch
import npsprojects.darkweather.ui.theme.*
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

private val weatherKey = "e1e45feaea76d66517c25291f2633d9a"


enum class MyBottomSheetState {
        COLLAPSED,MEDIUM,EXPANDED
}



@ExperimentalMaterialApi
@Composable fun MySheet(maxHeight: Dp, content: @Composable() () -> Unit, body:@Composable() () -> Unit){

 var state by remember { mutableStateOf(MyBottomSheetState.COLLAPSED)}
    val swipeableState = rememberSwipeableState(MyBottomSheetState.COLLAPSED)
    val offset by animateOffsetAsState(targetValue = (if (state == MyBottomSheetState.COLLAPSED) Offset(x = 0f,y = 600f) else if (state ==  MyBottomSheetState.MEDIUM ) Offset(x = 0f,y = 300f) else Offset(x = 0f,y = 0f)))
val dragState = rememberDraggableState{
    if (it > 0f) {

        if (state == MyBottomSheetState.MEDIUM) {
            state = MyBottomSheetState.EXPANDED
        } else if (state == MyBottomSheetState.COLLAPSED) {
            state = MyBottomSheetState.MEDIUM
        }

    } else {
        if (state == MyBottomSheetState.MEDIUM) {
            state = MyBottomSheetState.COLLAPSED
        } else if (state == MyBottomSheetState.EXPANDED) {
            state = MyBottomSheetState.MEDIUM
        }


    }
}

Box(modifier = Modifier.fillMaxSize(),contentAlignment = Alignment.BottomCenter){
    body()
    Box(modifier = Modifier
        .offset(y = offset.y.dp)
        .fillMaxWidth()
        .height(maxHeight)

        .background(
            color = blue_grey_100,
            shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp)
        )
        .draggable(state = dragState, orientation = Orientation.Vertical)

        ,contentAlignment = Alignment.CenterStart){
        Column(modifier = Modifier.fillMaxSize(),horizontalAlignment = Alignment.CenterHorizontally,verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(modifier = Modifier
                .height(8.dp)
                .width(60.dp)
                .background(color = Color.DarkGray, shape = RoundedCornerShape(30))

            )
            content()
        }
    }


}
    
}

class CustomInfoWindowForGoogleMap(context: Context) : GoogleMap.InfoWindowAdapter {

    var mContext = context
    var mWindow = (context as Activity).layoutInflater.inflate(R.layout.custom_info_window, null)

    private fun rendowWindowText(marker: Marker, view: View){

        val tvTitle = view.findViewById<TextView>(R.id.title)
        val tvTemp = view.findViewById<TextView>(R.id.temp)
        val tvSnippet = view.findViewById<TextView>(R.id.snippet)
        marker.title?.let {
            tvTitle.text = it.split(",").toTypedArray()[0]
            tvTemp.text = it.split(",").toTypedArray()[1]
        }
        tvSnippet.text = marker.snippet

    }

    override fun getInfoContents(marker: Marker): View {
        rendowWindowText(marker, mWindow)
        return mWindow
    }

    override fun getInfoWindow(marker: Marker): View? {
        rendowWindowText(marker, mWindow)
        return mWindow
    }
}


@ExperimentalFoundationApi
@ExperimentalMaterialApi

@Composable
fun NewMapView(model: WeatherViewModel,  controller: NavController) {
    val map = rememberMapViewWithLifecycle()
    val scope = rememberCoroutineScope()
    var mapType by remember { mutableStateOf("clouds_new") }
    var coordinates by remember { mutableStateOf(if(model.locations.isNotEmpty())  LatLng(model.locations[0].data.latitude,model.locations[0].data.longitude) else LatLng(37.9838, 23.7275)
    ) }
    var zoom by rememberSaveable(map) { mutableStateOf(InitialZoom) }
    var index:Int by remember { mutableStateOf(0)}
    val overlays: MutableList<TileOverlay> by remember { mutableStateOf(ArrayList<TileOverlay>()) }
    var searchTerm by remember { mutableStateOf("")}
    val backColor = if (isSystemInDarkTheme()) Color(0xFF202020) else Color(0xFFF5F5F5)
    val cardColor =  if (isSystemInDarkTheme()) Color(0xFF101010) else Color.White
    var isLoading by remember { mutableStateOf(false)}
    BottomSheetScaffold(sheetContent = {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(750.dp)
                .background(backColor)
        ) {
            if (!isLoading) {
                Column(modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(50.dp)
                            .background(Color.DarkGray, shape = RoundedCornerShape(40))
                    )

                    LazyColumn(
                        Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        item {
                            Box(
                                modifier = Modifier

                                    .fillMaxWidth()

                                    .height(45.dp)
                                    .background(
                                        color = cardColor,
                                        shape = RoundedCornerShape(20.dp)
                                    ),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                BasicTextField(
                                    value = searchTerm,
                                    onValueChange = {
                                        searchTerm = it

                                    },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Text,
                                        imeAction = ImeAction.Search
                                    ),
                                    keyboardActions = KeyboardActions(onSearch = {
                                      scope.launch {
                                          model.getCoordinatesFromLocation(searchTerm)
                                      }
                                    }),
                                    modifier = Modifier
                                        .padding(start = 20.dp)
                                        .fillMaxWidth(0.9f),
                                    textStyle = MaterialTheme.typography.caption,

                                    )
                                if (searchTerm == "") {
                                    Text(
                                        "Search a new place",
                                        style = MaterialTheme.typography.caption,
                                        modifier = Modifier
                                            .padding(start = 20.dp)
                                    )
                                }
                            }
                        }


                        model.searchedAdresses.forEach {
                            if (it.subLocality != null || it.locality != null || it.featureName != null) {
                                item {
                                    Row(
                                        modifier = Modifier
                                            .padding(vertical = 5.dp)
                                            .fillMaxWidth()
                                            .height(50.dp)
                                            .background(
                                                color = cardColor,
                                                shape = RoundedCornerShape(20.dp)
                                            )
                                            .clickable {
                                                isLoading = true
                                                scope.launch {
                                                    model.getDataFromCoordinates(
                                                        latitude = it.latitude,
                                                        it.longitude,
                                                        (if (it.subLocality != null) it.subLocality else it.locality)
                                                    ) { locationData ->
                                                        if (locationData != null) {
                                                            var savedLocations =
                                                                model.locations.toMutableList()
                                                            savedLocations.add(locationData)
                                                            model.locations =
                                                                savedLocations.toList()
                                                        }
                                                        isLoading = false
                                                        searchTerm = ""
                                                        model.searchedAdresses.clear()
                                                    }
                                                }


                                            },
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            if (it.featureName != null) it.featureName else if (it.subLocality != null) it.subLocality else it.locality,
                                            style = MaterialTheme.typography.body2,
                                            modifier = Modifier.padding(
                                                horizontal = 16.dp,
                                                vertical = 8.dp
                                            )
                                        )

                                    }
                                }
                            }
                        }
                        item {
                            LazyRow(

                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                item {
                                    IconButton(onClick = {
                                        model.getCurrentLocationWeather()
                                    }) {
                                        Box(
                                            modifier = Modifier
                                                .height(30.dp)
                                                .width(30.dp)
                                                .background(
                                                    color = Color(0xFF101010),
                                                    shape = CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Rounded.Autorenew,
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp),
                                                contentDescription = ""
                                            )
                                        }
                                    }
                                }
                                itemsIndexed(model.locations) { i, item ->
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = if (index == i) cardColor else Color.Transparent,
                                                shape = RoundedCornerShape(50)
                                            )
                                            .clickable {
                                                coordinates =
                                                    LatLng(item.data.latitude, item.data.longitude)
                                                index = i

                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(modifier = Modifier.padding(4.dp)) {
                                            if (item.isCurrent) {
                                                Icon(
                                                    Icons.Rounded.LocationOn,
                                                    tint = if (index == i) (if (isSystemInDarkTheme()) Color.White else Color.Black) else Color.Gray,
                                                    contentDescription = "",
                                                    modifier = Modifier.size(25.dp)
                                                )
                                            }
                                            Text(
                                                item.name,
                                                style = MaterialTheme.typography.button.copy(
                                                    color = if (index == i) (if (isSystemInDarkTheme()) Color.White else Color.Black)  else Color.Gray
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
                        item {
                            Row(
                                modifier = Modifier

                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {

                                        mapType = "clouds_new"

                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        contentColor = blue_500,
                                        backgroundColor = indigo_500.copy(alpha = if (mapType == "clouds_new") 0.4f else 0.1f)
                                    ),
                                    contentPadding = PaddingValues(12.dp),
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier
                                        .width(80.dp)
                                        .height(40.dp),

                                    elevation = ButtonDefaults.elevation(
                                        defaultElevation = 0.dp,
                                        pressedElevation = 0.dp,
                                        disabledElevation = 0.dp
                                    )
                                ) {

                                    Icon(Icons.Filled.Cloud, contentDescription = null)
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("Clouds", style = MaterialTheme.typography.caption)
                                }

                                Button(
                                    onClick = {
                                        mapType = "precipitation_new"
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        contentColor = blue_500,
                                        backgroundColor = indigo_500.copy(alpha = if (mapType == "precipitation_new") 0.4f else 0.1f)
                                    ),
                                    contentPadding = PaddingValues(12.dp),
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier
                                        .width(70.dp)
                                        .height(40.dp),
                                    elevation = ButtonDefaults.elevation(
                                        defaultElevation = 0.dp,
                                        pressedElevation = 0.dp,
                                        disabledElevation = 0.dp
                                    )
                                ) {
                                    Icon(Icons.Filled.Opacity, contentDescription = null)
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("Rain", style = MaterialTheme.typography.caption)
                                }
                                Button(
                                    onClick = {
                                        mapType = "temp_new"


                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        contentColor = blue_500,
                                        backgroundColor = indigo_500.copy(alpha = if (mapType == "temp_new") 0.4f else 0.1f)
                                    ),
                                    contentPadding = PaddingValues(12.dp),
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier
                                        .width(110.dp)
                                        .height(40.dp),
                                    elevation = ButtonDefaults.elevation(
                                        defaultElevation = 0.dp,
                                        pressedElevation = 0.dp,
                                        disabledElevation = 0.dp
                                    )
                                ) {
                                    Icon(Icons.Filled.Thermostat, contentDescription = null)
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("Temperature", style = MaterialTheme.typography.caption)
                                }
                                Button(
                                    onClick = {
                                        mapType = "wind_new"
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        contentColor = blue_500,
                                        backgroundColor = indigo_500.copy(alpha = if (mapType == "wind_new") 0.4f else 0.1f)
                                    ),
                                    contentPadding = PaddingValues(12.dp),
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier
                                        .width(70.dp)
                                        .height(40.dp),
                                    elevation = ButtonDefaults.elevation(
                                        defaultElevation = 0.dp,
                                        pressedElevation = 0.dp,
                                        disabledElevation = 0.dp
                                    )
                                ) {
                                    Icon(Icons.Filled.Air, contentDescription = null)
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("Wind", style = MaterialTheme.typography.caption)
                                }
                            }
                        }
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
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

Row() {

    Box(modifier = Modifier.height(100.dp)) {
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
                text = "Feels like ${model.locations[index].data.currently.apparentTemperature!!.toInt()}°",
                style = MaterialTheme.typography.caption.copy(
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(0.5f, 0.5f)
                    ),
                    color = pink_100
                ),

                textAlign = TextAlign.Center
            )
        }
    }
    Box(modifier = Modifier
        .height(100.dp)
        .padding(start = 10.dp)) {


        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
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
                                            .height(100.dp)
                                            .width(100.dp)
                                    )
                                }
                            }
                        }


                        item {
                            LazyRow(
                                modifier = Modifier.fillMaxWidth()


                            ) {

                                model.locations[index].data.hourly.data.forEach {
                                    item {
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
                        item {
                            WeeklyTimes(
                                data = model.locations[index].data.daily.data,
                                units = model.units
                            )
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
                                                color = cardColor,
                                                shape = RoundedCornerShape(20.dp)
                                            )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .padding(4.dp)
                                                .height(100.dp)
                                                .width(80.dp),
                                            verticalArrangement = Arrangement.SpaceEvenly,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Image(
                                                painter = painterResource(id = R.drawable.humidity),
                                                contentDescription = "",
                                                modifier = Modifier.size(25.dp)
                                            )
                                            Text(
                                                "${(100 * model.locations[index].data.currently.humidity!!).toInt()}%",
                                                style = MaterialTheme.typography.h4
                                            )
                                        }
                                    }


                                    Box(
                                        modifier = Modifier
                                            .height(100.dp)
                                            .width(80.dp)
                                            .background(
                                                color = when (model.locations[index].data.currently.uvIndex) {
                                                    0, 1, 2 -> Color.Green.copy(alpha = 0.3f)
                                                    3, 4, 5 -> Color.Yellow.copy(alpha = 0.3f)
                                                    6, 7 -> orange_500.copy(alpha = 0.3f)
                                                    else -> Color.Red.copy(alpha = 0.3f)
                                                },
                                                shape = RoundedCornerShape(20.dp)
                                            )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .padding(4.dp)
                                                .height(100.dp)
                                                .width(80.dp),
                                            verticalArrangement = Arrangement.SpaceEvenly,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Image(
                                                painter = painterResource(id = R.drawable.uv),
                                                contentDescription = "",
                                                modifier = Modifier.size(25.dp)
                                            )
                                            Text(
                                                "${model.locations[index].data.currently.uvIndex}",
                                                style = MaterialTheme.typography.h4
                                            )
                                        }
                                    }



                                    Box(
                                        modifier = Modifier
                                            .height(100.dp)
                                            .width(80.dp)
                                            .background(
                                                color = cardColor,
                                                shape = RoundedCornerShape(20.dp)
                                            )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .padding(4.dp)
                                                .height(100.dp)
                                                .width(80.dp),
                                            verticalArrangement = Arrangement.SpaceEvenly,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Image(
                                                painter = painterResource(id = R.drawable.sunrise),
                                                contentDescription = "",
                                                modifier = Modifier.size(25.dp)
                                            )
                                            Text(

                                                SimpleDateFormat("HH:mm").format(1000 * model.locations[index].data.daily.data[0].sunriseTime!!.toLong()),
                                                style = MaterialTheme.typography.h4
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .height(100.dp)
                                            .width(80.dp)
                                            .background(
                                                color = cardColor,
                                                shape = RoundedCornerShape(20.dp)
                                            )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .padding(4.dp)
                                                .height(100.dp)
                                                .width(80.dp),
                                            verticalArrangement = Arrangement.SpaceEvenly,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Image(
                                                painter = painterResource(id = R.drawable.sunset),
                                                contentDescription = "",
                                                modifier = Modifier.size(25.dp)
                                            )
                                            Text(
                                                SimpleDateFormat("HH:mm").format(1000 * model.locations[index].data.daily.data[0].sunsetTime!!.toLong()),
                                                style = MaterialTheme.typography.h4
                                            )
                                        }
                                    }
                                }


                            }


                        }
                        item {
                            Spacer(modifier = Modifier.height(40.dp))
                        }
                        item {
                            if (model.myLocations.any { it.name == model.locations[index].name }){
                                if (!model.locations[index].isCurrent) {
                                    Box(modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .background(
                                            color = red_700,
                                            shape = RoundedCornerShape(20.dp)
                                        )
                                        .clickable {
                                            val oldIndex = index
                                            index = 0
                                            scope.launch {
                                                model.remove(
                                                    SavedLocation(
                                                        model.locations[oldIndex].name,
                                                        model.locations[oldIndex].data.latitude.round(
                                                            2
                                                        ),
                                                        model.locations[oldIndex].data.longitude.round(
                                                            2
                                                        )
                                                    )
                                                )
                                            }
                                        },contentAlignment = Alignment.Center) {
                                        Text(
                                            "Remove from list",
                                            style = MaterialTheme.typography.h4.copy(color = Color.White)
                                        )
                                    }
                                }
                            }else {
                                if (!model.locations[index].isCurrent) {
                                    Box(modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .background(
                                            color = teal_500,
                                            shape = RoundedCornerShape(20.dp)
                                        )
                                        .clickable {
                                            if (!model.myLocations.any { it.name == model.locations[index].name }) {
                                                scope.launch {
                                                    model.saveLocation(
                                                        SavedLocation(
                                                            model.locations[index].name,
                                                            model.locations[index].data.latitude.round(
                                                                2
                                                            ),
                                                            model.locations[index].data.longitude.round(
                                                                2
                                                            )
                                                        )
                                                    )
                                                }
                                            }
                                        }, contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "Add to list",
                                            style = MaterialTheme.typography.h4.copy(color = Color.White)
                                        )
                                    }
                                }
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(40.dp))
                        }
                    }
                }
            }
        }
    },
        sheetPeekHeight = 400.dp,
        sheetShape = RoundedCornerShape(topEnd = 20.dp,topStart = 20.dp),
        sheetElevation = 10.dp
    ) {
      if(!isLoading){
          Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.fillMaxSize()) {


              Box(contentAlignment = Alignment.CenterEnd, modifier = Modifier.fillMaxSize()) {
                  MapViewContainer(
                      map = map,
                      latitude = coordinates.latitude,
                      longitude = coordinates.longitude,
                      mapType = mapType,
                      model = model,
                      index = index
                  )
              }
              Row(modifier = Modifier
                  .padding(10.dp)
                  .padding(top = 10.dp)
                  .fillMaxWidth(),horizontalArrangement = Arrangement.End) {
                  IconButton(onClick = {
                      controller.navigate("Settings")
                  }) {
                      Icon(
                          Icons.Rounded.Settings,
                          tint = Color.Black,
                          modifier = Modifier.size(25.dp),
                          contentDescription = ""
                      )

                  }
              }

          }
        }
    }

}




private const val InitialZoom = 5f
const val MinZoom = 2f
const val MaxZoom = 8f


@Composable
private fun MapViewContainer(
    map: MapView,
    latitude: Double,
    longitude: Double,
    mapType:String,
    model: WeatherViewModel,
    index:Int,


) {

    var zoom by rememberSaveable(map) { mutableStateOf(InitialZoom) }

    val coroutineScope = rememberCoroutineScope()
    var hasOverlay by remember { mutableStateOf(false)}
    val coordinates = LatLng(latitude,longitude)
    var overlays:MutableList<TileOverlay>  by remember { mutableStateOf(  ArrayList<TileOverlay>())}
val darkTheme = isSystemInDarkTheme()
    Box(contentAlignment = Alignment.CenterEnd,modifier = Modifier.fillMaxSize()) {
    AndroidView({ map }) { mapView ->

        val mapZoom = zoom

        mapView.getMapAsync {
            it.setMapStyle(if(darkTheme) MapStyleOptions.loadRawResourceStyle(context,R.raw.map_in_dark) else null)
            val tileProvider: TileProvider = object : UrlTileProvider(256, 256) {
                override fun getTileUrl(x: Int, y: Int, zoom: Int): URL? {

                    /* Define the URL pattern for the tile images */

                    val url =
                        "https://tile.openweathermap.org/map/$mapType/$zoom/$x/$y.png?appid=$weatherKey"
                    return if (!checkTileExists(x, y, zoom)) {

                        null
                    } else try {
                        URL(url)
                    } catch (e: MalformedURLException) {
                        throw AssertionError(e)
                    }
                }




                private fun checkTileExists(x: Int, y: Int, zoom: Int): Boolean {
                    val minZoom = MinZoom.toInt()
                    val maxZoom = MaxZoom.toInt()
                    return zoom in minZoom..maxZoom
                }
            }
            it.setZoom(mapZoom)
            it.setInfoWindowAdapter(CustomInfoWindowForGoogleMap(context = context))
            val icon = BitmapFactory.decodeResource(context.resources,getWeatherIcon(input = model.locations[index].data.currently.icon ?: ""))
            val smallMarker = icon.scale(width = 100,height = 100,filter = false)
            val markerOptions = MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                .title("${model.locations[index].name},${model.locations[index].data.currently.temperature?.toInt()}°")
                .snippet("${model.locations[index].data.currently.summary}")



            val marker =  it.addMarker(

                markerOptions.position(coordinates)
            )
            marker?.showInfoWindow()
//            it.addMarker(
//
//                MarkerOptions().position(position)
//            )
            it.animateCamera(CameraUpdateFactory.newLatLng(coordinates))

            val tile = it.addTileOverlay(
                TileOverlayOptions()
                    .tileProvider(tileProvider).fadeIn(true)
            )

            tile?.let { overlay ->
                overlays.add(overlay)
            }
            overlays.forEachIndexed { index, item ->
                if (index == overlays.size - 1) {

                } else {
                    item.remove()
                }
            }


        }

    }
//    Column(
//        Modifier
//            .padding(5.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.spacedBy(10.dp)
//    ) {
//        Icon(Icons.Filled.AddCircle,contentDescription = "",tint = indigo_500,modifier = Modifier
//            .size(40.dp)
//            .clickable {
//                if (zoom < MaxZoom) {
//                    zoom += 0.2f
//                }
//            })
//        Icon(Icons.Filled.RemoveCircle,contentDescription = "",tint = indigo_500,modifier = Modifier
//            .size(40.dp)
//            .clickable {
//                if (zoom > MinZoom) {
//                    zoom -= 0.2f
//                }
//            })
//    }
}
}




@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            id = R.id.map
        }
    }

    // Makes MapView follow the lifecycle of this composable
    val lifecycleObserver = rememberMapLifecycleObserver(mapView)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }

    return mapView
}

@Composable
private fun rememberMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
    remember(mapView) {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> throw IllegalStateException()
            }
        }
    }


fun GoogleMap.setZoom(
    @FloatRange(from = MinZoom.toDouble(), to = MaxZoom.toDouble()) zoom: Float
) {
    resetMinMaxZoomPreference()
    setMinZoomPreference(zoom)
    setMaxZoomPreference(zoom)
}