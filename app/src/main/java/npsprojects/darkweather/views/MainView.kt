package npsprojects.darkweather.views

import android.location.Address
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController

import com.google.accompanist.insets.LocalWindowInsets
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileOverlay
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import npsprojects.darkweather.*
import npsprojects.darkweather.R
import npsprojects.darkweather.models.SavedLocation
import npsprojects.darkweather.models.WeatherViewModel

import npsprojects.darkweather.ui.theme.*
import npsprojects.darkweather.views.InitialZoom
import npsprojects.darkweather.views.NewMapViewBig
import npsprojects.darkweather.views.rememberMapViewWithLifecycle
import java.io.IOException
import java.text.SimpleDateFormat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.annotation.ExperimentalCoilApi

sealed class Screen(val route: String, @StringRes val resourceId: Int, val icon: ImageVector) {
    object Main : Screen("main", R.string.home,icon = Icons.Outlined.Cloud)
    object AddLocation : Screen("add", R.string.add_location,icon = Icons.Outlined.AddCircleOutline)
    object ManageLocation : Screen("manage", R.string.manage_location,icon = Icons.Outlined.Summarize)
    object Settings : Screen("settings", R.string.settings,icon = Icons.Outlined.Settings)
}

@ExperimentalCoilApi
@ExperimentalAnimationApi
@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterialApi
@Composable
fun MainPageView(model: WeatherViewModel, controller: NavController) {
    val items = listOf(Screen.Main,Screen.ManageLocation,Screen.Settings)
    val scope = rememberCoroutineScope()

    val state = rememberScaffoldState(
        rememberDrawerState(DrawerValue.Closed)
    )
    var currentPage by remember { mutableStateOf("Main") }

    val configuration = LocalConfiguration.current
    val deviceType = when(with(LocalDensity.current){
        configuration.screenWidthDp > 420
    }){
        true -> DeviceType.BIGSCREEN
        false -> DeviceType.PHONE
    }
    val isLoading by model.loading.observeAsState(true)
    Scaffold(
    ) {

                when (deviceType) {
                    DeviceType.PHONE -> NewMainView(model = model, controller = controller)
                    DeviceType.BIGSCREEN -> NewMapViewBig(model = model, controller = controller)
                }


    }
}
