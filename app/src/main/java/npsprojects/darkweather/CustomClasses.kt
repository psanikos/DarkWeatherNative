package npsprojects.darkweather

import android.annotation.SuppressLint
import android.text.format.DateUtils
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileOverlay
import npsprojects.darkweather.views.InitialZoom
import npsprojects.darkweather.views.rememberMapViewWithLifecycle
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.*

const val openWeatherKey = "e1e45feaea76d66517c25291f2633d9a"

enum class DeviceType{
    BIGSCREEN,PHONE
}

enum class WeatherError {
    NONETWORK, NOGPS, NOPERMISSION, NONE
}

enum class RainTimeCategory {
    HOURLY, DAILY
}
enum class WeatherUnits {
    SI, US, AUTO
}
class Coordinates(
    val latitude: Double,
    val longitude: Double
)
fun isOnline() : Boolean {
    val runtime = Runtime.getRuntime()
    return try {
        val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
        val exitValue = ipProcess.waitFor()
        exitValue == 0
    } catch (e: IOException) {

        false
    } catch (e: InterruptedException) {
        false
    }

}
fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return kotlin.math.round(this * multiplier) / multiplier
}
fun getWeatherColor(input: String): Color {
    return when (input) {
        "01d" -> Color(235, 220, 180)
        "01n" -> Color(0xFF183050)
        "09d","10d","09n","10n" -> Color(0xFF305c81)
        "13d","13n" -> Color(0xFFbadbe3)

        "50d","50n" -> Color(0xFF90a699)

        "04d","04n","03d","03n" -> Color(0xFF5a6469)
        "02d" -> Color(0xFF1d4c70)
        "02n" -> Color(0xFF1d4c57)

        "11d","11n" -> Color(0xFF130c36)

        else -> Color(0xFF2F4276)
    }
}
fun getWeatherBackground(input: String): Int {
    return when (input) {
        "01d","01n" -> R.drawable.sunnyback
        "09d","10d","09n","10n" -> R.drawable.rainback
        "13d","13n" -> R.drawable.snowback

        "50d","50n" -> R.drawable.mistback // fog

        "04d","04n","03d","03n" -> R.drawable.cloudyback // cloudy
        "02d", "02n" -> R.drawable.partlycloudyback

        "11d","11n" -> R.drawable.stormback

        else ->  R.drawable.partlycloudyback
    }
}
//fun getWeatherIcon(input: String): Int {
//
//    return when (input) {
//        "clear-day" -> R.drawable.sun
//        "clear-night" -> R.drawable.clearnight
//        "rain" -> R.drawable.rain
//        "snow" -> R.drawable.snow
//        "sleet" -> R.drawable.snow
//        "wind" -> R.drawable.air
//        "fog" -> R.drawable.clouds
//        "cloudy" -> R.drawable.clouds
//        "partly-cloudy-day" -> R.drawable.partlycloudy
//        "partly-cloudy-night" -> R.drawable.partlycloudynight
//        "hail" -> R.drawable.snow
//        "thunderstorm" -> R.drawable.heavyrain
//        "tornado" -> R.drawable.air
//        else -> R.drawable.sun
//    }
//}
fun moonIcon(input:Double):Int{

    return when(input){
        0.0 -> R.drawable.moon0
        in 0.01 .. 0.17 -> R.drawable.moon1
        in 0.18 .. 0.34 -> R.drawable.moon2
        in 0.35 .. 0.49 -> R.drawable.moon3
        0.5 -> R.drawable.moon4
        in 0.51 .. 0.67 -> R.drawable.moon5
        in 0.68 .. 0.84 -> R.drawable.moon6
        in 0.85 .. 0.99 -> R.drawable.moon7
        1.0 -> R.drawable.moon0
        else -> R.drawable.moon0
    }

}
fun moonDecription(input:Double):Int{

    return when(input){
        0.0 ->  R.string.new_moon
        in 0.01 .. 0.49 -> R.string.waxing
        0.5 -> R.string.full_moon
        in 0.51 .. 0.99 -> R.string.waning
        1.0 -> R.string.new_moon
        else -> R.string.new_moon
    }

}
fun getWeatherIcon(input: String): Int {

    return when (input) {
        "01d" -> R.drawable.i01d
        "02d" -> R.drawable.i02d
        "03d" -> R.drawable.i03d
        "04d" -> R.drawable.i04d
        "01n" -> R.drawable.i01n
        "02n" -> R.drawable.i02n
        "03n" -> R.drawable.i03n
        "04n" -> R.drawable.i04n
        "09n" -> R.drawable.i09n
        "10n" -> R.drawable.i10n
        "11n" -> R.drawable.i11n
        "13n" -> R.drawable.i13n
        "50n" -> R.drawable.i50n
        "09d" -> R.drawable.i09d
        "10d" -> R.drawable.i10d
        "11d" -> R.drawable.i11d
        "13d" -> R.drawable.i13d
        "50d" -> R.drawable.i50d
        else -> R.drawable.i02d
    }
}

fun Date.timeAgo():String {
  val dateString = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).format(this)
    var conversionTime =""
    try{
        val format = "yyyy-MM-dd hh:mm:ss"

        val sdf = SimpleDateFormat(format)

        val datetime= Calendar.getInstance()
        var date2= sdf.format(datetime.time).toString()

        val dateObj1 = sdf.parse(dateString)
        val dateObj2 = sdf.parse(date2)
        val diff = dateObj2.time - dateObj1.time

        val diffDays = diff / (24 * 60 * 60 * 1000)
        val diffhours = diff / (60 * 60 * 1000)
        val diffmin = diff / (60 * 1000)
        val diffsec = diff  / 1000
        if(diffDays>1){
            conversionTime+= "$diffDays days "
        }else if(diffhours>1){
            conversionTime+=(diffhours-diffDays*24).toString()+" hours "
        }else if(diffmin>1){
            conversionTime+=(diffmin-diffhours*60).toString()+" min "
        }else if(diffsec>1){
            conversionTime+=(diffsec-diffmin*60).toString()+" sec "
        }
    }catch (ex:java.lang.Exception){
        Log.e("formatTimeAgo",ex.toString())
    }
    if(conversionTime!=""){
        conversionTime+="ago"
    }
    return conversionTime
}
fun Date.ago():String {
    val dateString = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).format(this)
    val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault())
    // sdf.timeZone = TimeZone.getDefault()
    return try {
        val time = this.toInstant().atZone(ZoneId.systemDefault()).toEpochSecond()*1000
        val now = Instant.now().toEpochMilli()
        val ago = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS)
        ago.toString()
    } catch (e: ParseException) {
        e.printStackTrace()
        ""
    }
}

@Composable
fun OverlayView(showOverlay:Boolean,color: Color,body:@Composable () -> Unit){
Box(modifier = Modifier.wrapContentSize().background(color = color.copy(alpha = if(showOverlay) 0.9f else 0.0f))){
    body()
}
}

//val map = rememberMapViewWithLifecycle()
//val mapType by remember { mutableStateOf("clouds_new") }
//val coordinates by remember {
//    mutableStateOf(
//        if (model.locations.isNotEmpty()) LatLng(
//            model.locations[model.index].data.lat,
//            model.locations[model.index].data.lon
//        ) else LatLng(37.9838, 23.7275)
//    )
//}
//var zoom by rememberSaveable(map) { mutableStateOf(InitialZoom) }
//
//val overlays: MutableList<TileOverlay> by remember { mutableStateOf(ArrayList<TileOverlay>()) }