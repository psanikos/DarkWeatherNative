package npsprojects.darkweather.services

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import npsprojects.darkweather.Coordinates
import npsprojects.darkweather.WeatherUnits
import npsprojects.darkweather.models.AirQuality
import npsprojects.darkweather.models.CurrentWeather
import npsprojects.darkweather.models.OpenWeather
import npsprojects.darkweather.openWeatherKey
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


private fun airApiCallString(lat:Double, lon:Double):String = "https://api.openweathermap.org/data/2.5/air_pollution?lat=$lat&lon=$lon&appid=$openWeatherKey"
const val openWeatherKey = "e1e45feaea76d66517c25291f2633d9a"
fun getOpenWeatherUrl(locale: Coordinates, units: WeatherUnits, lang:String):String{
    val unit =  if(units == WeatherUnits.SI || units == WeatherUnits.AUTO)  "metric" else "imperial"
    return  "https://api.openweathermap.org/data/2.5/onecall?lat=${locale.latitude}&lon=${locale.longitude}&exclude=minutely&units=${unit}&lang=$lang&appid=e1e45feaea76d66517c25291f2633d9a"
}
fun getOpenWeatherUrlLite(locale: Coordinates, units: WeatherUnits, lang:String):String{
    val unit =  if(units == WeatherUnits.SI || units == WeatherUnits.AUTO)  "metric" else "imperial"
    Log.d("Url","https://api.openweathermap.org/data/2.5/weather?lat=${locale.latitude}&lon=${locale.longitude}&units=${unit}&lang=$lang&appid=e1e45feaea76d66517c25291f2633d9a")
    return  "https://api.openweathermap.org/data/2.5/weather?lat=${locale.latitude}&lon=${locale.longitude}&units=${unit}&lang=$lang&appid=e1e45feaea76d66517c25291f2633d9a"
}


private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl("https://api.openweathermap.org/data/2.5/")
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()

interface RetrofitDataFetch {
    @GET("onecall?&exclude=minutely")
    suspend fun oneCallWeather(@Query("lat") lat:Double, @Query("lon") lon:Double, @Query("units") unit:String, @Query("lang") lang:String,@Query("appid") appid:String): OpenWeather

    @GET("weather?")
    suspend fun currentWeather(@Query("lat") lat:Double, @Query("lon") lon:Double, @Query("units") unit:String, @Query("lang") lang:String,@Query("appid") appid:String): CurrentWeather


    @GET("air_pollution?")
    suspend fun airData(@Query("lat") lat:Double,@Query("lon") lon:Double,@Query("appid") appid:String): AirQuality

}
object WeatherDataApi{

    val retrofitService : RetrofitDataFetch  by lazy {
        retrofit.create(RetrofitDataFetch::class.java) }



}