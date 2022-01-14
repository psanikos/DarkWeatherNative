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
import com.google.android.material.shape.MarkerEdgeTreatment
import npsprojects.darkweather.R
import npsprojects.darkweather.models.WeatherModel
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
    val locations by model.locations.observeAsState(initial = listOf<WeatherModel>())

    val testCoordinates = LatLng(37.98384, 23.72753)

    var coordinates by rememberSaveable {
        mutableStateOf(
            testCoordinates
        )
    }
    var zoom by rememberSaveable(map) { mutableStateOf(InitialZoom) }
    val overlays: MutableList<TileOverlay> by rememberSaveable { mutableStateOf(java.util.ArrayList<TileOverlay>()) }
    LaunchedEffect(key1 = index + locations.size, block = {
        if (locations.isNotEmpty()) {
            coordinates = LatLng(
                locations[index].data.lat,
                locations[index].data.lon
            )
        }

    })

            Box(modifier = Modifier.fillMaxSize().clickable {
                controller.navigate("Map")
            }, contentAlignment = Alignment.BottomEnd) {


                MapViewContainer(
                    map = map,
                    latitude = coordinates.latitude,
                    longitude = coordinates.longitude,
                    mapType = mapType,
                    model = model
                )

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


const val InitialZoom = 9f
const val MinZoom = 5f
const val MaxZoom = 20f




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

                it.setInfoWindowAdapter(CustomInfoWindowForGoogleMap(context = context))

//                val icon = BitmapFactory.decodeResource(context.resources,getWeatherIcon(input = if(model.locations.isNotEmpty()) (model.locations[index].data.current.weather[0].icon ?: "") else ""))
//                val width: Int = icon.width
//                val height: Int = icon.height
//                val ratio:Double = height.toDouble()/width.toDouble()
//
//                val smallMarker = icon.scale(width = 90,height = (90*ratio).toInt(),filter = false)
//
//                val markerOptions = MarkerOptions()
//                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
//                    .title(if(model.locations.isNotEmpty()) "${model.locations[index].name},${model.locations[index].data.current.temp.toUInt()}°" else ",")
//                    .snippet(if(model.locations.isNotEmpty()) model.locations[index].data.current.weather[0].description else "")


                it.clear()
                val marker =  it.addMarker(

                    MarkerOptions().position(coordinates)
                )
               // marker?.showInfoWindow()
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

                it.setZoom(mapZoom)
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