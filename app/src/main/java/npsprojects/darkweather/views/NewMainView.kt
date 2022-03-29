package npsprojects.darkweather.views

import android.graphics.drawable.Icon
import android.graphics.drawable.ShapeDrawable
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
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
    var isNight by remember {
        mutableStateOf(false)
    }

    fun isNight(): Boolean {
        if (locations.isNotEmpty()) {

            val sunrise = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(1000 * locations[index].data.daily[1].sunrise!!),
                ZoneId.systemDefault()
            )
            Log.i("Sunrise", sunrise.toString())

            val currentTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault())
            Log.i("Current", currentTime.toString())

            return currentTime.isBefore(sunrise)
        } else {
            return false
        }
    }

    LaunchedEffect(key1 = "${locations.size}", block = {
//        isNight = isNight()
//        Log.i("IsNight", isNight.toString())
    })
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val deviceType = when(with(LocalDensity.current){
        configuration.screenWidthDp > 480
    }){
        true -> DeviceType.BIGSCREEN
        false -> DeviceType.PHONE
    }
    when (deviceType) {
                DeviceType.PHONE -> SmallMain(model = model, controller = controller)
                    DeviceType.BIGSCREEN -> LargeMain(model = model, controller = controller)
                }


}