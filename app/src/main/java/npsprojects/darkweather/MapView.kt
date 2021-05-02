package npsprojects.darkweather

import android.os.Bundle
import androidx.annotation.FloatRange
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.launch
import npsprojects.darkweather.MyApp.context
import java.net.MalformedURLException
import java.net.URL
import kotlin.math.absoluteValue


private val weatherKey = "e1e45feaea76d66517c25291f2633d9a"


@Preview
@Composable
fun MapPreview(){
    MapScreen(latitude = "37.9838", longitude = "23.7275")
}


@Composable
fun MapScreen(latitude: String, longitude: String) {
    // The MapView lifecycle is handled by this composable. As the MapView also needs to be updated
    // with input from Compose UI, those updates are encapsulated into the MapViewContainer
    // composable. In this way, when an update to the MapView happens, this composable won't
    // recompose and the MapView won't need to be recreated.
    val mapView = rememberMapViewWithLifecycle()

    var mapType by remember { mutableStateOf("clouds_new")}
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(400.dp)
        .clip(RoundedCornerShape(8.dp)),contentAlignment = Alignment.BottomCenter) {


        MapViewContainer(mapView, latitude, longitude, mapType = mapType)

Row(
    modifier = Modifier
        .padding(12.dp)
        .fillMaxWidth(),horizontalArrangement = Arrangement.SpaceEvenly,verticalAlignment = Alignment.CenterVertically){
    Button(onClick = {

        mapType = "clouds_new"

    },colors = ButtonDefaults.buttonColors(
        contentColor = Color.White,
        backgroundColor = Color.DarkGray.copy(alpha = 0.8f)
    ),contentPadding = PaddingValues(12.dp),shape = RoundedCornerShape(50),modifier = Modifier
        .width(40.dp)
        .height(40.dp),

        elevation = ButtonDefaults.elevation(defaultElevation = 0.dp,pressedElevation = 0.dp,disabledElevation = 0.dp)
        ) {
        Icon(Icons.Filled.Cloud,contentDescription = null)
    }
    Button(onClick = {
        mapType = "precipitation_new"
    },colors = ButtonDefaults.buttonColors(
        contentColor = Color.White,
        backgroundColor = Color.DarkGray.copy(alpha = 0.8f)
    ),contentPadding = PaddingValues(12.dp),shape = RoundedCornerShape(50),modifier = Modifier
        .width(40.dp)
        .height(40.dp),
        elevation = ButtonDefaults.elevation(defaultElevation = 0.dp,pressedElevation = 0.dp,disabledElevation = 0.dp)
    ) {
        Icon(Icons.Filled.Opacity,contentDescription = null)
    }
    Button(onClick = {
        mapType = "temp_new"
    },colors = ButtonDefaults.buttonColors(
        contentColor = Color.White,
        backgroundColor = Color.DarkGray.copy(alpha = 0.8f)
    ),contentPadding = PaddingValues(12.dp),shape = RoundedCornerShape(50),modifier = Modifier
        .width(40.dp)
        .height(40.dp),
        elevation = ButtonDefaults.elevation(defaultElevation = 0.dp,pressedElevation = 0.dp,disabledElevation = 0.dp)
    ) {
        Icon(Icons.Filled.Thermostat,contentDescription = null)
    }
    Button(onClick = {
        mapType = "wind_new"
    },colors = ButtonDefaults.buttonColors(
        contentColor = Color.White,
        backgroundColor = Color.DarkGray.copy(alpha = 0.8f)
    ),contentPadding = PaddingValues(12.dp),shape = RoundedCornerShape(50),modifier = Modifier
        .width(40.dp)
        .height(40.dp),
        elevation = ButtonDefaults.elevation(defaultElevation = 0.dp,pressedElevation = 0.dp,disabledElevation = 0.dp)
    ) {
        Icon(Icons.Filled.Air,contentDescription = null)
    }
//    Button(onClick = {
//        mapType = "pressure_new"
//    },colors = ButtonDefaults.buttonColors(
//        contentColor = Color.White,
//        backgroundColor = Color.DarkGray.copy(alpha = 0.8f)
//    ),shape = RoundedCornerShape(25),modifier = Modifier
//        .width(60.dp)
//        .height(40.dp),
//        elevation = ButtonDefaults.elevation(defaultElevation = 0.dp,pressedElevation = 0.dp,disabledElevation = 0.dp)
//    ) {
//        Text("Pressure",style = MaterialTheme.typography.button.copy(fontSize = 10.sp))
//    }
}
    }
}
private const val InitialZoom = 5f
const val MinZoom = 2f
const val MaxZoom = 8f


@Composable
private fun MapViewContainer(
    map: MapView,
    latitude: String,
    longitude: String,
    mapType:String,

) {

    var zoom by rememberSaveable(map) { mutableStateOf(InitialZoom) }

    val coroutineScope = rememberCoroutineScope()
var hasOverlay by remember { mutableStateOf(false)}

    var overlays:MutableList<TileOverlay>  by remember { mutableStateOf(  ArrayList<TileOverlay>())}
Box(contentAlignment = Alignment.TopEnd,modifier = Modifier.fillMaxSize()) {
    AndroidView({ map }) { mapView ->

        val mapZoom = zoom
        mapView.getMapAsync {
            val position = LatLng(latitude.toDouble(), longitude.toDouble())
            var tileProvider: TileProvider = object : UrlTileProvider(256, 256) {
                override fun getTileUrl(x: Int, y: Int, zoom: Int): URL? {

                    /* Define the URL pattern for the tile images */

                    val url =
                        "https://tile.openweathermap.org/map/$mapType/$zoom/$x/$y.png?appid=$weatherKey"
                    return if (!checkTileExists(x, y, zoom)) {

                        null
                    } else try {
                        URL(url)
                    } catch (e: MalformedURLException) {
                        throw AssertionError(e)
                    }
                }

                private fun checkTileExists(x: Int, y: Int, zoom: Int): Boolean {
                    val minZoom = MinZoom.toInt()
                    val maxZoom = MaxZoom.toInt()
                    return zoom in minZoom..maxZoom
                }
            }
            it.setZoom(mapZoom)
//            it.addMarker(
//                MarkerOptions().position(position)
//            )
            it.animateCamera(CameraUpdateFactory.newLatLng(position))

            val tile = it.addTileOverlay(
                TileOverlayOptions()
                    .tileProvider(tileProvider).fadeIn(true)
            )

            tile?.let { overlay ->
                overlays.add(overlay)
            }
            overlays.forEachIndexed { index, item ->
                if (index == overlays.size - 1) {

                } else {
                    item.remove()
                }
            }


        }

    }
    Row(
        Modifier
            .padding(5.dp)
            .fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        ZoomButton("-", onClick = { if (zoom > MinZoom){
            zoom -= 0.1f
        } })
        ZoomButton("+", onClick = { if (zoom < MaxZoom){
            zoom += 0.1f
        } })
    }
}
}


@Composable
private fun ZoomButton(text: String, onClick: () -> Unit) {
    Button(
        modifier = Modifier.width(60.dp).height(60.dp).padding(6.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.LightGray,
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(20)
        ,
        elevation = ButtonDefaults.elevation(disabledElevation = 0.dp,pressedElevation = 0.dp,defaultElevation = 0.dp),
        onClick = onClick
    ) {
        Text(text = text, style = MaterialTheme.typography.h3)
    }
}



@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            id = R.id.map
        }
    }

    // Makes MapView follow the lifecycle of this composable
    val lifecycleObserver = rememberMapLifecycleObserver(mapView)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }

    return mapView
}

@Composable
private fun rememberMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
    remember(mapView) {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> throw IllegalStateException()
            }
        }
    }


fun GoogleMap.setZoom(
    @FloatRange(from = MinZoom.toDouble(), to = MaxZoom.toDouble()) zoom: Float
) {
    resetMinMaxZoomPreference()
    setMinZoomPreference(zoom)
    setMaxZoomPreference(zoom)
}