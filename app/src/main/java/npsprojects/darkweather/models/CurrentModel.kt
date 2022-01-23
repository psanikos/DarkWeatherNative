package npsprojects.darkweather.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CurrentWeather (
    val coord: Coord,
    val weather: List<CurWeather>,
    val base: String,
    val main: MainD,
    val visibility: Long,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Long,
    val sys: Sys,
    val timezone: Long,
    val id: Long,
    val name: String,
    val cod: Long
):Parcelable
@Parcelize
data class Clouds (
    val all: Long
):Parcelable
@Parcelize
data class Coord (
    val lon: Double,
    val lat: Double
):Parcelable
@Parcelize
data class MainD (
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Long,
    val humidity: Long
):Parcelable
@Parcelize
data class Sys (
    val type: Long,
    val id: Long,
    val country: String,
    val sunrise: Long,
    val sunset: Long
):Parcelable
@Parcelize
data class CurWeather (
    val id: Long,
    val main: String,
    val description: String,
    val icon: String
):Parcelable
@Parcelize
data class Wind (
    val speed: Double,
    val deg: Long,
    val gust: Double
):Parcelable
