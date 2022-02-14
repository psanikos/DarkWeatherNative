package npsprojects.darkweather

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.text.format.DateUtils
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
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
fun isOnline(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (connectivityManager != null) {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
    }
    return false
}
fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return kotlin.math.round(this * multiplier) / multiplier
}

fun getWeatherBack(input: String): Int {
    return when (input) {
        "01d" -> R.drawable.dayphone
        "01n" -> R.drawable.nightphone
        "09d","10d" -> R.drawable.rainphone
        "09n","10n" -> R.drawable.rainphone
        "13d","13n" -> R.drawable.snowphone

        "50d" -> R.drawable.cloudphone
        "50n" -> R.drawable.cloudnightphone

        "04d","03d" -> R.drawable.cloudphone
        "04n","03n" -> R.drawable.cloudnightphone
        "02d" -> R.drawable.cloudphone
        "02n" -> R.drawable.cloudnightphone

        "11d" -> R.drawable.rainphone
       "11n" -> R.drawable.rainphone

        else ->  R.drawable.dayphone
    }
}

fun getWeatherBackIcon(input: String): Int {
    return when (input) {
        "01d" -> R.drawable.clearday
        "01n" -> R.drawable.clearnight
        "09d","10d" -> R.drawable.rainny
        "09n","10n" -> R.drawable.rainny
        "13d" -> R.drawable.daycloudy
        "13n" -> R.drawable.nightcloudy

        "50d" -> R.drawable.daycloudy
        "50n" -> R.drawable.nightcloudy

        "04d","03d" -> R.drawable.daycloudy
        "04n","03n" -> R.drawable.nightcloudy
        "02d" -> R.drawable.daycloudy
        "02n" -> R.drawable.nightcloudy

        "11d" -> R.drawable.rainny
        "11n" -> R.drawable.rainny

        else ->  R.drawable.clearday
    }
}

fun getWeatherImage(input: String): Int {
    return when (input) {
        "01d" -> R.drawable.sun
        "01n" -> R.drawable.moonclear
        "09d" -> R.drawable.lightrainday
        "09n" -> R.drawable.lightrainnight
        "10d","10n" -> R.drawable.rain
        "13d","13n" -> R.drawable.snow

        "50d","50n" -> R.drawable.wind
        "03d"-> R.drawable.partcloudday
        "03n"-> R.drawable.partcloudnight
        "04d","04n" -> R.drawable.clouds
        "02d" ->  R.drawable.partcloudday
        "02n" -> R.drawable.partcloudnight

        "11d","11n" -> R.drawable.thunderstorm

        else -> R.drawable.partcloudday
    }
}


//fun getWeatherIcon(input: Int, night:Boolean): ImageVector {
//    if(!night) {
//        //Day-----
//        return when (input) {
//            200 -> WeatherIcons.DayThunderstorm
//            201 -> WeatherIcons.DayThunderstorm
//            202 -> WeatherIcons.DayThunderstorm
//            210 -> WeatherIcons.DayLightning
//            211 -> WeatherIcons.DayLightning
//            212 -> WeatherIcons.DayLightning
//            221 -> WeatherIcons.DayLightning
//            230 -> WeatherIcons.DayThunderstorm
//            231 -> WeatherIcons.DayThunderstorm
//            232 -> WeatherIcons.DayThunderstorm
//            300 -> WeatherIcons.DaySprinkle
//            301 -> WeatherIcons.DaySprinkle
//            302 -> WeatherIcons.DayRain
//            310 -> WeatherIcons.DayRain
//            311 -> WeatherIcons.DayRain
//            312 -> WeatherIcons.DayRain
//            313 -> WeatherIcons.DayRain
//            314 -> WeatherIcons.DayRain
//            321 -> WeatherIcons.DaySprinkle
//            500 -> WeatherIcons.DaySprinkle
//            501 -> WeatherIcons.DayRain
//            502 -> WeatherIcons.DayRain
//            503 -> WeatherIcons.DayRain
//            504 -> WeatherIcons.DayRain
//            511 -> WeatherIcons.DayRainMix
//            520 -> WeatherIcons.DayShowers
//            521 -> WeatherIcons.DayShowers
//            522 -> WeatherIcons.DayShowers
//            531 -> WeatherIcons.DayStormShowers
//            600 -> WeatherIcons.DaySnow
//            601 -> WeatherIcons.DaySleet
//            602 -> WeatherIcons.DaySnow
//            611 -> WeatherIcons.DayRainMix
//            612 -> WeatherIcons.DayRainMix
//            615 -> WeatherIcons.DayRainMix
//            616 -> WeatherIcons.DayRainMix
//            620 -> WeatherIcons.DayRainMix
//            621 -> WeatherIcons.DaySnow
//            622 -> WeatherIcons.DaySnow
//            701 -> WeatherIcons.DayShowers
//            711 -> WeatherIcons.Smoke
//            721 -> WeatherIcons.DayHaze
//            731 -> WeatherIcons.Dust
//            741 -> WeatherIcons.DayFog
//            761 -> WeatherIcons.Dust
//            762 -> WeatherIcons.Dust
//            781 -> WeatherIcons.Tornado
//            800 -> WeatherIcons.DaySunny
//            801 -> WeatherIcons.DayCloudyGusts
//            802 -> WeatherIcons.DayCloudyGusts
//            803 -> WeatherIcons.DayCloudyGusts
//            804 -> WeatherIcons.DaySunnyOvercast
//            900 -> WeatherIcons.Tornado
//            902 -> WeatherIcons.Hurricane
//            903 -> WeatherIcons.SnowflakeCold
//            904 -> WeatherIcons.Hot
//            906 -> WeatherIcons.DayHail
//            957 -> WeatherIcons.StrongWind
//            else -> WeatherIcons.DaySunny
//        }
//    }
//    else {
//        //Night----
//        return when (input) {
//            200 -> WeatherIcons.NightAltThunderstorm
//            201 -> WeatherIcons.NightAltThunderstorm
//            202 -> WeatherIcons.NightAltThunderstorm
//            210 -> WeatherIcons.NightAltLightning
//            211 -> WeatherIcons.NightAltLightning
//            212 -> WeatherIcons.NightAltLightning
//            221 -> WeatherIcons.NightAltLightning
//            230 -> WeatherIcons.NightAltThunderstorm
//            231 -> WeatherIcons.NightAltThunderstorm
//            232 -> WeatherIcons.NightAltThunderstorm
//            300 -> WeatherIcons.NightAltSprinkle
//            301 -> WeatherIcons.NightAltSprinkle
//            302 -> WeatherIcons.NightAltRain
//            310 -> WeatherIcons.NightAltRain
//            311 -> WeatherIcons.NightAltRain
//            312 -> WeatherIcons.NightAltRain
//            313 -> WeatherIcons.NightAltRain
//            314 -> WeatherIcons.NightAltRain
//            321 -> WeatherIcons.NightAltSprinkle
//            500 -> WeatherIcons.NightAltSprinkle
//            501 -> WeatherIcons.NightAltRain
//            502 -> WeatherIcons.NightAltRain
//            503 -> WeatherIcons.NightAltRain
//            504 -> WeatherIcons.NightAltRain
//            511 -> WeatherIcons.NightAltRainMix
//            520 -> WeatherIcons.NightAltShowers
//            521 -> WeatherIcons.NightAltShowers
//            522 -> WeatherIcons.NightAltShowers
//            531 -> WeatherIcons.NightAltStormShowers
//            600 -> WeatherIcons.NightAltSnow
//            601 -> WeatherIcons.NightAltSleet
//            602 -> WeatherIcons.NightAltSnow
//            611 -> WeatherIcons.NightAltRainMix
//            612 -> WeatherIcons.NightAltRainMix
//            615 -> WeatherIcons.NightAltRainMix
//            616 -> WeatherIcons.NightAltRainMix
//            620 -> WeatherIcons.NightAltRainMix
//            621 -> WeatherIcons.NightAltSnow
//            622 -> WeatherIcons.NightAltSnow
//            701 -> WeatherIcons.NightAltShowers
//            711 -> WeatherIcons.Smoke
//            721 -> WeatherIcons.DayHaze
//            731 -> WeatherIcons.Dust
//            741 -> WeatherIcons.NightFog
//            761 -> WeatherIcons.Dust
//            762 -> WeatherIcons.Dust
//            781 -> WeatherIcons.Tornado
//            800 -> WeatherIcons.NightClear
//            801 -> WeatherIcons.NightAltCloudyGusts
//            802 -> WeatherIcons.NightAltCloudyGusts
//            803 -> WeatherIcons.NightAltCloudyGusts
//            804 -> WeatherIcons.NightAltCloudy
//            900 -> WeatherIcons.Tornado
//            902 -> WeatherIcons.Hurricane
//            903 -> WeatherIcons.SnowflakeCold
//            904 -> WeatherIcons.Hot
//            906 -> WeatherIcons.NightAltHail
//            957 -> WeatherIcons.StrongWind
//            else -> WeatherIcons.NightClear
//        }
//    }
////return when (input){
////    200-> WeatherIcons.Thunderstorm
////    201-> WeatherIcons.Thunderstorm
////    202-> WeatherIcons.Thunderstorm
////    210-> WeatherIcons.Lightning
////    211-> WeatherIcons.Lightning
////    212-> WeatherIcons.Lightning
////    221-> WeatherIcons.Lightning
////    230-> WeatherIcons.Thunderstorm
////    231-> WeatherIcons.Thunderstorm
////    232-> WeatherIcons.Thunderstorm
////    300-> WeatherIcons.Sprinkle
////    301-> WeatherIcons.Sprinkle
////    302-> WeatherIcons.Rain
////    310-> WeatherIcons.RainMix
////    311-> WeatherIcons.Rain
////    312-> WeatherIcons.Rain
////    313-> WeatherIcons.Showers
////    314-> WeatherIcons.Rain
////    321-> WeatherIcons.Sprinkle
////    500-> WeatherIcons.Sprinkle
////    501-> WeatherIcons.Rain
////    502-> WeatherIcons.Rain
////    503-> WeatherIcons.Rain
////    504-> WeatherIcons.Rain
////    511-> WeatherIcons.RainMix
////    520-> WeatherIcons.Showers
////    521-> WeatherIcons.Showers
////    522-> WeatherIcons.Showers
////    531-> WeatherIcons.StormShowers
////    600-> WeatherIcons.Snow
////    601-> WeatherIcons.Snow
////    602-> WeatherIcons.Sleet
////    611-> WeatherIcons.RainMix
////    612-> WeatherIcons.RainMix
////    615-> WeatherIcons.RainMix
////    616-> WeatherIcons.RainMix
////    620-> WeatherIcons.RainMix
////    621-> WeatherIcons.Snow
////    622-> WeatherIcons.Snow
////    701-> WeatherIcons.Showers
////    711-> WeatherIcons.Smoke
////    721-> WeatherIcons.DayHaze
////    731-> WeatherIcons.Dust
////    741-> WeatherIcons.Fog
////    761-> WeatherIcons.Dust
////    762-> WeatherIcons.Dust
////    771-> WeatherIcons.CloudyGusts
////    781-> WeatherIcons.Tornado
////    800-> WeatherIcons.DaySunny
////    801-> WeatherIcons.CloudyGusts
////    802-> WeatherIcons.CloudyGusts
////    803-> WeatherIcons.CloudyGusts
////    804-> WeatherIcons.Cloudy
////    900-> WeatherIcons.Tornado
////    901-> WeatherIcons.StormShowers
////    902-> WeatherIcons.Hurricane
////    903-> WeatherIcons.SnowflakeCold
////    904-> WeatherIcons.Hot
////    905-> WeatherIcons.Windy
////    906-> WeatherIcons.Hail
////    957-> WeatherIcons.StrongWind
////    else -> WeatherIcons.DaySunny
////}
////
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

fun moonDescription(input:Double):Int{

    return when(input){
        0.0 ->  R.string.new_moon
        in 0.01 .. 0.49 -> R.string.waxing
        0.5 -> R.string.full_moon
        in 0.51 .. 0.99 -> R.string.waning
        1.0 -> R.string.new_moon
        else -> R.string.new_moon
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
Box(modifier = Modifier
    .wrapContentSize()
    .background(color = color.copy(alpha = if (showOverlay) 0.9f else 0.0f))){
    body()
}
}
fun getBackColor(input:String):Color {
    return when (input) {

        "01d"-> Color(red=235/255, green=220/255, blue=180/255)
        "01n"-> Color(0xFF183050)
        "09d","09n","10d","10n"-> Color(0xFF305c81)
        "13d"-> Color(0xFFbadbe3)
        "50d"-> Color(0xFFc5e1e8)
        "50n"-> Color(0xFF90a699)
        "04d","04n","03n","03d"->  Color(0xFF3B5C77)
        "02d"-> Color(0xFF1d4c70)
        "02n"-> Color(0xFF1d4c57)
        "13n"->  Color(0xFFc5e1e8)
        "11d"-> Color(0xFF130c36)
        "11n"->  Color(0xFF1e1640)
        else -> Color(0xFF18a9c9)
    }

}
fun getBackColorHex(input:String):Long {
    return when (input) {

        "01d"-> 0xFFfff8d6
        "01n"-> 0xFF183050
        "09d","09n","10d","10n"-> 0xFF305c81
        "13d"-> 0xFFbadbe3
        "50d"-> 0xFFc5e1e8
        "50n"-> 0xFF90a699
        "04d","04n","03n","03d"-> 0xFF3B5C77
        "02d"-> 0xFF1d4c70
        "02n"-> 0xFF1d4c57
        "13n"->  0xFFc5e1e8
        "11d"-> 0xFF130c36
        "11n"->  0xFF1e1640
        else -> 0xFF18a9c9
    }

}
fun LazyListScope.gridItems(
    count: Int,
    nColumns: Int,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    itemContent: @Composable BoxScope.(Int) -> Unit,
) {
    gridItems(
        data = List(count) { it },
        nColumns = nColumns,
        horizontalArrangement = horizontalArrangement,
        itemContent = itemContent,
    )
}

fun <T> LazyListScope.gridItems(
    data: List<T>,
    nColumns: Int,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    key: ((item: T) -> Any)? = null,
    itemContent: @Composable BoxScope.(T) -> Unit,
) {
    val rows = if (data.isEmpty()) 0 else 1 + (data.count() - 1) / nColumns
    items(rows) { rowIndex ->
        Row(horizontalArrangement = horizontalArrangement) {
            for (columnIndex in 0 until nColumns) {
                val itemIndex = rowIndex * nColumns + columnIndex
                if (itemIndex < data.count()) {
                    val item = data[itemIndex]
                    androidx.compose.runtime.key(key?.invoke(item)) {
                        Box(
                            modifier = Modifier.weight(1f, fill = true),
                            propagateMinConstraints = true
                        ) {
                            itemContent.invoke(this, item)
                        }
                    }
                } else {
                    Spacer(Modifier.weight(1f, fill = true))
                }
            }
        }
    }
}
fun Color.isDark(): Boolean {
    return ColorUtils.calculateLuminance(this.toArgb()) < 0.5
}