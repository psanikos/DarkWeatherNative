package com.npsappprojects.darkweather

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import com.beust.klaxon.Klaxon
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.CacheControl
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import java.util.*
import java.util.concurrent.TimeUnit


class WeatherViewModel: ViewModel() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locator = LocationHelper()
    var isLoading by mutableStateOf(false)
    var currentLocation: Location? by  mutableStateOf(null)
    var currentLocationData: WeatherResponse? by  mutableStateOf(null)
    var currentLocationName:String by mutableStateOf("My Location")

fun getLastLocation(context: Context){
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        GlobalScope.launch {
            delay(2000)
            println("loading complete")
            isLoading = false
        }
        return
    }
    else {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
             if (location != null){
                currentLocation = location
                getCurrentLocationData()
                getNameFromCoordinates(context = context,latitude = location.latitude,longitude = location.longitude){
                    currentLocationName = it
                }
                GlobalScope.launch {
                    delay(2000)
                    println("loading complete")
                    isLoading = false
                }
            }else {
                 GlobalScope.launch {
                     delay(2000)
                     println("loading complete")
                     isLoading = false
                 }
            }
            }
    }

}

    fun getNameFromCoordinates(context: Context,latitude:Double,longitude:Double,completion:(String)->Unit){
        val addresses: List<Address>?
        val geocoder: Geocoder = Geocoder(context, Locale.getDefault())

        addresses = geocoder.getFromLocation(
            latitude,
            longitude,
            1
        )


//        val address: String =
//            addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

        if (addresses != null && addresses[0] != null){
            val city: String = addresses[0].locality
//        val state: String = addresses[0].getAdminArea()
//        val country: String = addresses[0].getCountryName()
//        val postalCode: String = addresses[0].getPostalCode()
//        val knownName: String = addresses[0].getFeatureName() //
            completion(city)
        }
          else {
              completion("My location")
          }
    }


 fun getCurrentLocation(context: Context){
     isLoading = true
     println("LOADING DATA ")
    locator.startListeningUserLocation(context, object : LocationHelper.MyLocationListener {
        override fun onLocationChanged(location: Location) {
          if (location == null) {
                getLastLocation(context = context)

          }
            else {
              println("GOT LOCATION ")
              currentLocation = location
              getCurrentLocationData()
              println("GOT DATA ")
              getNameFromCoordinates(context = context,latitude = location.latitude,longitude = location.longitude){
                  currentLocationName = it
              }
              println("GOT NAME ")
              GlobalScope.launch {
                  delay(2000)
                  println("loading complete")
                  isLoading = false
              }
          }

        }
    })
}


    fun getCurrentLocationData() {

        if (currentLocation != null) {
            DataFetcher.getFromUrl(url = "https://api.darksky.net/forecast/0b2f0e7f415678b66d4918b96d6672fa/${currentLocation!!.latitude},${currentLocation!!.longitude}?lang=en&units=si"){
             if(it != null){
                 println("DATAAA $it")
                 val klaxon = Klaxon()

                 val out = klaxon.parse<WeatherResponse>(it.toString())
                 currentLocationData = out

            }
            }
        } else {
            GlobalScope.launch {
                delay(2000)
                println("loading complete")
                isLoading = false
            }
        }

    }

}



const val RANDOMURL = "https://api.spoonacular.com/recipes/random?number=8&apiKey=2c920a6d86764c1d86ad2605bb2b97d2"

//'https://api.darksky.net/forecast/0b2f0e7f415678b66d4918b96d6672fa/${position
//        .latitude},${position.longitude}?lang=$language&units=$units'

object DataFetcher {

    private val client = OkHttpClient()
    private val cacheControl by lazy {
        CacheControl.Builder().maxStale(8, TimeUnit.HOURS).build()
    }


    fun getFromUrl(url: String, completion: (String?) -> Unit) {
        val request = Request.Builder()
            .url(url)
            .cacheControl(cacheControl)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("FAILED GETTING DATA")
                e.printStackTrace()
                completion(null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        println("RESPONSE UNSUCCESSFUL")
                        throw   IOException("Unexpected code $response")
                        completion(null)
                    }

                    if (response.body != null) {
                        println("GOT RESPONSE")
                        completion(response.body!!.string())
                    } else {
                        println("RESPONSE NULL")
                        completion(null)
                    }
                }
            }
        })

    }
}