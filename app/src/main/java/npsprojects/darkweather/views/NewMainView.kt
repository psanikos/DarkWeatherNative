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
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
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
    Scaffold(
        topBar = {
             TopBarView(model = model, controller = controller)

        }) {

    LazyColumn(
        Modifier
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ){
    item {
    MainCard(model = model)
    }

        item{
            HourlyView(model = model)
        }
        item{
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(color = Color.DarkGray, shape = RoundedCornerShape(12))
                    .padding(8.dp)
                    .clip(RoundedCornerShape(10))
            ) {
                CustomMapView(model = model)
            }
        }
        item{
            WeekView(model = model)
        }
    }
    }
}
