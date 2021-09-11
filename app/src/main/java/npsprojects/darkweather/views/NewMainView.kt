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
import androidx.compose.foundation.lazy.LazyColumn
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
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileOverlay
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
import java.util.*


@ExperimentalCoilApi
@Composable
fun NewMainView(model: WeatherViewModel, controller: NavController) {
    val index:Int by model.index.observeAsState(initial = 0)
    val insets = LocalWindowInsets.current
    val bottomPadding = with(LocalDensity.current){insets.systemGestures.bottom.toDp()}
    val isRefreshing by model.loading.observeAsState(initial = false)
    Scaffold(
        topBar = {
             TopBarView(model = model, controller = controller)

        }) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = { model.initActions() },
        ) {
            LazyColumn(
                Modifier
                    .padding(horizontal = 0.dp)
                    .padding(bottom = bottomPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(30.dp)
            ) {
                item {
                    MainCard(model = model)
                }

                item {
                    HourlyView(model = model)
                }
//                item {
//                    Column(
//                        modifier = Modifier
//                            .padding(20.dp)
//                            .fillMaxWidth()
//                            .wrapContentHeight()
//                            ,
//                        horizontalAlignment = Alignment.Start,
//                        verticalArrangement = Arrangement.spacedBy(15.dp)
//                    ) {
//                        Text("Weather map", style = MaterialTheme.typography.h4)
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(220.dp)
//                                .background(color = Color.DarkGray, shape = RoundedCornerShape(8))
//                                .padding(8.dp)
//                                .clip(RoundedCornerShape(6))
//                        ) {
//                            CustomMapView(model = model)
//                        }
//                    }
//                }
              item{
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
            }
        }
    }
}
