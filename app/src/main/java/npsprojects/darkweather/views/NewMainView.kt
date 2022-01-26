package npsprojects.darkweather.views

import android.graphics.drawable.Icon
import android.graphics.drawable.ShapeDrawable
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.internal.enableLiveLiterals
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileOverlay
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.EllipsisV
import compose.icons.fontawesomeicons.solid.LocationArrow
import compose.icons.fontawesomeicons.solid.Map
import compose.icons.fontawesomeicons.solid.Search
import kotlinx.coroutines.launch
import npsprojects.darkweather.*
import npsprojects.darkweather.R
import npsprojects.darkweather.models.Current
import npsprojects.darkweather.models.WeatherModel
import npsprojects.darkweather.models.Weather
import npsprojects.darkweather.models.WeatherViewModel
import npsprojects.darkweather.services.SavedLocation
import npsprojects.darkweather.ui.theme.*
import java.lang.reflect.Array.get
import java.lang.reflect.Array.set
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@ExperimentalAnimationApi


@Composable
fun NewMainView(model: WeatherViewModel, controller: NavController) {
    val index: Int by model.index.observeAsState(initial = 0)
    val currentLocation by model.currentLocation.observeAsState(initial = listOf<WeatherModel>())
    val insets = LocalWindowInsets.current
    val bottomPadding = with(LocalDensity.current) { insets.systemGestures.bottom.toDp() }
    val isRefreshing by model.isLoading.observeAsState(initial = false)
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val locations by model.locations.observeAsState(listOf())

    var dropExtended by remember {
        mutableStateOf(false)
    }
    var showCurrent by remember {
        mutableStateOf(false)
    }

    var selectedLocation: WeatherModel? by remember {
        mutableStateOf(null)
    }

    LaunchedEffect(key1 = "${model.currentLocation.value?.size}", block = {

    })
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {


        Scaffold(
            floatingActionButton = {
                if (locations.isNotEmpty() && locations[index].isCurrent && WeatherViewModel.LocationFetcher.isOnline(context = context)) {
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
                                        Text("N/A", style = MaterialTheme.typography.displayMedium)
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
                                                )
                                            }
                                            Text(
                                                locations[index].location.name,
                                                style = MaterialTheme.typography.displayMedium
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
                                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
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
                            //containerColor = Color.Transparent
                        ),
                        modifier = Modifier.statusBarsPadding()
                    )

                }
            },


            containerColor = MaterialTheme.colorScheme.surface

        ) {
            Box(contentAlignment = Alignment.TopCenter) {
                AnimatedVisibility(visible = locations.isNotEmpty()) {
                    Image(
                        painter = painterResource(
                            id = if (locations.isNotEmpty())
                                getWeatherBackIcon(locations[index].data.current!!.weather.first().icon!!) else R.drawable.clearday
                        ),
                        contentDescription = "",
                        modifier = Modifier.size(500.dp), contentScale = ContentScale.Fit
                    )
                }
                SwipeRefresh(
                    state = rememberSwipeRefreshState(isRefreshing),
                    onRefresh = {
                        scope.launch {
                            model.initActions(context = context)
                        }
                    }
                ) {
                    when (locations.size) {
                        0 -> LoadingAnimationScreen()

                        else -> Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(state = ScrollState(0)),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(40.dp)
                        ) {
                            Spacer(modifier = Modifier.height(300.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .materialYouFrosted(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    modifier = Modifier.padding(15.dp),
                                    horizontalAlignment = Alignment.Start,
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    SummaryCard(
                                        current = locations[index].data.current!!,
                                        dayDetails = locations[index].data.daily.first().weather.first().description!!
                                    )
                                    HourView(
                                        hourly = locations[index].data.hourly,
                                        inSi = model.units == WeatherUnits.SI
                                    )
                                    DetailsCard(
                                        current = locations[index].data.current!!,
                                        daily = locations[index].data.daily.first(),
                                        inSi = model.units == WeatherUnits.SI
                                    )
                                    if (locations[index].airQuality != null) {
                                        AirQuality(
                                            aqi = locations[index].airQuality?.list?.first()?.main?.aqi
                                                ?: 1
                                        )
                                    }
                                    MoonView(
                                        phase = locations[index].data.daily.first().moon_phase!!,
                                        moonrise = locations[index].data.daily.first().moonrise!!,
                                        moonset = locations[index].data.daily.first().moonset!!
                                    )
                                    WeeklyView(
                                        days = locations[index].data.daily,
                                        inSi = model.units == WeatherUnits.SI
                                    )
                                    Spacer(modifier = Modifier.height(40.dp))
                                }
                            }

                        }

                    }
                }

            }
        }
    }
}

