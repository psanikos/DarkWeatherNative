package npsprojects.darkweather

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.util.CollectionUtils.listOf
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import okio.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.round
import com.google.gson.annotations.SerializedName

import npsprojects.darkweather.MyApp.context
import java.lang.StringBuilder
import java.lang.reflect.Array


const val openWeatherKey = "e1e45feaea76d66517c25291f2633d9a"

class LocationGeocoding : ArrayList<LocationGeocodingItem>()

data class LocationGeocodingItem(
    @SerializedName("country")
    val country: String? = "",
    @SerializedName("lat")
    val lat: Double? = 0.0,
    @SerializedName("local_names")
    val localNames: LocalNames? = LocalNames(),
    @SerializedName("lon")
    val lon: Double? = 0.0,
    @SerializedName("name")
    val name: String? = "",
    @SerializedName("state")
    val state: String? = ""
)

data class LocalNames(
    @SerializedName("ascii")
    val ascii: String? = "",
    @SerializedName("ca")
    val ca: String? = "",
    @SerializedName("en")
    val en: String? = "",
    @SerializedName("feature_name")
    val featureName: String? = ""
)

class WeatherModel constructor(
    val name: String,
    val data: WeatherResponse,
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
    val list: List<DetailsList>? = listOf()
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


fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}

class WeatherViewModel : ViewModel() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locator = LocationHelper()
    var isLoading by mutableStateOf(true)
    var currentLocation: Location? by mutableStateOf(null)
    var currentLocationData: WeatherResponse? by mutableStateOf(null)
    var currentLocationName: String by mutableStateOf("My Location")
    var locations: List<WeatherModel> by mutableStateOf(listOf())
    var units: WeatherUnits by mutableStateOf(WeatherUnits.AUTO)
    var searchedAdresses: MutableList<Address> by mutableStateOf(mutableListOf())
    var myLocations: List<SavedLocation> by mutableStateOf(listOf())
    var error: WeatherError by mutableStateOf(WeatherError.NONE)
    var hasInit: Boolean by mutableStateOf(false)
    private val _permissionGranted = MutableLiveData(false)
    val permissionGranted = _permissionGranted

fun airApiCallString(lat:Double,lon:Double):String = "https://api.openweathermap.org/data/2.5/air_pollution?lat=$lat&lon=$lon&appid=$openWeatherKey"

    fun onPermissionGranted() = _permissionGranted.postValue(true)



    init {
        isInit()
    }

fun getAirDataFromCoordinates(lat:Double,lon:Double,completion:(AirQuality?)->Unit){
    DataFetcher.getFromUrl(url = airApiCallString(lat = lat,lon = lon)) {
        if (it != null) {
            println("DATAAA $it")
            val gson = Gson()

            val out =
                gson.fromJson<AirQuality>(it.toString(), AirQuality::class.java)

            completion(out)
        } else {

            completion(null)
        }
    }

}


    fun hasRun() {
        val context = MyApp.context

        val pref: SharedPreferences = context
            .getSharedPreferences("MyPref", 0)
        with(pref.edit()) {
            putBoolean("init", true)
            apply()
        }
        hasInit = true
    }

    fun isInit() {
        val context = MyApp.context

        val pref: SharedPreferences = context
            .getSharedPreferences("MyPref", 0)

        val initStatus = pref.getBoolean("init", false)
        hasInit = initStatus
    }

    fun askPermission() {
        val permissionFineLocation = android.Manifest.permission.ACCESS_FINE_LOCATION
        val permissionCoarseLocation = android.Manifest.permission.ACCESS_COARSE_LOCATION
        val context = MyApp.context
        val REQUEST_CODE_LOCATION = 100

        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                GlobalScope.launch {
                    hasRun()
                }
                getCurrentLocationWeather()


            }
            else -> {
                requestPermissions(
                    MyApp.activity,
                    arrayOf(permissionFineLocation, permissionCoarseLocation),
                    REQUEST_CODE_LOCATION
                )
            }
        }


    }

    fun askContinueWithout() {
        error = WeatherError.NONE
        isLoading = false
    }

    fun getCoordinatesFromLocation(input: String,completion:(Boolean)->Unit) {
        if (input != "" && isOnline()) {
            searchedAdresses.clear()
            val context = MyApp.context
            val geocoder = Geocoder(context, Locale.getDefault())
            try {
                val addressList = geocoder.getFromLocationName(input,3)
                if (addressList != null && addressList.size > 0) {
                searchedAdresses = addressList
                    if (searchedAdresses.size == 0){
                        completion(false)
                    }
                    else {
                        completion(true)
                    }
                }
            } catch (e: IOException) {
              completion(false)
            }
            println("searching ${input}")
            val url = "https://api.openweathermap.org/geo/1.0/direct?q=$input&limit=3&appid=$openWeatherKey"

//            val geocoder: Geocoder = Geocoder(context, Locale.getDefault())
//            val addresses = geocoder.getFromLocationName(input, 3)
//            searchedAdresses = addresses
//            println("got ${searchedAdresses.size}")
        }
    }

    private fun getNameFromCoordinates(
        context: Context,
        latitude: Double,
        longitude: Double,
        completion: (String) -> Unit
    ) {
        var addresses: List<Address>
        val myContext = MyApp.context
        val geocoder = Geocoder(myContext, Locale.getDefault())
        if (isOnline()) {
            try {
                addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    1
                )
            } catch (error:IOException ) {
              println(error.message)
                addresses = listOf()
            }
//        val address: String =
//            addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

            if (addresses.isNotEmpty()) {
                val city: String? = addresses[0].locality ?: addresses[0].featureName ?: addresses[0].subLocality
//        val state: String = addresses[0].getAdminArea()
//        val country: String = addresses[0].getCountryName()
//        val postalCode: String = addresses[0].getPostalCode()
//        val knownName: String = addresses[0].getFeatureName() //
                completion(city ?: "")
            } else {
                completion("")
            }
        }
        else completion("")
    }

    fun remove(item: SavedLocation) {
        isLoading = true
        //----List of locations----
        var items = locations.toMutableList()
        var places = myLocations.toMutableList()
        places.removeIf { it.name == item.name }
        myLocations = places.toList()
        items.removeIf { it.name == item.name }
        locations = items.toList()
        //---------------------------

        val context = MyApp.context
        val pref: SharedPreferences = context
            .getSharedPreferences("MyPref", 0) // 0 - for private mode
        val savedLocations = pref.getString("locations", null)
        println("SAVED $savedLocations ")
        val itemsSaved =
            savedLocations?.trimStart()?.trimEnd()?.split(",")?.map {
                it
            }?.toMutableList()
                ?: mutableListOf<String>()

        var savedItems = if (itemsSaved.size > 0) itemsSaved.map { saved ->
            saved.replace("[", "").replace("]", "")
            val list = saved.split("|")
            SavedLocation(
                name = list[0].replace("[", "").replace("]", "").replace("|", ""),
                latitude = list[1].replace("[", "").replace("]", "").replace("]", "")
                    .replace("|", "").toDouble(),
                longitude = list[2].replace("[", "").replace("]", "").replace("]", "")
                    .replace("|", "").toDouble()
            )
        }.toMutableList() else mutableListOf<SavedLocation>()

        savedItems.removeIf { it.name == item.name  }

        with(pref.edit()) {
            putString("locations", savedItems.toString())
            apply()
        }

        getSavedLocations() {}
        isLoading = false
    }

    fun getCurrentLocationWeather() {
        val context = MyApp.context
        isLoading = true

        getDataFromUserDefaults()
        println("LOADING DATA ")
        if(isOnline()) {
            getCurrentLocation {
                if (it != null) {
                    currentLocation = it
                    getNameFromCoordinates(
                        context = context,
                        latitude = it.latitude,
                        longitude = it.longitude
                    ) {
                        currentLocationName = it
                        getCurrentLocationData() {
                            getSavedLocations() {
                                getSavedLocationData() {
                                    GlobalScope.launch {
                                        delay(2000)
                                        println("loading complete")
                                        error = WeatherError.NONE
                                        isLoading = false
                                    }
                                }
                            }

                        }
                    }
                } else {
                    getSavedLocations() {
                        getSavedLocationData() {
                            if(ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED) {
                                error = WeatherError.NOGPS
                            }
                            else {
                                error = WeatherError.NOPERMISSION
                            }
                            GlobalScope.launch {
                                delay(3000)
                                println("loading complete")

                                isLoading = false
                            }
                        }
                    }
                }
            }
        }
        else {
            error = WeatherError.NONETWORK
            GlobalScope.launch {
                delay(3000)
                isLoading = false
            }
        }
    }

    fun canGetLocation(): Boolean {
        return isLocationEnabled(MyApp.context) // application context
    }

    fun isLocationEnabled(context: Context): Boolean {
        var locationMode = 0
        val locationProviders: String
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode =
                    Settings.Secure.getInt(context.contentResolver, Settings.Secure.LOCATION_MODE)
            } catch (e: Settings.SettingNotFoundException) {
                e.printStackTrace()
            }
            locationMode != Settings.Secure.LOCATION_MODE_OFF
        } else {
            locationProviders =
                Settings.Secure.getString(context.contentResolver, Settings.Secure.LOCATION_MODE)
            !TextUtils.isEmpty(locationProviders)
        }
    }

    private fun getCurrentLocation(completion: (Location?) -> Unit) {
        val context = MyApp.context
        val permissionFineLocation = android.Manifest.permission.ACCESS_FINE_LOCATION
        val permissionCoarseLocation = android.Manifest.permission.ACCESS_COARSE_LOCATION

        val REQUEST_CODE_LOCATION = 100
        if (canGetLocation() && isOnline()) {

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            //       fusedLocationClient.
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
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        completion(location)
                    } else {
                        println("GOT NULL LOCATION ")
                        locator.startListeningUserLocation(
                            context,
                            object : LocationHelper.MyLocationListener {
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

        } else {
            error = WeatherError.NOGPS
            println("NO LOCATION GOT")
            completion(null)
        }
    }

    private fun getDataFromUserDefaults() {
        val context = MyApp.context

        val pref: SharedPreferences = context
            .getSharedPreferences("MyPref", 0) // 0 - for private mode

        val savedUnit = pref.getString("unit", null)
        if (savedUnit != null) {
            units =
                if (savedUnit == "si") WeatherUnits.SI else if (savedUnit == "us") WeatherUnits.US else WeatherUnits.AUTO
        }
    }

    fun saveUnit(inputUnit: WeatherUnits) {
        val context = MyApp.context
        val unit =
            if (inputUnit == WeatherUnits.AUTO) "auto" else if (inputUnit == WeatherUnits.SI) "si" else "us"
        val pref: SharedPreferences = context
            .getSharedPreferences("MyPref", 0) // 0 - for private mode
        with(pref.edit()) {
            putString("unit", unit)
            apply()
        }

        units = inputUnit

    }

    fun saveLocation(address: SavedLocation) {
        val context = MyApp.context
        val pref: SharedPreferences = context
            .getSharedPreferences("MyPref", 0) // 0 - for private mode
        val savedLocations = pref.getString("locations", null)
        println("SAVED $savedLocations ")
        val items = if (savedLocations != null && savedLocations != "[]")
            savedLocations.trimStart().trimEnd().split(",").map {
                it
            }.toMutableList()
        else mutableListOf<String>()

        val itemToAdd = address.toString()
        if (!items.contains(itemToAdd)) {
            items.add(itemToAdd)
        }

        with(pref.edit()) {
            putString("locations", items.toString())
            apply()
        }

        getSavedLocations() {
            //  getCurrentLocationWeather()
        }
    }


    private fun getSavedLocations(completion: () -> Unit) {
        val context = MyApp.context

        val pref: SharedPreferences = context
            .getSharedPreferences("MyPref", 0) // 0 - for private mode
        val savedLocations = pref.getString("locations", null)
        println("SAVED $savedLocations ")
        val items = if (savedLocations != null && savedLocations != "[]")
            savedLocations.trimStart().trimEnd().split(",").map {
                it
            }.toMutableList()
        else mutableListOf<String>()
        val savedItems = mutableListOf<SavedLocation>()
        if (items.size > 0) {
            items.forEach { saved ->
                saved.replace("[", "").replace("]", "")
                val list = saved.split("|")
                val output = SavedLocation(
                    name = list[0].replace("[", "").replace("]", "").replace("|", ""),
                    latitude = list[1].replace("[", "").replace("]", "").replace("]", "")
                        .replace("|", "").toDouble(),
                    longitude = list[2].replace("[", "").replace("]", "").replace("]", "")
                        .replace("|", "").toDouble()
                )
                if (!savedItems.contains(output)) {
                    savedItems.add(output)
                }
            }
        }
        myLocations = savedItems.toList()
        completion()
    }

    private fun getCurrentLocationData(completion: () -> Unit) {
        val unit =
            if (units == WeatherUnits.AUTO) "auto" else if (units == WeatherUnits.SI) "si" else "us"
        if (currentLocation != null && isOnline()) {

            val locale = context.resources.configuration.locales

            val language: String = if(locale.isEmpty) "en" else locale[0].language
            val searchLanguage = if (language == "el") "el" else "en"
            DataFetcher.getFromUrl(url = "https://api.darksky.net/forecast/0b2f0e7f415678b66d4918b96d6672fa/${currentLocation!!.latitude},${currentLocation!!.longitude}?lang=$searchLanguage&units=$unit") {
                if (it != null) {
                    println("DATAAA $it")
                    val gson = Gson()


                    val out =  try {gson.fromJson<WeatherResponse>(it.toString(), WeatherResponse::class.java) } catch (e:IOException){ null }
                    currentLocationData = out
                    if (currentLocationData != null) {
                 getAirDataFromCoordinates(lat = currentLocation!!.latitude,lon = currentLocation!!.longitude) {
                        val current = WeatherModel(
                            name = currentLocationName,
                            data = currentLocationData!!,
                            isCurrent = true,
                            airQuality = it
                        )
                     locations = listOf(current)
                     completion()
                 }


                    }
                } else {
                    error = WeatherError.NONETWORK
                    completion()
                }
            }
        } else {
            completion()
        }

    }

  fun getDataFromCoordinates(
        latitude: Double,
        longitude: Double,
        name: String,
        completion: (WeatherModel?) -> Unit
    ) {
      val unit =
          if (units == WeatherUnits.AUTO) "auto" else if (units == WeatherUnits.SI) "si" else "us"
      if (isOnline()) {
          val locale = context.resources.configuration.locales

          val language: String = if(locale.isEmpty) "en" else locale[0].language
          val searchLanguage = if (language == "el") "el" else "en"
          DataFetcher.getFromUrl(url = "https://api.darksky.net/forecast/0b2f0e7f415678b66d4918b96d6672fa/${latitude},${longitude}?lang=$searchLanguage&units=$unit") { it ->
              if (it != null) {
                  println("DATAAA $it")
                  val gson = Gson()

                  val out =
                      gson.fromJson<WeatherResponse>(it.toString(), WeatherResponse::class.java)

                  if (out != null) {
              getAirDataFromCoordinates(lat = latitude,lon = longitude){ air ->
                  val current = WeatherModel(
                      name = name,
                      data = out,
                      isCurrent = false,
                      airQuality = air
                  )
                  completion(current)
              }


                  }
              } else {
                  completion(null)
              }
          }
      }
  }

    private fun getSavedLocationData(completion: () -> Unit) {
        var out = arrayListOf<WeatherModel>()
        println("SAVED LOCATIONS ${myLocations.count()}")

        locations =
            if (locations.isNotEmpty() && currentLocationData != null) listOf(locations[0]) else listOf()
        for (item in myLocations) {
            println("GETTING FROM SAVED")
            getDataFromCoordinates(
                latitude = item.latitude,
                longitude = item.longitude,
                name = item.name
            ) {
                println("COMPLETION FROM SAVED")
                it?.let { data ->

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

                        completion(null)
                        throw   IOException("Unexpected code $response")
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
