package com.npsappprojects.darkweather

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
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
import kotlin.math.round

class WeatherModel constructor(
    val name:String,
    val data:WeatherResponse,
    val isCurrent:Boolean
)

class SavedLocation constructor(
    val name:String,
val latitude: Double,
val longitude: Double
) {
    override fun toString(): String {
        return "[$name|${latitude.round(5)}|${longitude.round(5)}]"
    }
}


fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}

class WeatherViewModel: ViewModel() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locator = LocationHelper()
    var isLoading by mutableStateOf(false)
    var currentLocation: Location? by  mutableStateOf(null)
    var currentLocationData: WeatherResponse? by  mutableStateOf(null)
    var currentLocationName:String by mutableStateOf("My Location")
    var locations:List<WeatherModel> by mutableStateOf(listOf())
    var units:WeatherUnits by  mutableStateOf(WeatherUnits.AUTO)
    var searchedAdresses:MutableList<Address> by mutableStateOf(mutableListOf())
    var myLocations:List<SavedLocation> by mutableStateOf(listOf())
    var error:WeatherError by mutableStateOf(WeatherError.NONE)


    fun askPermission() {
        val permissionFineLocation = android.Manifest.permission.ACCESS_FINE_LOCATION
        val permissionCoarseLocation = android.Manifest.permission.ACCESS_COARSE_LOCATION
        val context = MyApp.context
        val REQUEST_CODE_LOCATION = 100

        requestPermissions(
            MyApp.activity,
            arrayOf(permissionFineLocation, permissionCoarseLocation),
            REQUEST_CODE_LOCATION
        )
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocationWeather()
        }

        }


    fun askContinueWithout(){
        error = WeatherError.NONE
        isLoading = false
    }

    fun getCoordinatesFromLocation(input:String){
        if (input != "") {
            searchedAdresses.clear()
        val context = MyApp.context

            val geocoder: Geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocationName(input, 3)
            searchedAdresses = addresses
        }
    }

    private fun getNameFromCoordinates(context: Context, latitude:Double, longitude:Double, completion:(String)->Unit){
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


    fun getCurrentLocationWeather(){
        val context = MyApp.context
        isLoading = true
        getDataFromUserDefaults()
        println("LOADING DATA ")
        getCurrentLocation {
            if(it != null) {
                currentLocation = it
                getNameFromCoordinates(
                    context = context,
                    latitude = it.latitude,
                    longitude = it.longitude
                ) {
                    currentLocationName = it
                    getCurrentLocationData() {
                        getSavedLocations(){
                            getSavedLocationData(){
                                GlobalScope.launch {
                                    delay(2000)
                                    println("loading complete")

                                    isLoading = false
                                }
                            }
                        }

                    }
                }
            }
            else {
                GlobalScope.launch {
                    getSavedLocations(){
                        getSavedLocationData(){
                            GlobalScope.launch {
                                delay(2000)
                                println("loading complete")
                              //  error = WeatherError.NOGPS

                            }
                        }
                    }


                }
            }
        }
    }

    private fun getCurrentLocation(completion: (Location?) -> Unit){
     val context = MyApp.context
      val permissionFineLocation=android.Manifest.permission.ACCESS_FINE_LOCATION
     val permissionCoarseLocation=android.Manifest.permission.ACCESS_COARSE_LOCATION

    val REQUEST_CODE_LOCATION=100


     fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
     // checking location permission
     if (ContextCompat.checkSelfPermission(
             context,
             Manifest.permission.ACCESS_COARSE_LOCATION
         ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
             context,
             Manifest.permission.ACCESS_FINE_LOCATION
         ) != PackageManager.PERMISSION_GRANTED
     ) {
        error = WeatherError.NOPERMISSION
         //requestPermissions(MyApp.activity, arrayOf(permissionFineLocation, permissionCoarseLocation), REQUEST_CODE_LOCATION)
        completion(null)
         return
     }
     fusedLocationClient.lastLocation
         .addOnSuccessListener { location : Location? ->
         if(location != null) {
             completion(location)
         }
             else {
             println("GOT NULL LOCATION ")
             locator.startListeningUserLocation(context, object : LocationHelper.MyLocationListener {
                 override fun onLocationChanged(location: Location) {
                     completion(location)
                 }
             })

             }

         }

         .addOnFailureListener {
             error = WeatherError.NOGPS
             println("NO LOCATION GOT")
         completion(null)
         }


}

    private fun getDataFromUserDefaults(){
    val context = MyApp.context

        val pref: SharedPreferences = context
            .getSharedPreferences("MyPref", 0) // 0 - for private mode

       val savedUnit =  pref.getString("unit", null)
        if (savedUnit != null) {
            units = if(savedUnit == "si" ) WeatherUnits.SI else if (savedUnit == "us") WeatherUnits.US else WeatherUnits.AUTO
        }
    }

    fun saveUnit(inputUnit:WeatherUnits){
        val context = MyApp.context
        val unit = if (inputUnit == WeatherUnits.AUTO) "auto" else if (inputUnit == WeatherUnits.SI) "si" else "us"
        val pref: SharedPreferences = context
            .getSharedPreferences("MyPref", 0) // 0 - for private mode
        with (pref.edit()) {
            putString("unit", unit)
            apply()
        }

            units = inputUnit

    }

    // {name,lat,lon}-{name,lat,lon}

    fun saveLocation(address:Address){
        val context = MyApp.context
        val pref: SharedPreferences = context
            .getSharedPreferences("MyPref", 0) // 0 - for private mode
        val savedLocations =  pref.getString("locations", null)
        println("SAVED $savedLocations ")
        val items = savedLocations?.trimStart()?.trimEnd()?.split(",")?.map {
            it
        }?.toMutableList()
            ?: mutableListOf<String>()

        val itemToAdd = SavedLocation(name = address.locality,latitude = address.latitude,longitude = address.longitude).toString()
       if (!items.contains(itemToAdd)){
           items.add(itemToAdd )
       }

        with (pref.edit()) {
            putString("locations", items.toString())
            apply()
        }

getSavedLocations(){}
    }

    private fun getSavedLocations(completion:()->Unit) {
        val context = MyApp.context
        val pref: SharedPreferences = context
            .getSharedPreferences("MyPref", 0) // 0 - for private mode
        val savedLocations = pref.getString("locations", null)
        println("SAVED $savedLocations ")
        val items =
            savedLocations?.trimStart()?.trimEnd()?.split(",")?.map {
                it
            }?.toMutableList()
                ?: mutableListOf<String>()
        val savedItems = items.map { saved ->
            saved.replace("[", "").replace("]", "")
            val list = saved.split("|")
            SavedLocation(
                name = list[0].replace("[", "").replace("]", "").replace("|", ""),
                latitude = list[1].replace("[", "").replace("]", "").replace("]", "").replace("|", "").toDouble(),
                longitude = list[2].replace("[", "").replace("]", "").replace("]", "").replace("|", "").toDouble()
            )
        }.toList()
        myLocations = savedItems
        completion()
    }
    private fun getCurrentLocationData(completion:()->Unit) {
        val unit = if (units == WeatherUnits.AUTO) "auto" else if (units == WeatherUnits.SI) "si" else "us"
        if (currentLocation != null) {
            DataFetcher.getFromUrl(url = "https://api.darksky.net/forecast/0b2f0e7f415678b66d4918b96d6672fa/${currentLocation!!.latitude},${currentLocation!!.longitude}?lang=en&units=$unit"){
             if(it != null){
                 println("DATAAA $it")
                 val klaxon = Klaxon()

                 val out = klaxon.parse<WeatherResponse>(it.toString())
                 currentLocationData = out
                 if (currentLocationData != null) {
                     val current = WeatherModel(
                         name = currentLocationName,
                         data = currentLocationData!!,
                         isCurrent = true
                     )
                     locations = listOf(current)
                    completion()
                 }
            }
                else {
                    error = WeatherError.NONETWORK
                 completion()
                }
            }
        } else {
           completion()
        }

    }

    private fun getDataFromCoordinates(latitude: Double,longitude: Double,name: String,completion:(WeatherModel?)->Unit){
        val unit =
            if (units == WeatherUnits.AUTO) "auto" else if (units == WeatherUnits.SI) "si" else "us"
        DataFetcher.getFromUrl(url = "https://api.darksky.net/forecast/0b2f0e7f415678b66d4918b96d6672fa/${latitude},${longitude}?lang=en&units=$unit") {
            if (it != null) {
                println("DATAAA $it")
                val klaxon = Klaxon()

                val out = klaxon.parse<WeatherResponse>(it.toString())

                if (out != null) {
                    val current = WeatherModel(
                        name = name,
                        data = out,
                        isCurrent = false
                    )

                    completion(current)
                }
            }
            else {
                completion(null)
            }
        }
    }

    private fun getSavedLocationData(completion:()->Unit) {
       var out = arrayListOf<WeatherModel>()
        println("SAVED LOCATIONS ${myLocations.count()}")
        for ( item in myLocations) {
            println("GETTING FROM SAVED")
          getDataFromCoordinates(latitude = item.latitude,longitude = item.longitude,name = item.name){
              println("COMPLETION FROM SAVED")
              it?.let { data->
                  locations = locations + data
              }
          }

        }
//        if (locations.count() > 0) {
//            out.add(0, locations.first())
//        }
       println("TOTALLY GOT ${locations.count()} PLACES")
//            locations = out.toList()
        completion()
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