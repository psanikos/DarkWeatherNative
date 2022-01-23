package npsprojects.darkweather.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.insets.LocalWindowInsets
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileOverlay
import npsprojects.darkweather.models.WeatherModel
import npsprojects.darkweather.models.WeatherViewModel
import java.util.ArrayList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullMapView(model: WeatherViewModel,controller:NavController) {
    val map = rememberMapViewWithLifecycle()
    var mapType by rememberSaveable { mutableStateOf("none") }
    val index: Int by model.index.observeAsState(initial = 0)
    val locations by model.locations.observeAsState(initial = listOf<WeatherModel>())

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
    LaunchedEffect(key1 = index + locations.size, block = {
        if (locations.isNotEmpty() && locations.size > index) {
            coordinates = LatLng(
                locations[index].data.lat,
                locations[index].data.lon
            )
        }

    })
    val bottomPadding = with(LocalDensity.current){
        LocalWindowInsets.current.systemGestures.bottom + 10
    }

  Scaffold(
      topBar = {
          MapTopBarView(model,controller)

      },
  ) {
      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {


          MapViewContainer(
              map = map,
              latitude = coordinates.latitude,
              longitude = coordinates.longitude,
              mapType = mapType,
              model = model
          )
          Column(

              verticalArrangement = Arrangement.spacedBy(10.dp),
              modifier = Modifier.padding(horizontal = 10.dp,vertical = bottomPadding.dp)
          ) {
              Button(
                  onClick = {

                      mapType = "clouds_new"

                  },
                  colors = ButtonDefaults.buttonColors(
                      contentColor = Color.DarkGray, containerColor  = if (mapType == "clouds_new") Color(0xFFFFFDD0) else Color.White
                  ),
                  contentPadding = PaddingValues(10.dp),
                  shape = CircleShape,
                  modifier = Modifier
                      .width(50.dp)
                      .height(50.dp),

              ) {
          Box(modifier = Modifier.fillMaxSize(),contentAlignment = Alignment.Center) {
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
                      containerColor = if (mapType == "precipitation_new") Color(0xFFFFFDD0) else Color.White
                  ),
                  contentPadding = PaddingValues(10.dp),
                  shape = CircleShape,
                  modifier = Modifier
                      .width(50.dp)
                      .height(50.dp)

                 ,

                  ) {
                  Box(modifier = Modifier.fillMaxSize(),contentAlignment = Alignment.Center) {
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
                      containerColor = if (mapType == "temp_new") Color(0xFFFFFDD0) else Color.White
                  ),
                  contentPadding = PaddingValues(10.dp),
                  shape = CircleShape,
                  modifier = Modifier
                      .width(50.dp)
                      .height(50.dp),

              ) {
                  Box(modifier = Modifier.fillMaxSize(),contentAlignment = Alignment.Center) {
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
                      containerColor = if (mapType == "wind_new") Color(0xFFFFFDD0) else Color.White
                  ),
                  contentPadding = PaddingValues(10.dp),
                  shape = CircleShape,
                  modifier = Modifier
                      .width(50.dp)
                      .height(50.dp),

              ) {
                  Box(modifier = Modifier.fillMaxSize(),contentAlignment = Alignment.Center) {
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

