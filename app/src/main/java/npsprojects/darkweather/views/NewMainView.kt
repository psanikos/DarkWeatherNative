package npsprojects.darkweather.views

import android.graphics.drawable.Icon
import android.graphics.drawable.ShapeDrawable
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
import androidx.compose.ui.platform.LocalDensity
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
    val index:Int by model.index.observeAsState(initial = 0)
    val insets = LocalWindowInsets.current
    val bottomPadding = with(LocalDensity.current){insets.systemGestures.bottom.toDp()}
    val isRefreshing by model.loading.observeAsState(initial = false)
    val state = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(


        ) {



            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing),
                onRefresh = { model.initActions() },
            ) {
                val color =   getWeatherColor(if (model.locations.size > 0) model.locations[index].data.current.weather[0].icon else "01d")
                Box(
                    modifier = Modifier
                        .navigationBarsPadding(bottom = true)
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    color,
                                    color,
                                    MaterialTheme.colors.background,
                                    MaterialTheme.colors.background,
                                    MaterialTheme.colors.background
                                )
                            )
                        ),
                    contentAlignment = Alignment.TopCenter
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .padding(horizontal = 25.dp)
                            .padding(top = 90.dp)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(50.dp)
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
                            AirQualityView(model = model)
                        }
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(20.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(stringResource(id = R.string.today), style = MaterialTheme.typography.h4)
                                    DayDetailsView(model = model)
                                }
                                Divider(modifier = Modifier.fillMaxWidth())
                              HourlyView(model = model)
                            }
                        }
                        item{
                            CustomMapView(model = model, controller = controller)
                        }
                        item {
                            UVView(model = model)
                        }
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(20.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(stringResource(id = R.string.weekly), style = MaterialTheme.typography.h4)
                                }
                                Divider(modifier = Modifier.fillMaxWidth())
                                WeekView(model = model)
                            }
                        }


                        

                    }
                    TopBarView(model = model, controller = controller)
                }
       
            }
        }
    
}

