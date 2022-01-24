package npsprojects.darkweather.models

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.provider.Settings
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.birjuvachhani.locus.*
import com.google.android.gms.common.util.CollectionUtils.listOf
import kotlinx.coroutines.*
import npsprojects.darkweather.WeatherUnits
import npsprojects.darkweather.isOnline
import npsprojects.darkweather.openWeatherKey
import npsprojects.darkweather.services.LocationsDatabase
import npsprojects.darkweather.services.SavedLocation
import npsprojects.darkweather.services.WeatherDataApi
import okio.IOException
import java.util.*


enum class Lang {
    EN,EL,FR
}
class LOCATIONERROR:Exception(){
    object LOCATION_DENIED:Exception("Location denied")
    object LOCATION_FULL_DENIED:Exception("Enable location from settings")
    object LOCATION_NOT_ASKED:Exception("Location not approved")
    object LOCATION_FATAL:Exception("Fatal error")
}


class WeatherViewModel : ViewModel() {

    companion object LocationFetcher{

        fun getUserLocation(context: Context,completion: (Location?, Exception?) -> Unit){
            val permissionFineLocation = android.Manifest.permission.ACCESS_FINE_LOCATION
            val permissionCoarseLocation = android.Manifest.permission.ACCESS_COARSE_LOCATION
            val REQUEST_CODE_LOCATION = 100

            if(isLocationEnabled(context = context)) {
                getCurrentLocation(context) { loc, error ->
                    if (error != null) {
                        requestPermissions(
                            context as Activity,
                            arrayOf(permissionFineLocation, permissionCoarseLocation),
                            REQUEST_CODE_LOCATION
                        )
                        completion(null,error)
                    } else {
                        completion(loc,null)
                    }
                }
            }
            else {
                completion(null,LOCATIONERROR.LOCATION_FATAL)
            }
        }

        private fun getCurrentLocation(context: Context, completion: (Location?, Exception?) -> Unit){
            Locus.getCurrentLocation(context = context) { result ->
                result.location?.let {
                completion(it,null)
                }
                result.error?.let { error->
                    when {
                        error.isDenied -> { completion(null, LOCATIONERROR.LOCATION_DENIED)}
                        error.isPermanentlyDenied -> { completion(null, LOCATIONERROR.LOCATION_FULL_DENIED) }
                        error.isFatal -> { completion(null, LOCATIONERROR.LOCATION_FATAL)}
                        error.isSettingsDenied -> {completion(null, LOCATIONERROR.LOCATION_FULL_DENIED) }
                        error.isSettingsResolutionFailed -> { completion(null, LOCATIONERROR.LOCATION_NOT_ASKED)}
                    }
                }
            }
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


        //Geolocation--------------------------------------

        fun getCoordinatesFromLocation(context: Context,input: String):MutableList<Address> {
            if (input != "" && isOnline()) {

                val geocoder = Geocoder(context, Locale.getDefault())
                println("Geocoding")
                try {
                    val addressList = geocoder.getFromLocationName(input, 3)
                    if (addressList != null && addressList.size > 0) {
                        println(addressList.first().countryName)
                        return addressList

                    }
                } catch (e: IOException) {
                    println("error" + e.localizedMessage)
                    return mutableListOf()
                }
            }
            println("Not online or empty")
            return mutableListOf()
        }

        private fun getNameFromCoordinates(myContext: Context, latitude: Double, longitude: Double, completion: (String) -> Unit) {
            val addresses: List<Address>

            val geocoder = Geocoder(myContext, Locale.getDefault())
            if (isOnline()) {
                addresses = try {
                    geocoder.getFromLocation(
                        latitude,
                        longitude,
                        1
                    )
                } catch (error:IOException ) {
                    println("Error" + error.message)
                    listOf()
                }
//        val address: String =
//            addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                if (addresses.isNotEmpty()) {
                    Log.d("Addresses ", addresses.toString())
                    val city: String? =  addresses[0].subAdminArea ?: addresses[0].locality  ?: addresses[0].subLocality ?: addresses[0].countryName
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
    }


    var currentLocation = MutableLiveData<List<WeatherModel>>(kotlin.collections.listOf())

    var isLoading = MutableLiveData(false)
    private val _locations = MutableLiveData(listOf<WeatherModel>())
    val locations: LiveData<List<WeatherModel>> = _locations


    var units: WeatherUnits by mutableStateOf(WeatherUnits.AUTO)

    var myLocations: List<SavedLocation> by mutableStateOf(listOf())


    private val _index =  MutableLiveData(0)
    val index:LiveData<Int> = _index

    private var lang by mutableStateOf(Lang.EN)


    fun changeIndex(value:Int){
        _index.value = value
    }

//Device settings-----------------
    private fun getLang(){
    val locale = Locale.getDefault().displayLanguage
    lang = when(locale.lowercase()){
        "french" -> Lang.FR
        "greek" -> Lang.EL
        else -> Lang.EN
    }
}

    private fun saveWidgetLocation(address: SavedLocation,context: Context) {
        val pref: SharedPreferences = context
            .getSharedPreferences("MyPref", 0)

        with(pref.edit()) {
            putString("widgetLocation", "${address.name},${address.latitude},${address.longitude}")
            apply()
        }
        Log.i("Widget","Saved widget location")
    }

    fun saveUnit(inputUnit: WeatherUnits,context: Context) {
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

    private fun getDataFromUserDefaults(context: Context) {


        val pref: SharedPreferences = context
            .getSharedPreferences("MyPref", 0) // 0 - for private mode

        val savedUnit = pref.getString("unit", null)
        if (savedUnit != null) {
            units =
                if (savedUnit == "si") WeatherUnits.SI else if (savedUnit == "us") WeatherUnits.US else WeatherUnits.AUTO
        }
        else {
            units = WeatherUnits.SI
        }
    }

//--------------------------------

    // DAO-------------------------------
   suspend fun saveLocation(address: SavedLocation, context: Context) {

        val db = LocationsDatabase.getInstance(context)
        withContext(Dispatchers.IO) {
            db?.let {
                db.locationsDao().insert(address)
            }
            getSavedLocations(context = context)
        }
    }


    private suspend fun getSavedLocations(context: Context) {
        kotlin.collections.listOf<SavedLocation>()
        val db = LocationsDatabase.getInstance(context)
        db?.let {
            withContext(Dispatchers.IO) {
                val out = with(db.locationsDao()) { getAll() }
                Log.d("dao", out.toString())
                myLocations = out
            }
        }
    }


     suspend fun remove(item: SavedLocation, context: Context) {
        val db = LocationsDatabase.getInstance(context)

            db?.let {
                with(it.locationsDao()) {
                    withContext(Dispatchers.IO) {
                        deleteWhere(latitude = item.latitude, longitude = item.longitude)
                        val out = with(db.locationsDao()) { getAll() }
                        Log.d("dao", out.toString())
                        myLocations = out
                    }
                }
            }

        }


    //-------------------------------------

    //Data fetch--------------------------

    @Throws(LOCATIONERROR::class)
    fun getCurrentLocation(context: Context){
        val unit = if (units == WeatherUnits.AUTO) "metric" else if (units == WeatherUnits.SI) "metric" else "imperial"
        val locale = context.resources.configuration.locales
        val language: String = if(locale.isEmpty) "en" else locale[0].language
        val searchLanguage = if (language == "el")  "el" else if (language == "fr") "fr" else "en"

     LocationFetcher.getUserLocation(context=context){ loc,error ->
         if(error != null){
             throw error
         }
         else{

             viewModelScope.launch {
                val air = try {
                     WeatherDataApi.retrofitService.airData(loc!!.latitude, loc!!.longitude,
                         appid = openWeatherKey)
                 } catch (e: Exception) {
                     null
                 }
                 val data = try {
                     WeatherDataApi.retrofitService.oneCallWeather(
                         lat = loc!!.latitude, lon = loc!!.longitude,
                         unit = unit, lang = searchLanguage,
                         appid = openWeatherKey
                     )

                 } catch (e: Exception) {
                     null
                 }

                 data?.let {
                     LocationFetcher.getNameFromCoordinates(context, latitude = loc!!.latitude, longitude = loc!!.longitude){
                       saveWidgetLocation(address = SavedLocation(
                           name = it,
                           longitude = loc!!.longitude,
                           latitude = loc!!.latitude
                       ), context = context)

                         currentLocation.value = listOf(WeatherModel(
                             location = SavedLocation(
                                 name = it,
                                 longitude = loc!!.longitude,
                                 latitude = loc!!.latitude
                             ),
                             data = data,
                             isCurrent = true,
                             airQuality = air
                         ))
                         var old = _locations.value?.toMutableList() ?: mutableListOf()
                         old.add(0,
                             WeatherModel(
                                 location = SavedLocation(
                                     name = it,
                                     longitude = loc!!.longitude,
                                     latitude = loc!!.latitude
                                 ),
                                 data = data,
                                 isCurrent = true,
                                 airQuality = air
                             ))
                         _locations.value = old.toList()
                     }

                 }

             }
         }
     }

    }

    private fun getSavedLocationData(context: Context){
        val unit = if (units == WeatherUnits.AUTO) "metric" else if (units == WeatherUnits.SI) "metric" else "imperial"
        val locale = context.resources.configuration.locales
        val language: String = if(locale.isEmpty) "en" else locale[0].language
        val searchLanguage = if (language == "el")  "el" else if (language == "fr") "fr" else "en"
        var out = kotlin.collections.mutableListOf<WeatherModel>()
            runBlocking {

                async {
                    myLocations.forEach {

                        val air = try {
                            WeatherDataApi.retrofitService.airData(
                                it!!.latitude, it!!.longitude,
                                appid = openWeatherKey
                            )
                        } catch (e: Exception) {
                            null
                        }
                        val data = try {
                            WeatherDataApi.retrofitService.oneCallWeather(
                                lat = it!!.latitude, lon = it!!.longitude,
                                unit = unit, lang = searchLanguage,
                                appid = openWeatherKey
                            )

                        } catch (e: Exception) {
                            null
                        }
                        if (data == null) {
                            Log.i("Weather data", "data null")

                        }
                        data?.let { dat ->
                            Log.i("Weather data", "Adding out")
                            out.add(
                                WeatherModel(
                                    location = SavedLocation(
                                        name = it.name,
                                        longitude = it!!.longitude,
                                        latitude = it!!.latitude
                                    ),
                                    data = dat,
                                    isCurrent = false,
                                    airQuality = air
                                )
                            )
                        }
                    }
                }.await()
                Log.i("Weather data", out.size.toString() + " locations ${myLocations.size}")
                _locations.value = out.toList()
            }
    }

    fun getSearchedLocationData(latitude: Double,longitude: Double,name:String,context: Context){
        val unit = if (units == WeatherUnits.AUTO) "metric" else if (units == WeatherUnits.SI) "metric" else "imperial"
        val locale = context.resources.configuration.locales
        val language: String = if(locale.isEmpty) "en" else locale[0].language
        val searchLanguage = if (language == "el")  "el" else if (language == "fr") "fr" else "en"

        viewModelScope.launch {
          isLoading.value = true

                val air = try {
                    WeatherDataApi.retrofitService.airData(latitude, longitude,
                        appid = openWeatherKey)
                } catch (e: Exception) {
                    null
                }
                val data = try {
                    WeatherDataApi.retrofitService.oneCallWeather(
                        lat = latitude, lon = longitude,
                        unit = unit, lang = searchLanguage,
                        appid = openWeatherKey
                    )

                } catch (e: Exception) {
                    null
                }

                data?.let { dat->
                    _locations.value = _locations.value!!.plus(WeatherModel(
                        location = SavedLocation(
                            name = name,
                            longitude = longitude,
                            latitude = latitude
                        ),
                        data = dat,
                        isCurrent = false,
                        airQuality = air
                    ))
                }
            isLoading.value = false


        }
    }
    //-----------------------------------


    suspend fun initActions(context: Context){

        viewModelScope.launch {
            isLoading.value = true
            _index.value = 0
            _locations.value = kotlin.collections.listOf()
            myLocations = kotlin.collections.listOf()
            currentLocation.value = kotlin.collections.listOf()

            getSavedLocations(context = context)
            getLang()
            getDataFromUserDefaults(context)
            getCurrentLocation(context = context)
            getSavedLocationData(context = context)

            isLoading.value = false
        }

     }



    //-----------------------------------------------



}


