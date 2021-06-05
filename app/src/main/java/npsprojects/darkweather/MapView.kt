package npsprojects.darkweather

import android.R.attr
import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.FloatRange
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateOffsetAsState
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
import androidx.compose.material.icons.rounded.Autorenew
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.scale
import androidx.navigation.NavController

import kotlinx.coroutines.launch
import npsprojects.darkweather.ui.theme.*
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
import android.R.attr.maxWidth

import android.R.attr.maxHeight




private val weatherKey = "e1e45feaea76d66517c25291f2633d9a"




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




const val InitialZoom = 5f
const val MinZoom = 2f
const val MaxZoom = 8f




@Composable
fun MapViewContainer(
    map: MapView,
    latitude: Double,
    longitude: Double,
    mapType:String,
    model: WeatherViewModel,
    index:Int,


) {

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

            val icon = BitmapFactory.decodeResource(context.resources,getWeatherIcon(input = if(model.locations.isNotEmpty()) (model.locations[index].data.currently.icon ?: "") else ""))
            var width: Int = icon.width
            var height: Int = icon.height
            val ratio:Double = height.toDouble()/width.toDouble()

            val smallMarker = icon.scale(width = 90,height = (90*ratio).toInt(),filter = false)

            val markerOptions = MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                .title(if(model.locations.isNotEmpty()) "${model.locations[index].name},${model.locations[index].data.currently.temperature?.toInt()}°" else ",")
                .snippet(if(model.locations.isNotEmpty()) "${model.locations[index].data.currently.summary}" else "")



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
            val tile = it.addTileOverlay(
                TileOverlayOptions()
                    .tileProvider(tileProvider).fadeIn(true)
            )


            tile?.let { overlay ->
                overlay.transparency = 0.2f
              
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