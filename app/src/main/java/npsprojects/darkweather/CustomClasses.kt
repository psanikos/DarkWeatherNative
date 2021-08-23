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
        "clear-day" -> Color(0xFF1ca6f0)
        "clear-night" -> Color(0xFF183050)
        "rain" -> Color(0xFF305c81)
        "snow" -> Color(0xFFbadbe3)
        "sleet" -> Color(0xFFc5e1e8)
        "wind" -> Color(0xFF90a699)
        "fog" -> Color(0xFF85a588)
        "cloudy" -> Color(0xFF5a6469)
        "partly-cloudy-day" -> Color(0xFF1d4c70)
        "partly-cloudy-night" -> Color(0xFF1d4c57)
        "hail" -> Color(0xFFc5e1e8)
        "thunderstorm" -> Color(0xFF130c36)
        "tornado" -> Color(0xFF1e1640)
        else -> Color(0xFF2F4276)
    }
}

fun getWeatherIcon(input: String): Int {

    return when (input) {
        "clear-day" -> R.drawable.sun
        "clear-night" -> R.drawable.clearnight
        "rain" -> R.drawable.rain
        "snow" -> R.drawable.snow
        "sleet" -> R.drawable.snow
        "wind" -> R.drawable.air
        "fog" -> R.drawable.clouds
        "cloudy" -> R.drawable.clouds
        "partly-cloudy-day" -> R.drawable.partlycloudy
        "partly-cloudy-night" -> R.drawable.partlycloudynight
        "hail" -> R.drawable.snow
        "thunderstorm" -> R.drawable.heavyrain
        "tornado" -> R.drawable.air
        else -> R.drawable.sun
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

