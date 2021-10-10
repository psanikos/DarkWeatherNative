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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import androidx.room.RoomSQLiteQuery
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.navigationBarsPadding
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
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalCoilApi
@Composable
fun NewMainView(model: WeatherViewModel, controller: NavController) {
    val index: Int by model.index.observeAsState(initial = 0)
    val insets = LocalWindowInsets.current
    val bottomPadding = with(LocalDensity.current) { insets.systemGestures.bottom.toDp() }
    val isRefreshing by model.loading.observeAsState(initial = false)
    val state = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val backOpacity =
        animateFloatAsState(targetValue = if (listState.firstVisibleItemIndex > 0) 1f else 0.5f)
    var color by remember {
        mutableStateOf(Color(0xFF2F4276))
    }
val error by model.error.observeAsState()
    LaunchedEffect(key1 = "$index ${model.locations.size}", block ={
        color = getWeatherColor(if (model.locations.size > 0) model.locations[index].data.current.weather[0].icon else "01d")
    } )
    val context = LocalContext.current
    LaunchedEffect(key1 =error, block = {
        if(error != WeatherError.NONE){
            Toast.makeText(context,"Please check your internet connection and location access",Toast.LENGTH_LONG).show()
        }
    })
    Scaffold(
        topBar = { TopBarView(model = model, controller = controller,color=color)},
        floatingActionButton = { FloatingActionButton(onClick = {
            controller.navigate("Map")
        },modifier = Modifier.navigationBarsPadding()) {
            Icon(FontAwesomeIcons.Solid.Map,contentDescription = "map",modifier = Modifier.size(20.dp),tint = if(isSystemInDarkTheme()) Color.Black else Color.White)
        }}
       // backgroundColor = color.copy(alpha = 0.7f)
    ) {


            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing),
                onRefresh = { model.initActions() }
            ) {


                LazyColumn(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(50.dp),
                    state = listState
                ) {

                    item {
                        MainCard(model = model, controller = controller)

                    }

                    if (!model.locations.isEmpty() && !model.locations[index].data.alerts.isNullOrEmpty()) {
                        model.locations[index].data.alerts?.let {
                            if (it.isNotEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 15.dp)
                                            .fillMaxWidth()
                                            .wrapContentHeight()
                                            .background(
                                                color = Color.Yellow.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(12)
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
                                                style = MaterialTheme.typography.h3.copy(
                                                    fontSize = 14.sp
                                                )
                                            )
                                            Text(
                                                it[0].description ?: "",
                                                style = MaterialTheme.typography.body2,
                                                maxLines = 3
                                            )
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(
                                                    10.dp
                                                ),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    Icons.Default.Timer,
                                                    contentDescription = ""
                                                )
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

                    if (!model.locations.isEmpty() && !model.locations[index].data.hourly.isNullOrEmpty() && (model.locations[index].data.hourly.firstOrNull { it.pop!! > 0.49 } != null)) {
                        item {
                            RainAlert(model = model)
                        }
                    }
                    item {
                        AirQualityView(model = model)
                    }

                    item {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 15.dp)
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp)),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(45.dp)
                                    .background(if (isSystemInDarkTheme()) iceBlack else frosted)
                                    .padding(horizontal = 10.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        stringResource(id = R.string.today),
                                        style = MaterialTheme.typography.h4.copy(
                                            fontWeight = FontWeight.ExtraBold,

                                        )
                                    )
                                    DayDetailsView(model = model)
                                }
                            }
                            HourlyView(model = model)
                        }
                    }

                    item {
                        UVView(model = model)
                    }

                    item {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 15.dp)
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp)),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(45.dp)
                                    .background(if (isSystemInDarkTheme()) iceBlack else frosted)
                                    .padding(horizontal = 10.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    stringResource(id = R.string.weekly),
                                    style = MaterialTheme.typography.h4.copy(
                                        fontWeight = FontWeight.ExtraBold,

                                    )
                                )
                            }

                            WeekView(model = model)
                        }
                    }
item{ Spacer(modifier = Modifier.height(40.dp))}

                }




        }
    }
}


