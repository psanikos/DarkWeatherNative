package com.npsappprojects.darkweather

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize





@SuppressLint("ParcelCreator")
@Parcelize
data class WeatherResponse(
    @SerializedName("currently")
    val currently: Currently = Currently(),
    @SerializedName("daily")
    val daily: Daily = Daily(),
    @SerializedName("flags")
    val flags: Flags = Flags(),
    @SerializedName("hourly")
    val hourly: Hourly = Hourly(),
    @SerializedName("latitude")
    val latitude: Double = 0.0,
    @SerializedName("longitude")
    val longitude: Double = 0.0,
    @SerializedName("offset")
    val offset: Int = 0,
    @SerializedName("timezone")
    val timezone: String = ""
) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class Currently(
    @SerializedName("apparentTemperature")
    val apparentTemperature: Double? = 0.0,
    @SerializedName("cloudCover")
    val cloudCover: Double? = 0.0,
    @SerializedName("dewPoint")
    val dewPoint: Double? = 0.0,
    @SerializedName("humidity")
    val humidity: Double? = 0.0,
    @SerializedName("icon")
    val icon: String? = "",
    @SerializedName("ozone")
    val ozone: Double? = 0.0,
    @SerializedName("precipIntensity")
    val precipIntensity: Double? = 0.0,
    @SerializedName("precipProbability")
    val precipProbability: Double? = 0.0,
    @SerializedName("pressure")
    val pressure: Double? = 0.0,
    @SerializedName("summary")
    val summary: String? = "",
    @SerializedName("temperature")
    val temperature: Double? = 0.0,
    @SerializedName("time")
    val time: Int = 0,
    @SerializedName("uvIndex")
    val uvIndex: Int = 0,
    @SerializedName("visibility")
    val visibility: Double? = 0.0,
    @SerializedName("windBearing")
    val windBearing: Int = 0,
    @SerializedName("windGust")
    val windGust: Double? = 0.0,
    @SerializedName("windSpeed")
    val windSpeed: Double? = 0.0
) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class Daily(
    @SerializedName("data")
    val `data`: List<Data> = listOf(),
    @SerializedName("icon")
    val icon: String? = "",
    @SerializedName("summary")
    val summary: String? = ""
) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class Flags(
    @SerializedName("meteoalarm-license")
    val meteoalarmLicense: String? = "",
    @SerializedName("nearest-station")
    val nearestStation: Double? = 0.0,
    @SerializedName("sources")
    val sources: List<String>? = listOf(),
    @SerializedName("units")
    val units: String? = ""
) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class Hourly(
    @SerializedName("data")
    val `data`: List<DataX> = listOf(),
    @SerializedName("icon")
    val icon: String? = "",
    @SerializedName("summary")
    val summary: String? = ""
) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class Data(
    @SerializedName("apparentTemperatureHigh")
    val apparentTemperatureHigh: Double? = 0.0,
    @SerializedName("apparentTemperatureHighTime")
    val apparentTemperatureHighTime: Int? = 0,
    @SerializedName("apparentTemperatureLow")
    val apparentTemperatureLow: Double? = 0.0,
    @SerializedName("apparentTemperatureLowTime")
    val apparentTemperatureLowTime: Int? = 0,
    @SerializedName("apparentTemperatureMax")
    val apparentTemperatureMax: Double? = 0.0,
    @SerializedName("apparentTemperatureMaxTime")
    val apparentTemperatureMaxTime: Int? = 0,
    @SerializedName("apparentTemperatureMin")
    val apparentTemperatureMin: Double? = 0.0,
    @SerializedName("apparentTemperatureMinTime")
    val apparentTemperatureMinTime: Int? = 0,
    @SerializedName("cloudCover")
    val cloudCover: Double? = 0.0,
    @SerializedName("dewPoint")
    val dewPoint: Double? = 0.0,
    @SerializedName("humidity")
    val humidity: Double? = 0.0,
    @SerializedName("icon")
    val icon: String? = "",
    @SerializedName("moonPhase")
    val moonPhase: Double = 0.0,
    @SerializedName("ozone")
    val ozone: Double? = 0.0,
    @SerializedName("precipIntensity")
    val precipIntensity: Double? = 0.0,
    @SerializedName("precipIntensityMax")
    val precipIntensityMax: Double? = 0.0,
    @SerializedName("precipIntensityMaxTime")
    val precipIntensityMaxTime: Int? = 0,
    @SerializedName("precipProbability")
    val precipProbability: Double? = 0.0,
    @SerializedName("precipType")
    val precipType: String? = "",
    @SerializedName("pressure")
    val pressure: Double? = 0.0,
    @SerializedName("summary")
    val summary: String? = "",
    @SerializedName("sunriseTime")
    val sunriseTime: Int? = 0,
    @SerializedName("sunsetTime")
    val sunsetTime: Int? = 0,
    @SerializedName("temperatureHigh")
    val temperatureHigh: Double? = 0.0,
    @SerializedName("temperatureHighTime")
    val temperatureHighTime: Int? = 0,
    @SerializedName("temperatureLow")
    val temperatureLow: Double? = 0.0,
    @SerializedName("temperatureLowTime")
    val temperatureLowTime: Int? = 0,
    @SerializedName("temperatureMax")
    val temperatureMax: Double? = 0.0,
    @SerializedName("temperatureMaxTime")
    val temperatureMaxTime: Int? = 0,
    @SerializedName("temperatureMin")
    val temperatureMin: Double? = 0.0,
    @SerializedName("temperatureMinTime")
    val temperatureMinTime: Int? = 0,
    @SerializedName("time")
    val time: Int? = 0,
    @SerializedName("uvIndex")
    val uvIndex: Int? = 0,
    @SerializedName("uvIndexTime")
    val uvIndexTime: Int? = 0,
    @SerializedName("visibility")
    val visibility: Double? = 0.0,
    @SerializedName("windBearing")
    val windBearing: Int? = 0,
    @SerializedName("windGust")
    val windGust: Double? = 0.0,
    @SerializedName("windGustTime")
    val windGustTime: Int? = 0,
    @SerializedName("windSpeed")
    val windSpeed: Double? = 0.0
) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class DataX(
    @SerializedName("apparentTemperature")
    val apparentTemperature: Double? = 0.0,
    @SerializedName("cloudCover")
    val cloudCover: Double? = 0.0,
    @SerializedName("dewPoint")
    val dewPoint: Double? = 0.0,
    @SerializedName("humidity")
    val humidity: Double? = 0.0,
    @SerializedName("icon")
    val icon: String? = "",
    @SerializedName("ozone")
    val ozone: Double? = 0.0,
    @SerializedName("precipIntensity")
    val precipIntensity: Double? = 0.0,
    @SerializedName("precipProbability")
    val precipProbability: Double? = 0.0,
    @SerializedName("precipType")
    val precipType: String? = "",
    @SerializedName("pressure")
    val pressure: Double? = 0.0,
    @SerializedName("summary")
    val summary: String? = "",
    @SerializedName("temperature")
    val temperature: Double? = 0.0,
    @SerializedName("time")
    val time: Int? = 0,
    @SerializedName("uvIndex")
    val uvIndex: Int? = 0,
    @SerializedName("visibility")
    val visibility: Double? = 0.0,
    @SerializedName("windBearing")
    val windBearing: Int? = 0,
    @SerializedName("windGust")
    val windGust: Double? = 0.0,
    @SerializedName("windSpeed")
    val windSpeed: Double? = 0.0
) : Parcelable


fun getWeatherColor(input: String): Color {
    return when (input) {
        "clear-day" -> Color(0xFF26b1f6)
        "clear-night" -> Color(0xFF183050)
        "rain" -> Color(0xFF305c81)
        "snow" -> Color(0xFFbadbe3)
        "sleet" -> Color(0xFFc5e1e8)
        "wind" -> Color(0xFF90a699)
        "fog" -> Color(0xFF85a588)
        "cloudy" -> Color(0xFF77807a)
        "partly-cloudy-day" -> Color(0xFF1d4c70)
        "partly-cloudy-night" -> Color(0xFF1d4c57)
        "hail" -> Color(0xFFc5e1e8)
        "thunderstorm" -> Color(0xFF130c36)
        "tornado" -> Color(0xFF1e1640)
        else -> Color(0xFF18a9c9)
    }
}

fun getWeatherIcon(input: String): Int {

    return when (input) {
        "clear-day" ->  R.drawable.sun
        "clear-night" -> R.drawable.clearnight
        "rain" -> R.drawable.rain
        "snow" -> R.drawable.snow
        "sleet" -> R.drawable.snow
        "wind" -> R.drawable.air
        "fog" -> R.drawable.clouds
        "cloudy" -> R.drawable.clouds
        "partly-cloudy-day" ->  R.drawable.partlycloudyday
        "partly-cloudy-night" -> R.drawable.partlycloudynight
        "hail" -> R.drawable.snow
        "thunderstorm" -> R.drawable.heavyrain
        "tornado" -> R.drawable.air
        else -> R.drawable.sun
    }
}