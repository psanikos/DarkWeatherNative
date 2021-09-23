package npsprojects.darkweather.views

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.FloatRange
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import npsprojects.darkweather.MyApp.context
import java.net.MalformedURLException
import java.net.URL
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.scale
import androidx.navigation.NavController
import npsprojects.darkweather.R
import npsprojects.darkweather.getWeatherIcon
import npsprojects.darkweather.models.WeatherViewModel
import npsprojects.darkweather.ui.theme.indigo_500
import npsprojects.darkweather.ui.theme.pink_500


private val weatherKey = "e1e45feaea76d66517c25291f2633d9a"

//temp_new
@Composable
fun CustomMapView(model: WeatherViewModel,controller:NavController) {
    val map = rememberMapViewWithLifecycle()
    var mapType by rememberSaveable { mutableStateOf("none") }
    val index: Int by model.index.observeAsState(initial = 0)
    val testCoordinates = LatLng(37.98384, 23.72753)

    var coordinates by rememberSaveable {
        mutableStateOf(
            testCoordinates
        )
    }
    var zoom by rememberSaveable(map) { mutableStateOf(InitialZoom) }
    val overlays: MutableList<TileOverlay> by rememberSaveable { mutableStateOf(java.util.ArrayList<TileOverlay>()) }
    LaunchedEffect(key1 = index + model.locations.size, block = {
        if (model.locations.isNotEmpty()) {
            coordinates = LatLng(
                model.locations[model.index.value!!].data.lat,
                model.locations[model.index.value!!].data.lon
            )
        }

    })
    Column(
        modifier = Modifier

            .fillMaxWidth()
            .wrapContentHeight()
        ,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(id = R.string.map), style =  MaterialTheme.typography.h4)
            Button(
                onClick = {
                    controller.navigate("Map")
                },
                elevation = ButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    disabledElevation = 0.dp,
                    pressedElevation = 0.dp
                ), colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Transparent,
                    disabledBackgroundColor = Color.Transparent,
                    disabledContentColor = Color.Transparent
                )
            ) {
                Icon(
                    Icons.Default.AspectRatio, contentDescription = "",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(color = MaterialTheme.colors.secondary, shape = RoundedCornerShape(14.dp))
                .padding(4.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {


                MapViewContainer(
                    map = map,
                    latitude = coordinates.latitude,
                    longitude = coordinates.longitude,
                    mapType = mapType,
                    model = model
                )
//        Column(
//
//            verticalArrangement = Arrangement.spacedBy(6.dp),
//            modifier = Modifier.padding(4.dp)
//        ) {
//            Button(
//                onClick = {
//
//                    mapType = "clouds_new"
//
//                },
//                colors = ButtonDefaults.buttonColors(
//                    contentColor = Color.DarkGray,
//                    backgroundColor = if (mapType == "clouds_new") Color(0xFFFFFDD0) else Color.White
//                ),
//                contentPadding = PaddingValues(10.dp),
//                shape = CircleShape,
//                modifier = Modifier
//                    .width(40.dp)
//                    .height(40.dp),
//
//                elevation = ButtonDefaults.elevation(
//                    defaultElevation = 2.dp,
//                    pressedElevation = 4.dp,
//                    disabledElevation = 2.dp
//                )
//            ) {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.Start
//                ) {
//                    Icon(
//                        Icons.Filled.Cloud,
//                        contentDescription = null,
//                        modifier = Modifier.size(16.dp)
//                    )
//
//                }
//            }
//
//            Button(
//                onClick = {
//                    mapType = "precipitation_new"
//                },
//                colors = ButtonDefaults.buttonColors(
//                    contentColor = Color.DarkGray,
//                    backgroundColor = if (mapType == "precipitation_new") Color(0xFFFFFDD0) else Color.White
//                ),
//                contentPadding = PaddingValues(10.dp),
//                shape = CircleShape,
//                modifier = Modifier
//                    .width(40.dp)
//                    .height(40.dp)
//
//                ,
//                elevation = ButtonDefaults.elevation(
//                    defaultElevation = 2.dp,
//                    pressedElevation = 4.dp,
//                    disabledElevation = 2.dp
//                ),
//
//                ) {
//                Row(modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.Start) {
//
//                    Icon(
//                        Icons.Filled.Opacity,
//                        contentDescription = null,
//                        modifier = Modifier.size(16.dp)
//                    )
//
//                }
//            }
//            Button(
//                onClick = {
//                    mapType = "temp_new"
//
//
//                },
//                colors = ButtonDefaults.buttonColors(
//                    contentColor = Color.DarkGray,
//                    backgroundColor = if (mapType == "temp_new") Color(0xFFFFFDD0) else Color.White
//                ),
//                contentPadding = PaddingValues(10.dp),
//                shape = CircleShape,
//                modifier = Modifier
//                    .width(40.dp)
//                    .height(40.dp),
//                elevation = ButtonDefaults.elevation(
//                    defaultElevation = 2.dp,
//                    pressedElevation = 4.dp,
//                    disabledElevation = 2.dp
//                )
//            ) {
//                Row(modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.Start) {
//                    Icon(
//                        Icons.Filled.Thermostat,
//                        contentDescription = null,
//                        modifier = Modifier.size(16.dp)
//                    )
//
//                }
//            }
//            Button(
//                onClick = {
//                    mapType = "wind_new"
//                },
//                colors = ButtonDefaults.buttonColors(
//                    contentColor = Color.DarkGray,
//                    backgroundColor = if (mapType == "wind_new") Color(0xFFFFFDD0) else Color.White
//                ),
//                contentPadding = PaddingValues(10.dp),
//                shape = CircleShape,
//                modifier = Modifier
//                    .width(40.dp)
//                    .height(40.dp),
//                elevation = ButtonDefaults.elevation(
//                    defaultElevation = 2.dp,
//                    pressedElevation = 4.dp,
//                    disabledElevation = 2.dp
//                )
//            ) {
//                Row(modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.Start) {
//                    Icon(
//                        Icons.Filled.Air,
//                        contentDescription = null,
//                        modifier = Modifier.size(16.dp)
//                    )
//
//                }
//            }
//        }
            }
        }
    }
}

class CustomInfoWindowForGoogleMap(context: Context) : GoogleMap.InfoWindowAdapter {

    var mContext = context
    var mWindow = (context as Activity).layoutInflater.inflate(R.layout.custom_info_window, null)

    private fun rendowWindowText(marker: Marker, view: View){

        val tvTitle = view.findViewById<TextView>(R.id.title)
        val tvTemp = view.findViewById<TextView>(R.id.temp)
        val tvSnippet = view.findViewById<TextView>(R.id.snippet)
        marker.title?.let {
            tvTitle.text = it.split(",").toTypedArray()[0]
            tvTemp.text = it.split(",").toTypedArray()[1]
        }
        tvSnippet.text = marker.snippet

    }

    override fun getInfoContents(marker: Marker): View {
        rendowWindowText(marker, mWindow)
        return mWindow
    }

    override fun getInfoWindow(marker: Marker): View? {
        rendowWindowText(marker, mWindow)
        return mWindow
    }
}


const val InitialZoom = 4f
const val MinZoom = 3f
const val MaxZoom = 5f




@Composable
fun MapViewContainer(
    map: MapView,
    latitude: Double,
    longitude: Double,
    mapType:String,
    model: WeatherViewModel,



    ) {
    val index by model.index.observeAsState(initial = 0)
    var zoom by rememberSaveable(map) { mutableStateOf(InitialZoom) }
    val coroutineScope = rememberCoroutineScope()
    var hasOverlay by remember { mutableStateOf(false)}
    val coordinates = LatLng(latitude,longitude)
    var overlays:MutableList<TileOverlay>  by remember { mutableStateOf(  ArrayList<TileOverlay>())}
    val darkTheme = isSystemInDarkTheme()
    Box(contentAlignment = Alignment.CenterEnd,modifier = Modifier.fillMaxSize()) {
        AndroidView({ map }) { mapView ->

            val mapZoom = zoom

            mapView.getMapAsync {
                it.setMapStyle(if(darkTheme) MapStyleOptions.loadRawResourceStyle(context,R.raw.map_in_dark) else null)
                val tileProvider: TileProvider = object : UrlTileProvider(256, 256) {
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
                it.setInfoWindowAdapter(CustomInfoWindowForGoogleMap(context = context))

                val icon = BitmapFactory.decodeResource(context.resources,getWeatherIcon(input = if(model.locations.isNotEmpty()) (model.locations[index].data.current.weather[0].icon ?: "") else ""))
                val width: Int = icon.width
                val height: Int = icon.height
                val ratio:Double = height.toDouble()/width.toDouble()

                val smallMarker = icon.scale(width = 90,height = (90*ratio).toInt(),filter = false)

                val markerOptions = MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                    .title(if(model.locations.isNotEmpty()) "${model.locations[index].name},${model.locations[index].data.current.temp.toUInt()}°" else ",")
                    .snippet(if(model.locations.isNotEmpty()) model.locations[index].data.current.weather[0].description else "")



                val marker =  it.addMarker(

                    markerOptions.position(coordinates)
                )
                marker?.showInfoWindow()
//            it.addMarker(
//
//                MarkerOptions().position(position)
//            )
                it.animateCamera(CameraUpdateFactory.newLatLng(coordinates))
                it.uiSettings.isScrollGesturesEnabled = false
                it.uiSettings.isZoomGesturesEnabled = true
                val tile = it.addTileOverlay(
                    TileOverlayOptions()
                        .tileProvider(tileProvider).fadeIn(true)
                )


                tile?.let { overlay ->
                    overlay.transparency = 0.2f
                    overlay.fadeIn = true
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