package npsprojects.darkweather

import android.util.Log
import android.view.View
import android.view.WindowInsets
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener
import androidx.navigation.NavController

import com.google.accompanist.insets.LocalWindowInsets
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileOverlay
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import npsprojects.darkweather.MyApp.context
import npsprojects.darkweather.ui.theme.*
import java.io.IOException
import java.net.InetAddress
import java.text.SimpleDateFormat

@ExperimentalAnimationApi
@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterialApi
@Composable
fun MainPageView(model: WeatherViewModel, controller: NavController) {

    if ((model.myLocations.size + 1 > model.locations.size) || model.locations.isEmpty()) {
        LaunchedEffect(key1 = "GetData") {

                model.getCurrentLocationWeather()

        }
        }



    val scope = rememberCoroutineScope()
    var index: Int by remember { mutableStateOf(0) }

    val state = rememberScaffoldState(
        rememberDrawerState(DrawerValue.Closed)
    )
    var currentPage by remember { mutableStateOf("Main") }
    var offset: Float by remember { mutableStateOf(0f) }
    val swipableModifier = Modifier.draggable(
        orientation = Orientation.Horizontal,
        state = rememberDraggableState { delta ->
            offset = delta

        },
        onDragStopped = {
            if (offset > 0) {
                if (index > 0) {
                    index--
                }
                offset = 0f
            } else if (offset < 0) {
                if (index < model.locations.size - 1) {
                    index++
                }
                offset = 0f
            }
        }

    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),


        ) {
        when (model.isLoading) {
            true -> LoadingView(model = model)

            false -> NewMapView(model = model,controller=controller)

            }


        }


}

fun isOnline() : Boolean {
    val runtime = Runtime.getRuntime()
    try {
        val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
        val exitValue = ipProcess.waitFor()
        return exitValue == 0
    } catch (e: IOException) {
        e.printStackTrace()
    } catch (e: InterruptedException) {
        e.printStackTrace()
    }
    return false
}



@ExperimentalFoundationApi
@ExperimentalMaterialApi

@Composable
fun NewMapView(model: WeatherViewModel,  controller: NavController) {
    val map = rememberMapViewWithLifecycle()
    val scope = rememberCoroutineScope()
    var mapType by remember { mutableStateOf("clouds_new") }
    var coordinates by remember {
        mutableStateOf(
            if (model.locations.isNotEmpty()) LatLng(
                model.locations[0].data.latitude,
                model.locations[0].data.longitude
            ) else LatLng(37.9838, 23.7275)
        )
    }
    var zoom by rememberSaveable(map) { mutableStateOf(InitialZoom) }
    var index: Int by remember { mutableStateOf(0) }
    val overlays: MutableList<TileOverlay> by remember { mutableStateOf(ArrayList<TileOverlay>()) }
    var searchTerm by remember { mutableStateOf("") }
    val backColor = if (isSystemInDarkTheme()) Color(0xFF303030) else Color(0xFFf0f0f7)
    val cardColor = if (isSystemInDarkTheme()) Color(0xFF151515) else Color.White
    var isLoading by remember { mutableStateOf(false) }
    var isAlertExpanded by remember { mutableStateOf(false) }
    val insets = LocalWindowInsets.current
    val state = rememberBottomSheetScaffoldState(drawerState = DrawerState(initialValue = DrawerValue.Closed))
    var showAlert:Boolean by remember { mutableStateOf(false)}
    BottomSheetScaffold(
            sheetContent = {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(720.dp)
                        .background(backColor)
                ) {
                    if (!isLoading) {
                        Column(
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(5.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
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
                                verticalArrangement = Arrangement.spacedBy(15.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(45.dp)
                                            .background(
                                                color = cardColor.copy(alpha = 0.6f),
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
                                                    model.getCoordinatesFromLocation(searchTerm){
                                                        success ->
                                                        if (!success){
                                                            showAlert = true
                                                        }
                                                    }
                                                }
                                            }),
                                            modifier = Modifier
                                                .padding(start = 20.dp)
                                                .fillMaxWidth(0.9f),
                                            textStyle = MaterialTheme.typography.caption.copy(color = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray),cursorBrush = Brush.horizontalGradient(colors = listOf(Color.Blue,Color.Gray))

                                            )
                                        if (searchTerm == "") {
                                            Text(
                                                stringResource(R.string.searchText),
                                                style = MaterialTheme.typography.caption,
                                                modifier = Modifier
                                                    .padding(start = 20.dp)
                                            )
                                        }
                                    }
                                }


                                model.searchedAdresses.forEach {
                                    if(it.locality != null || it.featureName != null) {
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
                                                                longitude = it.longitude,
                                                                name = it.locality ?: it.featureName
                                                            )
                                                            { locationData ->
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
                                                horizontalArrangement = Arrangement.Start,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    it.locality ?: it.featureName,
                                                    style = MaterialTheme.typography.body2,
                                                    modifier = Modifier.padding(
                                                        horizontal = 16.dp,
                                                        vertical = 8.dp
                                                    )
                                                )

                                Text(it.countryName ?: "",   style = MaterialTheme.typography.caption,
                                    modifier = Modifier.padding( horizontal = 20.dp,vertical = 8.dp))
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
                                                model.error = WeatherError.NONE
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
                                                            LatLng(
                                                                item.data.latitude,
                                                                item.data.longitude
                                                            )
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
                                                            color = if (index == i) (if (isSystemInDarkTheme()) Color.White else Color.Black) else Color.Gray
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
                                if (model.locations.isNotEmpty()) {
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
                                                contentPadding = PaddingValues(10.dp),
                                                shape = RoundedCornerShape(50),
                                                modifier = Modifier
                                                    .width(90.dp)
                                                    .height(35.dp),

                                                elevation = ButtonDefaults.elevation(
                                                    defaultElevation = 0.dp,
                                                    pressedElevation = 0.dp,
                                                    disabledElevation = 0.dp
                                                )
                                            ) {

                                                Icon(
                                                    Icons.Filled.Cloud,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(2.dp))
                                                Text(
                                                    stringResource(R.string.clouds),
                                                    style = MaterialTheme.typography.caption
                                                )
                                            }

                                            Button(
                                                onClick = {
                                                    mapType = "precipitation_new"
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    contentColor = blue_500,
                                                    backgroundColor = indigo_500.copy(alpha = if (mapType == "precipitation_new") 0.4f else 0.1f)
                                                ),
                                                contentPadding = PaddingValues(10.dp),
                                                shape = RoundedCornerShape(50),
                                                modifier = Modifier
                                                    .width(75.dp)
                                                    .height(35.dp),
                                                elevation = ButtonDefaults.elevation(
                                                    defaultElevation = 0.dp,
                                                    pressedElevation = 0.dp,
                                                    disabledElevation = 0.dp
                                                )
                                            ) {
                                                Icon(
                                                    Icons.Filled.Opacity,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(2.dp))
                                                Text(
                                                    stringResource(R.string.rain),
                                                    style = MaterialTheme.typography.caption
                                                )
                                            }
                                            Button(
                                                onClick = {
                                                    mapType = "temp_new"


                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    contentColor = blue_500,
                                                    backgroundColor = indigo_500.copy(alpha = if (mapType == "temp_new") 0.4f else 0.1f)
                                                ),
                                                contentPadding = PaddingValues(10.dp),
                                                shape = RoundedCornerShape(50),
                                                modifier = Modifier
                                                    .width(115.dp)
                                                    .height(35.dp),
                                                elevation = ButtonDefaults.elevation(
                                                    defaultElevation = 0.dp,
                                                    pressedElevation = 0.dp,
                                                    disabledElevation = 0.dp
                                                )
                                            ) {
                                                Icon(
                                                    Icons.Filled.Thermostat,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(2.dp))
                                                Text(
                                                    stringResource(R.string.Temperature),
                                                    style = MaterialTheme.typography.caption
                                                )
                                            }
                                            Button(
                                                onClick = {
                                                    mapType = "wind_new"
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    contentColor = blue_500,
                                                    backgroundColor = indigo_500.copy(alpha = if (mapType == "wind_new") 0.4f else 0.1f)
                                                ),
                                                contentPadding = PaddingValues(10.dp),
                                                shape = RoundedCornerShape(50),
                                                modifier = Modifier
                                                    .width(75.dp)
                                                    .height(35.dp),
                                                elevation = ButtonDefaults.elevation(
                                                    defaultElevation = 0.dp,
                                                    pressedElevation = 0.dp,
                                                    disabledElevation = 0.dp
                                                )
                                            ) {
                                                Icon(
                                                    Icons.Filled.Air,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(2.dp))
                                                Text(
                                                    stringResource(R.string.Wind),
                                                    style = MaterialTheme.typography.caption
                                                )
                                            }
                                        }
                                    }
                                    item {
                                        WeatherMain(model = model, index = index)
                                    }


                                    item {
                                        HourlyView(model = model, index = index)
                                    }
                                    item {
                                        WeeklyTimes(
                                            data = model.locations[index].data.daily.data,
                                            units = model.units
                                        )
                                    }
                                    model.locations[index].airQuality?.list?.first()?.main?.aqi?.let { index ->


                                        item {
                                            AirQualityView(index = index)
                                        }
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
                                        if (model.myLocations.any { it.name == model.locations[index].name }) {
                                            if (!model.locations[index].isCurrent) {
                                                Box(
                                                    modifier = Modifier
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
                                                        }, contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        stringResource(id = R.string.ListRemove),
                                                        style = MaterialTheme.typography.h4.copy(
                                                            color = Color.White
                                                        )
                                                    )
                                                }
                                            }
                                        } else {
                                            if (!model.locations[index].isCurrent) {
                                                Box(
                                                    modifier = Modifier
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
                                                        stringResource(id = R.string.ListAdd),
                                                        style = MaterialTheme.typography.h4.copy(
                                                            color = Color.White
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    item {
                                        Spacer(modifier = Modifier.height(40.dp))
                                    }
                                } else {
                                    item {
                                        Text(
                                            if (isOnline()) stringResource(id = R.string.NoLocationNoAccess) else stringResource(
                                                id = R.string.NoInternet
                                            ),
                                            style = MaterialTheme.typography.body2.copy(color = Color.Gray),
                                            modifier = Modifier.padding(10.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            sheetPeekHeight = if(insets.ime.isVisible) 480.dp else 400.dp,
            sheetShape = RoundedCornerShape(topEnd = 20.dp, topStart = 20.dp),
            sheetElevation = 10.dp,
            scaffoldState = state
        ) {
            if (!isLoading) {
                Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.fillMaxSize()) {


                    Box(
                        contentAlignment = Alignment.CenterEnd, modifier = Modifier
                            .padding(bottom = 150.dp)
                            .fillMaxSize()
                    ) {
                        MapViewContainer(
                            map = map,
                            latitude = coordinates.latitude,
                            longitude = coordinates.longitude,
                            mapType = mapType,
                            model = model,
                            index = index
                        )
                    }

                    Row(
                        modifier = Modifier
                            .padding(10.dp)
                            .padding(top = 30.dp)
                            .fillMaxWidth(), horizontalArrangement = Arrangement.End
                    ) {
                        if (model.locations.isNotEmpty()) {
                            Box(modifier = Modifier.fillMaxWidth(0.9f)) {
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    item {

                                        if (model.locations[index].data.alerts.isNotEmpty()) {

                                            Surface(
                                                modifier = Modifier
                                                    .width(310.dp)
                                                    .animateContentSize()
                                                    .clickable {
                                                        isAlertExpanded = !isAlertExpanded
                                                    },
                                                color = if (model.locations[index].data.alerts[0].severity == "warning") red_500.copy(
                                                    alpha = 0.8f
                                                ) else orange_500.copy(alpha = 0.8f),
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
                                                            .padding(6.dp)
                                                            .size(30.dp),
                                                        tint = if (model.locations[index].data.alerts[0].severity == "warning") red_500 else orange_500
                                                    )
                                                    Column(
                                                        horizontalAlignment = Alignment.Start,
                                                        verticalArrangement = Arrangement.SpaceBetween,
                                                        modifier = Modifier.padding(6.dp)
                                                    ) {
                                                        Text(
                                                            model.locations[index].data.alerts[0].title,
                                                            style = MaterialTheme.typography.h4.copy(
                                                                fontSize = 14.sp
                                                            )
                                                        )

                                                        if (isAlertExpanded) {
                                                            Text(
                                                                model.locations[index].data.alerts[0].description.toLowerCase(),
                                                                style = MaterialTheme.typography.button.copy(
                                                                    fontSize = 11.sp
                                                                ),
                                                                modifier = Modifier.padding(vertical = 5.dp),

                                                                )
                                                        } else {
                                                            Text(
                                                                model.locations[index].data.alerts[0].description.toLowerCase(),
                                                                style = MaterialTheme.typography.button.copy(
                                                                    fontSize = 11.sp
                                                                ),
                                                                modifier = Modifier.padding(vertical = 5.dp),
                                                                maxLines = 2
                                                            )
                                                        }

                                                        Text(
                                                            "Until: " +
                                                                    SimpleDateFormat("EEEE dd  HH:mm").format(
                                                                        1000 * model.locations[index].data.alerts[0].expires.toLong()
                                                                    ),
                                                            style = MaterialTheme.typography.h4.copy(
                                                                fontSize = 12.sp
                                                            ),
                                                            lineHeight = 10.sp
                                                        )


                                                    }
                                                }
                                            }
                                        }
                                    }
                                    item {
                                        RainTimeAlert(rainProbability = model.locations[index].data.hourly.data)


                                    }
                                }
                            }
                        }
                        IconButton(onClick = {
                            controller.navigate("Settings")
                        }) {
                            Icon(
                                Icons.Rounded.Settings,
                                tint = if (isSystemInDarkTheme()) Color.White else Color.Black,
                                modifier = Modifier.size(25.dp),
                                contentDescription = ""
                            )


                        }
                    }
                    if (showAlert) {
                        // below line is use to
                        // display a alert dialog.
                        AlertDialog(
                            // on dialog dismiss we are setting
                            // our dialog value to false.
                            onDismissRequest = { showAlert = false },

                            // below line is use to display title of our dialog
                            // box and we are setting text color to white.
                            title = { Text(text = stringResource(id = R.string.NoResults), style = MaterialTheme.typography.h4) },

                            // below line is use to display
                            // description to our alert dialog.
                            text = { Text(stringResource(id = R.string.ChangeSearch), style = MaterialTheme.typography.body2) },

                            // in below line we are displaying
                            // our confirm button.
                            confirmButton = {
                                // below line we are adding on click
                                // listener for our confirm button.
                                TextButton(
                                    onClick = {
                                        showAlert = false
                                        searchTerm = ""

                                    }
                                ) {
                                    // in this line we are adding
                                    // text for our confirm button.
                                    Text("OK", style = MaterialTheme.typography.button.copy(color = teal_500))
                                }
                            },
                            // in below line we are displaying
                            // our dismiss button.
                            dismissButton = {
                                // in below line we are displaying
                                // our text button
                                TextButton(
                                    // adding on click listener for this button
                                    onClick = {
                                        showAlert = false

                                    }
                                ) {
                                    // adding text to our button.
                                    Text(stringResource(id = R.string.Back), style = MaterialTheme.typography.button.copy(color = red_500))
                                }
                            },
                            // below line is use to add background color to our alert dialog
                            backgroundColor = if(isSystemInDarkTheme()) Color.DarkGray else Color.White,

                            // below line is use to add content color for our alert dialog.
                            contentColor = if(isSystemInDarkTheme()) Color.White else Color.Black
                        )
                    }
                }
            }
        }

}



