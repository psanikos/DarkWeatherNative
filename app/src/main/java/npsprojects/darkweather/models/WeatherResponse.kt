package npsprojects.darkweather.models

import android.annotation.SuppressLint
import android.os.Parcelable
import android.util.Log

import androidx.compose.ui.graphics.Color
import com.google.android.gms.common.util.CollectionUtils
import com.squareup.moshi.Json
import npsprojects.darkweather.Coordinates
import npsprojects.darkweather.R
import npsprojects.darkweather.WeatherUnits
import npsprojects.darkweather.round
import npsprojects.darkweather.services.SavedLocation
import java.util.*





data class OpenWeather (
    val lat: Double? = null,
    val lon: Double? = null,
    val timezone: String? = null,
    val timezoneOffset: Long? = null,
    val current: Current? = null,
    val hourly: List<Current>,
    val daily: List<Daily>,
    val alerts: List<Alert>? = null
)


data class Alert (
    val sender_name:String? = null,
    val event: String? = null,
    val start:Int? = null,
    val end: Int? = null,
   val description: String? = null
)


data class Current (
    val dt: Long? = null,
    val sunrise: Long? = null,
    val sunset: Long? = null,
    val temp: Double? = null,
    val feels_like: Double? = null,
    val pressure: Long? = null,
    val humidity: Long? = null,
    val dew_point: Double? = null,
    val uvi: Double? = null,
    val clouds: Long? = null,
    val visibility: Long? = null,
    val wind_speed: Double? = null,
    val wind_deg: Long? = null,
    val wind_gust: Double? = null,
    val weather: List<Weather>,
    val pop: Double? = null
)


data class Weather (
    val id: Int? = null,
    val main: String? = null,
    val description: String? = null,
    val icon: String? = null
)



data class Daily (
    val dt: Long? = null,
    val sunrise: Long? = null,
    val sunset: Long? = null,
    val moonrise: Long? = null,
    val moonset: Long? = null,
    val moon_phase: Double? = null,
    val temp: Temp? = null,
    val feels_like: FeelsLike? = null,
    val pressure: Long? = null,
    val humidity: Long? = null,
    val dewPoint: Double? = null,
    val wind_speed: Double? = null,
    val wind_deg: Long? = null,
    val wind_gust: Double? = null,
    val weather: List<Weather>,
    val clouds: Long? = null,
    val pop: Double? = null,
    val uvi: Double? = null,
    val rain: Double? = null
)


data class FeelsLike (
    val day: Double? = null,
    val night: Double? = null,
    val eve: Double? = null,
    val morn: Double? = null
)


data class Temp (
    val day: Double? = null,
    val min: Double? = null,
    val max: Double? = null,
    val night: Double? = null,
    val eve: Double? = null,
    val morn: Double? = null
)



class WeatherModel constructor(
    val location: SavedLocation,
    val data: OpenWeather,
    val isCurrent: Boolean,
    val airQuality: AirQuality?
)



data class AirQuality(
//    @field:Json(name ="coord")
//    val coord: List<Int>? = listOf(),
    @field:Json(name ="list")
    val list: List<DetailsList>? = CollectionUtils.listOf()
)

data class DetailsList(
//    @field:Json(name ="components")
//    val components: Components? = Components(),
//    @field:Json(name ="dt")
//    val dt: Int? = 0,
    @field:Json(name ="main")
    val main: Main? = Main()
)

data class Components(
    @field:Json(name ="co")
    val co: Double? = 0.0,
    @field:Json(name ="nh3")
    val nh3: Double? = 0.0,
    @field:Json(name ="no")
    val no: Double? = 0.0,
    @field:Json(name ="no2")
    val no2: Double? = 0.0,
    @field:Json(name ="o3")
    val o3: Double? = 0.0,
    @field:Json(name ="pm10")
    val pm10: Double? = 0.0,
    @field:Json(name ="pm2_5")
    val pm25: Double? = 0.0,
    @field:Json(name ="so2")
    val so2: Double? = 0.0
)

data class Main(
    @field:Json(name ="aqi")
    val aqi: Int? = 0
)

