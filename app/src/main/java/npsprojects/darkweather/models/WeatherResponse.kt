package npsprojects.darkweather.models

import android.annotation.SuppressLint
import android.os.Parcelable

import androidx.compose.ui.graphics.Color
import com.google.android.gms.common.util.CollectionUtils
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import npsprojects.darkweather.Coordinates
import npsprojects.darkweather.R
import npsprojects.darkweather.WeatherUnits
import npsprojects.darkweather.round
import java.util.*


fun getOpenWeatherUrl(locale:Coordinates,units:WeatherUnits):String{
    val unit =  if(units == WeatherUnits.SI)  "metric" else "imperial"
   return "https://api.openweathermap.org/data/2.5/onecall?lat=${locale.latitude}&lon=${locale.longitude}&exclude=minutely&units=${unit}&appid=e1e45feaea76d66517c25291f2633d9a"
}


@Parcelize
data class OpenWeather (
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezoneOffset: Long,
    val current: Current,
    val hourly: List<Current>,
    val daily: List<Daily>,
    val alerts: List<Alert>? = null
) : Parcelable

@Parcelize
data class Alert (
    val sender_name:String,
    val event: String?,
    val start:Int,
    val end: Int?,
   val description: String?
):Parcelable

@Parcelize
data class Current (
    val dt: Long,
    val sunrise: Long? = null,
    val sunset: Long? = null,
    val temp: Double,
    val feels_like: Double,
    val pressure: Long,
    val humidity: Long,
    val dew_point: Double,
    val uvi: Double,
    val clouds: Long,
    val visibility: Long,
    val wind_speed: Double,
    val wind_deg: Long,
    val wind_gust: Double,
    val weather: List<Weather>,
    val pop: Double? = null
):Parcelable

@Parcelize
data class Weather (
    val id: Long,
    val main: String,
    val description: String,
    val icon: String
):Parcelable


@Parcelize
data class Daily (
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val moonrise: Long,
    val moonset: Long,
    val moonPhase: Double,
    val temp: Temp,
    val feels_like: FeelsLike,
    val pressure: Long,
    val humidity: Long,
    val dewPoint: Double,
    val wind_speed: Double,
    val wind_deg: Long,
    val wind_gust: Double,
    val weather: List<Weather>,
    val clouds: Long,
    val pop: Double,
    val uvi: Double,
    val rain: Double? = null
):Parcelable

@Parcelize
data class FeelsLike (
    val day: Double,
    val night: Double,
    val eve: Double,
    val morn: Double
):Parcelable

@Parcelize
data class Temp (
    val day: Double,
    val min: Double,
    val max: Double,
    val night: Double,
    val eve: Double,
    val morn: Double
):Parcelable



class WeatherModel constructor(
    val name: String,
    val data: OpenWeather,
    val isCurrent: Boolean,
    val airQuality: AirQuality?
)

class SavedLocation constructor(
    val name: String,
    val latitude: Double,
    val longitude: Double
) {
    override fun toString(): String {
        return "[$name|${latitude.round(5)}|${longitude.round(5)}]"
    }
}

data class AirQuality(
//    @SerializedName("coord")
//    val coord: List<Int>? = listOf(),
    @SerializedName("list")
    val list: List<DetailsList>? = CollectionUtils.listOf()
)

data class DetailsList(
//    @SerializedName("components")
//    val components: Components? = Components(),
//    @SerializedName("dt")
//    val dt: Int? = 0,
    @SerializedName("main")
    val main: Main? = Main()
)

data class Components(
    @SerializedName("co")
    val co: Double? = 0.0,
    @SerializedName("nh3")
    val nh3: Double? = 0.0,
    @SerializedName("no")
    val no: Double? = 0.0,
    @SerializedName("no2")
    val no2: Double? = 0.0,
    @SerializedName("o3")
    val o3: Double? = 0.0,
    @SerializedName("pm10")
    val pm10: Double? = 0.0,
    @SerializedName("pm2_5")
    val pm25: Double? = 0.0,
    @SerializedName("so2")
    val so2: Double? = 0.0
)

data class Main(
    @SerializedName("aqi")
    val aqi: Int? = 0
)

