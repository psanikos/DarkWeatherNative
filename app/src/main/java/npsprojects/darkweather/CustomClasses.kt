package npsprojects.darkweather

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.ui.graphics.Color
import java.io.IOException
import java.text.SimpleDateFormat
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
fun getWeatherIcon(input: String): Int {

    return when (input) {
        "01d" -> R.drawable.sun
        "02d" -> R.drawable.air
        "03d" -> R.drawable.clouds
        "04d" -> R.drawable.clouds
        "01n" -> R.drawable.clearnight
        "02n" -> R.drawable.partlycloudynight
        "03n" -> R.drawable.clouds
        "04n" -> R.drawable.clouds
        "09n" -> R.drawable.rain
        "10n" -> R.drawable.rain
        "11n" -> R.drawable.heavyrain
        "13n" -> R.drawable.snow
        "50n" -> R.drawable.air
        "09d" -> R.drawable.rain
        "10d" -> R.drawable.rain
        "11d" -> R.drawable.heavyrain
        "13d" -> R.drawable.snow
        "50d" -> R.drawable.air
        else -> R.drawable.air
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

