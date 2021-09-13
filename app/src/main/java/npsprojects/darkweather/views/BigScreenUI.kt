package npsprojects.darkweather.views

import android.location.Address
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.insets.LocalWindowInsets
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileOverlay
import kotlinx.coroutines.launch
import npsprojects.darkweather.models.SavedLocation
import npsprojects.darkweather.models.WeatherViewModel
import npsprojects.darkweather.ui.theme.*
import java.text.SimpleDateFormat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay
import npsprojects.darkweather.*
import npsprojects.darkweather.R
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.ArrayList

@ExperimentalCoilApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun NewMapViewBig(model: WeatherViewModel, controller: NavController) {
    val map = rememberMapViewWithLifecycle()
    var mapType by rememberSaveable { mutableStateOf("none") }
    val index: Int by model.index.observeAsState(initial = 0)
    val testCoordinates = LatLng(37.98384, 23.72753)
    var dropExtended by remember {
        mutableStateOf(false)
    }
    var coordinates by rememberSaveable {
        mutableStateOf(
            testCoordinates
        )
    }
    var zoom by rememberSaveable(map) { mutableStateOf(InitialZoom) }
    val overlays: MutableList<TileOverlay> by rememberSaveable { mutableStateOf(ArrayList<TileOverlay>()) }
    LaunchedEffect(key1 = index + model.locations.size, block = {
        if (model.locations.isNotEmpty()) {
            coordinates = LatLng(
                model.locations[model.index.value!!].data.lat,
                model.locations[model.index.value!!].data.lon
            )
        }

    })

    val bottomPadding = with(LocalDensity.current) {
        LocalWindowInsets.current.systemGestures.layoutInsets.bottom
    }
    val isRefreshing by model.loading.observeAsState(initial = false)
    val state = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(

        topBar = {
            CompactTopBarView(model = model, controller = controller,onAdd = {

            })
        },

    ) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.fillMaxHeight()
                .fillMaxWidth(0.5f)
        ) {
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing),
                onRefresh = { model.initActions() },
            ) {
                LazyColumn(
                    Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(30.dp)
                ) {
                    item {
                        MainCard(model = model)
                    }
                    if (!model.locations.isEmpty() && !model.locations[index].data.alerts.isNullOrEmpty()) {
                        model.locations[index].data.alerts?.let {
                            if (it.isNotEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .padding(10.dp)
                                            .fillMaxWidth()
                                            .wrapContentHeight()
                                            .background(
                                                color = Color.Yellow.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(6)
                                            )
                                            .padding(10.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                Icons.Filled.Warning,
                                                contentDescription = "",
                                                tint = Color.Red,
                                                modifier = Modifier.size(40.dp)
                                            )
                                            Text(
                                                it[0].event ?: "Alert",
                                                style = MaterialTheme.typography.h3.copy(fontSize = 14.sp)
                                            )
                                            Text(
                                                it[0].description ?: "",
                                                style = MaterialTheme.typography.body2,
                                                maxLines = 3
                                            )
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Default.Timer, contentDescription = "")
                                                Text(
                                                    "From ${
                                                        DateTimeFormatter.ofPattern("EEEE dd")
                                                            .format(
                                                                LocalDateTime.ofInstant(
                                                                    Instant.ofEpochMilli((1000 * it[0].start).toLong()),
                                                                    ZoneId.systemDefault()
                                                                )
                                                            )
                                                    }" + if (it[0].end != null) " until ${
                                                        DateTimeFormatter.ofPattern("EEEE dd")
                                                            .format(
                                                                LocalDateTime.ofInstant(
                                                                    Instant.ofEpochMilli((1000 * it[0].end!!).toLong()),
                                                                    ZoneId.systemDefault()
                                                                )
                                                            )
                                                    }" else "",
                                                    style = MaterialTheme.typography.body2
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    item {
                        HourlyView(model = model)
                    }

                    item {
                        UVView(model = model)
                    }


                    item {
                        WeekView(model = model)
                    }

                    item {
                        AirQualityView(model = model)
                    }
                    item {
                        DayDetailsView(model = model)
                    }
                    item {
                        Spacer(modifier = Modifier.height(60.dp))
                    }
                }
            }
        }

        Box(
            modifier = Modifier.padding(bottom = (0.5*bottomPadding).dp).fillMaxHeight()
                .fillMaxWidth().clip(RoundedCornerShape(6)), contentAlignment = Alignment.BottomEnd
        ) {


            MapViewContainer(
                map = map,
                latitude = coordinates.latitude,
                longitude = coordinates.longitude,
                mapType = mapType,
                model = model
            )
            Column(

                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = bottomPadding.dp)
            ) {
                Button(
                    onClick = {

                        mapType = "clouds_new"

                    },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.DarkGray,
                        backgroundColor = if (mapType == "clouds_new") Color(0xFFFFFDD0) else Color.White
                    ),
                    contentPadding = PaddingValues(10.dp),
                    shape = CircleShape,
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp),

                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 4.dp,
                        disabledElevation = 2.dp
                    )
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Filled.Cloud,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )

                    }
                }

                Button(
                    onClick = {
                        mapType = "precipitation_new"
                    },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.DarkGray,
                        backgroundColor = if (mapType == "precipitation_new") Color(0xFFFFFDD0) else Color.White
                    ),
                    contentPadding = PaddingValues(10.dp),
                    shape = CircleShape,
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 4.dp,
                        disabledElevation = 2.dp
                    ),

                    ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Filled.Opacity,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                }
                Button(
                    onClick = {
                        mapType = "temp_new"


                    },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.DarkGray,
                        backgroundColor = if (mapType == "temp_new") Color(0xFFFFFDD0) else Color.White
                    ),
                    contentPadding = PaddingValues(10.dp),
                    shape = CircleShape,
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 4.dp,
                        disabledElevation = 2.dp
                    )
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Filled.Thermostat,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )

                    }
                }
                Button(
                    onClick = {
                        mapType = "wind_new"
                    },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.DarkGray,
                        backgroundColor = if (mapType == "wind_new") Color(0xFFFFFDD0) else Color.White
                    ),
                    contentPadding = PaddingValues(10.dp),
                    shape = CircleShape,
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 4.dp,
                        disabledElevation = 2.dp
                    )
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Filled.Air,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

    }

}

}


