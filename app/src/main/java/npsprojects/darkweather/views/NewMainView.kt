package npsprojects.darkweather.views

import android.graphics.drawable.Icon
import android.graphics.drawable.ShapeDrawable
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TopAppBar

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.rememberBottomSheetScaffoldState
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
import compose.icons.fontawesomeicons.solid.Map
import kotlinx.coroutines.launch
import npsprojects.darkweather.*
import npsprojects.darkweather.R
import npsprojects.darkweather.models.Current
import npsprojects.darkweather.models.WeatherModel
import npsprojects.darkweather.models.Weather
import npsprojects.darkweather.models.WeatherViewModel
import npsprojects.darkweather.ui.theme.*
import java.lang.reflect.Array.get
import java.lang.reflect.Array.set
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


@OptIn(ExperimentalMaterialApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
@ExperimentalAnimationApi


@Composable
fun NewMainView(model: WeatherViewModel, controller: NavController) {
    val index: Int by model.index.observeAsState(initial = 0)
    val locations by model.locations.observeAsState(initial = listOf<WeatherModel>())
    val insets = LocalWindowInsets.current
    val bottomPadding = with(LocalDensity.current) { insets.systemGestures.bottom.toDp() }
    val isRefreshing by model.loading.observeAsState(initial = false)
    val state = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val backOpacity =
        animateFloatAsState(targetValue = if (listState.firstVisibleItemIndex > 0) 1f else 0.5f)
    var backImage by remember {
        mutableStateOf(R.drawable.nightphone)
    }
    val error by model.error.observeAsState()
    LaunchedEffect(key1 = "$index ${locations.size}", block = {
        backImage =
            getWeatherBack((if (locations.isNotEmpty()) locations[index].data.current!!.weather[0].icon else "01d")!!)
    })
    val context = LocalContext.current
    LaunchedEffect(key1 = error, block = {
        if (error != WeatherError.NONE) {
            Toast.makeText(
                context,
                "Please check your internet connection and location access",
                Toast.LENGTH_LONG
            ).show()
        }
    })
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {


        Scaffold(
            floatingActionButton = {
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
            },
            topBar = {

                AnimatedVisibility(visible = locations.isNotEmpty()) {
                    TopBarView(
                        model = model,
                        controller = controller,
                        color = Color.Transparent
                    )
                }

            },


            containerColor = MaterialTheme.colorScheme.surface

            ) {
            Box(contentAlignment = Alignment.TopCenter) {
            AnimatedVisibility(visible = locations.isNotEmpty()) {
                Image(
                    painter = painterResource(id = if(locations.isNotEmpty())
                    getWeatherBackIcon(locations[index].data.current!!.weather.first().icon!!) else R.drawable.clearday),
                    contentDescription = "",
                    modifier = Modifier.size(500.dp)

                    , contentScale = ContentScale.Fit
                )
            }
                SwipeRefresh(
                    state = rememberSwipeRefreshState(isRefreshing),
                    onRefresh = { model.initActions() }
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

