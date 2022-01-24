package npsprojects.darkweather.models

import android.os.Parcelable

data class CurrentWeather (
    val coord: Coord? = null,
    val weather: List<CurWeather>,
    val base: String? = null,
    val main: MainD? = null,
    val visibility: Long? = null,
    val wind: Wind? = null,
    val clouds: Clouds? = null,
    val dt: Long? = null,
    val sys: Sys? = null,
    val timezone: Long? = null,
    val id: Long? = null,
    val name: String? = null,
    val cod: Long? = null
)

data class Clouds (
    val all: Long? = null
)

data class Coord (
    val lon: Double? = null,
    val lat: Double? = null
)

data class MainD (
    val temp: Double? = null,
    val feels_like: Double? = null,
    val temp_min: Double? = null,
    val temp_max: Double? = null,
    val pressure: Long? = null,
    val humidity: Long? = null
)

data class Sys (
    val type: Long? = null,
    val id: Long? = null,
    val country: String? = null,
    val sunrise: Long? = null,
    val sunset: Long? = null
)

data class CurWeather (
    val id: Long? = null,
    val main: String? = null,
    val description: String? = null,
    val icon: String? = null
)

data class Wind (
    val speed: Double? = null,
    val deg: Long? = null,
    val gust: Double? = null
)
