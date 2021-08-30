package npsprojects.darkweather.models

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
import androidx.compose.runtime.mutableStateListOf
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import okio.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.round
import com.google.gson.annotations.SerializedName
import npsprojects.darkweather.*

import npsprojects.darkweather.MyApp.context
import npsprojects.darkweather.services.DataFetcher
import npsprojects.darkweather.services.LocationHelper

class WeatherViewModel : ViewModel() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locator = LocationHelper()
    var locations = mutableStateListOf<WeatherModel>()
    var loading: Boolean by mutableStateOf(false)
    var units: WeatherUnits by mutableStateOf(WeatherUnits.AUTO)
    var myLocations: List<SavedLocation> by mutableStateOf(listOf())
    var error =  mutableStateOf(WeatherError.NONE)
    var hasInit: Boolean by mutableStateOf(false)
    private val _permissionGranted = MutableLiveData(false)
    val permissionGranted = _permissionGranted
    var index by mutableStateOf(0)

    private fun airApiCallString(lat:Double, lon:Double):String = "https://api.openweathermap.org/data/2.5/air_pollution?lat=$lat&lon=$lon&appid=$openWeatherKey"

    fun onPermissionGranted() = _permissionGranted.postValue(true)



    init {
        initActions()
    }



    fun initActions(){
        loading = true
        isInit()
        getDataFromUserDefaults()
        getUserLocationData {
             getSavedLocationsData(){
                viewModelScope.launch {
                    delay(2000)
                    loading = false
                }
             }
        }
    }

    // Initialization actions-------------
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
    private fun isInit() {
        val context = MyApp.context

        val pref: SharedPreferences = context
            .getSharedPreferences("MyPref", 0)

        val initStatus = pref.getBoolean("init", false)
        hasInit = initStatus
    }
    //------------------------------------

    // Get and save user data-------------
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

        myLocations = getSavedLocations()
    }
    private fun getSavedLocations():MutableList<SavedLocation> {
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

        return savedItems
    }

    fun remove(item: SavedLocation) {
        //----List of locations----

        locations.removeIf { it.name == item.name }
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

        val savedItems = if (itemsSaved.size > 0) itemsSaved.map { saved ->
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

       myLocations = getSavedLocations()

    }
   //-------------------------------------


    //Permissions and actions--------------------------
    fun askPermission(completion:(Boolean)->Unit) {
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
                viewModelScope.launch {
                    hasRun()
                }
                completion(true)


            }
            else -> {
                requestPermissions(
                    MyApp.activity,
                    arrayOf(permissionFineLocation, permissionCoarseLocation),
                    REQUEST_CODE_LOCATION
                )
                if(ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED){
                    completion(true)
                }
                else {
                    completion(false)
                }
            }
        }


    }
    fun askContinueWithout() {
        error.value = WeatherError.NONE
    }
    private fun canGetLocation(): Boolean {
        return isLocationEnabled(MyApp.context) // application context
    }
    private fun isLocationEnabled(context: Context): Boolean {
        var locationMode = 0
        val locationProviders: String

        try {
            locationMode =
                Settings.Secure.getInt(context.contentResolver, Settings.Secure.LOCATION_MODE)
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
        }
        return locationMode != Settings.Secure.LOCATION_MODE_OFF

    }
    //------------------------------------

   //Get current location and data--------------------

    private fun getUserLocationData(completion: (Boolean) -> Unit){
        if (isOnline() && canGetLocation()){
            askPermission { permission ->
                if(permission) {
                    getUserLocation { locale ->
                        if (locale == null) {
                            error.value = WeatherError.NOGPS
                        }
                        locale?.let {
                            getLocationWeather(location = locale)
                        }

                    }
                }
                else {
                    error.value = WeatherError.NOPERMISSION
                }
            }
            completion(true)
        }
        else {
            error.value = WeatherError.NONETWORK
            completion(false)
        }
    }
    private fun getUserLocation(completion: (Location?) -> Unit) {
        val context = MyApp.context
        val permissionFineLocation = android.Manifest.permission.ACCESS_FINE_LOCATION
        val permissionCoarseLocation = android.Manifest.permission.ACCESS_COARSE_LOCATION

        val REQUEST_CODE_LOCATION = 100

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

            //requestPermissions(MyApp.activity, arrayOf(permissionFineLocation, permissionCoarseLocation), REQUEST_CODE_LOCATION)
            completion(null)

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

                completion(null)
            }


    }
    private fun getLocationWeather(location:Location) {
        val context = MyApp.context

        getNameFromCoordinates(
            context = context,
            latitude = location.latitude,
            longitude = location.longitude
        ) {

            getLocationData(location = Coordinates(location.latitude,location.longitude),name = it,isCurrent = true){ weather->
                weather?.let {
                        locations.add(0, weather)
                }
            }
        }

    }
   //-------------------------------------------------


    //Geolocate--------------------------------------
    fun getCoordinatesFromLocation(input: String):MutableList<Address> {
    if (input != "" && isOnline()) {

        val context = MyApp.context
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addressList = geocoder.getFromLocationName(input,3)
            if (addressList != null && addressList.size > 0) {

               return addressList

            }
        } catch (e: IOException) {
            return mutableListOf()
        }
    }
        return mutableListOf()
}
    private fun getNameFromCoordinates(context: Context, latitude: Double, longitude: Double, completion: (String) -> Unit) {
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
    //----------------------------------------------

    //Saved locations data---------------------------
    private fun getSavedLocationsData(completion:()->Unit){
      val locations = getSavedLocations()
        locations.forEach{
              getCoordinatesWeather(Coordinates(it.latitude,it.longitude))
          }
      myLocations = locations
        completion()
    }
    fun getCoordinatesWeather(location:Coordinates) {
        val context = MyApp.context

        getNameFromCoordinates(
            context = context,
            latitude = location.latitude,
            longitude = location.longitude
        ) {
            val currentLocationName = it
            getLocationData(location = location,isCurrent = false,name = currentLocationName){ weather->
                weather?.let {
                    locations.add(weather)
                }
            }
        }

    }
    //-----------------------------------------------


    private fun getAirDataFromCoordinates(lat:Double, lon:Double, completion:(AirQuality?)->Unit){
    DataFetcher.getFromUrl(url = airApiCallString(lat = lat, lon = lon)) {
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
    private fun getLocationData(location:Coordinates,isCurrent: Boolean,name:String,completion: (WeatherModel?) -> Unit) {
        val unit =
            if (units == WeatherUnits.AUTO) "auto" else if (units == WeatherUnits.SI) "si" else "us"
            val locale = context.resources.configuration.locales
            val language: String = if(locale.isEmpty) "en" else locale[0].language
            val searchLanguage = if (language == "el")  "el" else if (language == "fr") "fr" else "en"
            DataFetcher.getFromUrl(url = getOpenWeatherUrl(location,units)) { it ->
                if (it != null) {
                    println("DATAAA $it")
                    val gson = Gson()

                    val currentLocationData = try {
                        gson.fromJson<OpenWeather>(it.toString(), OpenWeather::class.java)
                    } catch (e: IOException) {
                        null
                    }

                    if (currentLocationData != null) {
                        getAirDataFromCoordinates(
                            lat = location.latitude,
                            lon = location.longitude
                        ) { air->
                            val current = WeatherModel(
                                name = name,
                                data = currentLocationData,
                                isCurrent = isCurrent,
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


