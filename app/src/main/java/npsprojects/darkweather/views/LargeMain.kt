package npsprojects.darkweather.views

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.lazy.GridCells
import androidx.navigation.NavController
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.EllipsisV
import compose.icons.fontawesomeicons.solid.LocationArrow
import compose.icons.fontawesomeicons.solid.Map
import compose.icons.fontawesomeicons.solid.Search
import kotlinx.coroutines.launch
import npsprojects.darkweather.*
import npsprojects.darkweather.R
import npsprojects.darkweather.models.WeatherModel
import npsprojects.darkweather.models.WeatherViewModel
import java.time.Instant
import java.util.*


@OptIn(ExperimentalMaterial3Api::class,
    androidx.compose.foundation.ExperimentalFoundationApi::class
)
@Composable
fun LargeMain(model: WeatherViewModel, controller: NavController){
        val index: Int by model.index.observeAsState(initial = 0)
        val currentLocation by model.currentLocation.observeAsState(initial = listOf<WeatherModel>())
        val insets = LocalWindowInsets.current
        val bottomPadding = with(LocalDensity.current) { insets.systemGestures.bottom.toDp() }
        val isRefreshing by model.isLoading.observeAsState(initial = false)
        val scope = rememberCoroutineScope()
        val listState = rememberLazyListState()
        val context = LocalContext.current
        val locations by model.locations.observeAsState(listOf())
        var dropExtended by remember {
            mutableStateOf(false)
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {


            Scaffold(
                floatingActionButton = {
                    if (locations.isNotEmpty() && locations[index].isCurrent && WeatherViewModel.isOnline(
                            context = context
                        )
                    ) {
                        ExtendedFloatingActionButton(
                            text = { Text("Map", style = MaterialTheme.typography.labelMedium) },
                            onClick = {
                                controller.navigate("Map")
                            }, modifier = Modifier.navigationBarsPadding(),
                            icon = {
                                Icon(
                                    FontAwesomeIcons.Solid.Map,
                                    contentDescription = "map",
                                    modifier = Modifier.size(20.dp),
                                )
                            })
                    }
                },
                topBar = {

                    AnimatedVisibility(visible = locations.isNotEmpty() || currentLocation.isNotEmpty()) {
                        SmallTopAppBar(
                            title = {
                                Column(
                                    horizontalAlignment = Alignment.Start,
                                    verticalArrangement = Arrangement.Bottom
                                ) {

                                    Box() {
                                        if (!(locations.isNotEmpty() && locations.size > index) && currentLocation.isEmpty()) {
                                            Text(
                                                "N/A",
                                                style = MaterialTheme.typography.displayMedium.copy(
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            )
                                        } else {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.clickable(onClick = {
                                                    dropExtended = !dropExtended
                                                })
                                            ) {
                                                if (locations[index].isCurrent) {
                                                    Icon(
                                                        FontAwesomeIcons.Solid.LocationArrow,
                                                        modifier = Modifier.size(15.dp),
                                                        contentDescription = "",
                                                        tint = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                                Text(
                                                    locations[index].location.name,
                                                    style = MaterialTheme.typography.displayMedium.copy(
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                )
                                            }
                                        }
                                        DropdownMenu(
                                            expanded = dropExtended,
                                            onDismissRequest = { /*TODO*/ }) {


                                            locations.forEachIndexed { index, item ->
                                                DropdownMenuItem(
                                                    onClick = {
                                                        model.changeIndex(index)

                                                        dropExtended = false
                                                    },
                                                    modifier = Modifier.width(160.dp)
                                                ) {
                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(
                                                            5.dp
                                                        ),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        if (item.isCurrent) {
                                                            Icon(
                                                                FontAwesomeIcons.Solid.LocationArrow,
                                                                modifier = Modifier.size(15.dp),
                                                                contentDescription = "",
                                                            )
                                                        }
                                                        Text(
                                                            item.location.name,
                                                            style = MaterialTheme.typography.bodyMedium
                                                        )
                                                    }
                                                }
                                            }

                                        }

                                    }

                                    Text(
                                        if (!(locations.isNotEmpty() && locations.size > index)) "No data" else
                                            Date.from(Instant.ofEpochSecond(locations[index].data.current!!.dt!!))
                                                .ago(),
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(start = 5.dp)
                                    )
                                }
                            },
                            actions = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (locations.isNotEmpty() && locations.size > index) {

                                        if (!locations[index].isCurrent) {
                                            TextButton(onClick = {

                                                if (model.myLocations.firstOrNull {
                                                        it.latitude == locations[index].location.latitude
                                                                && it.longitude == locations[index].location.longitude
                                                    } != null) {
                                                    Log.i("Heart", "removing")
                                                    model.isLoading.value = true
                                                    val curIndex = index
                                                    model.changeIndex(0)
                                                    scope.launch {
                                                        model.remove(
                                                            locations[curIndex].location,
                                                            context = context
                                                        )
                                                    }
                                                    model.isLoading.value = false
                                                } else {
                                                    Log.i("Heart", "saving")
                                                    model.isLoading.value = true
                                                    scope.launch {
                                                        model.saveLocation(
                                                            locations[index].location,
                                                            context = context
                                                        )
                                                    }

                                                    model.isLoading.value = false
                                                }
                                            }) {

                                                ColoredIcon(
                                                    if (model.myLocations.firstOrNull {
                                                            it.latitude == locations[index].location.latitude
                                                                    && it.longitude == locations[index].location.longitude
                                                        } != null)
                                                        Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                    contentDescription = "",
                                                    modifier = Modifier.size(30.dp),
                                                    padding = 6.dp,
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }

                                        }

                                    }
                                    TextButton(onClick = {
                                        controller.navigate("Search")
                                    }) {
                                        Box() {

                                            ColoredIcon(
                                                imageVector = FontAwesomeIcons.Solid.Search,
                                                contentDescription = "",
                                                modifier = Modifier.size(30.dp),
                                                padding = 6.dp,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                    TextButton(onClick = {
                                        controller.navigate("Settings")
                                    }) {

                                        ColoredIcon(
                                            imageVector = FontAwesomeIcons.Solid.EllipsisV,
                                            contentDescription = "",
                                            modifier = Modifier.size(30.dp),
                                            padding = 6.dp,
                                            tint = MaterialTheme.colorScheme.primary
                                        )


                                    }
                                }

                            },
                            colors = TopAppBarDefaults.mediumTopAppBarColors(
                                titleContentColor = MaterialTheme.colorScheme.tertiary,
                                containerColor = Color.Transparent
                            ),
                            modifier = Modifier.statusBarsPadding()
                        )

                    }
                },


                containerColor = MaterialTheme.colorScheme.background

            ) {
                Box(contentAlignment = Alignment.TopEnd) {
                    AnimatedVisibility(visible = locations.isNotEmpty()) {
                        Image(
                            painter = painterResource(
                                id = if (locations.isNotEmpty())
                                    getWeatherBackIcon(locations[index].data.current!!.weather.first().icon!!) else R.drawable.clearday
                            ),
                            contentDescription = "",
                            modifier = Modifier.size(400.dp), contentScale = ContentScale.Fit
                        )
                    }

                    when (locations.size) {
                        0 ->
                            LoadingAnimationScreen(model = model)


                        else -> SwipeRefresh(
                            state = rememberSwipeRefreshState(isRefreshing),
                            onRefresh = {
                                scope.launch {
                                    model.initActions(context = context)
                                }
                            }
                        ) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(40.dp)
                            ) {
                                item {
                                    Spacer(modifier = Modifier.height(250.dp))
                                }
                                item{
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .background(color = MaterialTheme.colorScheme.background,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .padding(10.dp)
                                    ,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(40.dp)
                                ) {

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceEvenly,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(modifier = Modifier.fillMaxWidth(0.5f)) {
                                                SummaryCard(
                                                    current = locations[index].data.current!!,
                                                    dayDetails = locations[index].data.daily.first().weather.first().description!!
                                                )
                                            }
                                            Box(modifier = Modifier.fillMaxWidth(1f)) {
                                                DetailsCardCompact(
                                                    current = locations[index].data.current!!,
                                                    daily = locations[index].data.daily.first(),
                                                    inSi = model.units == WeatherUnits.SI
                                                )
                                            }
                                        }


                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceEvenly,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(modifier = Modifier.fillMaxWidth(0.5f)) {

                                                MoonView(
                                                    phase = locations[index].data.daily.first().moon_phase!!,
                                                    moonrise = locations[index].data.daily.first().moonrise!!,
                                                    moonset = locations[index].data.daily.first().moonset!!
                                                )
                                            }
                                            if (locations[index].airQuality != null) {
                                                Box(modifier = Modifier.fillMaxWidth(1f)) {

                                                    AirQuality(
                                                        aqi = locations[index].airQuality?.list?.first()?.main?.aqi
                                                            ?: 1
                                                    )
                                                }
                                            }

                                        }


                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceEvenly,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(modifier = Modifier.fillMaxWidth(0.5f)) {

                                                VerticalHourView(
                                                    hourly = locations[index].data.hourly,
                                                    inSi = model.units == WeatherUnits.SI
                                                )
                                            }
                                            Box(modifier = Modifier.fillMaxWidth(1f)) {

                                                WeeklyView(
                                                    days = locations[index].data.daily,
                                                    inSi = model.units == WeatherUnits.SI
                                                )
                                            }
                                        }


                                        Spacer(modifier = Modifier.height(50.dp))

                                }
                                }
                            }
                        }
                    }
                }

            }
        }

}