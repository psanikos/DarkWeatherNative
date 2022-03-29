package npsprojects.darkweather.widgets
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Range
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.*
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.*
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.unit.ColorProvider

import androidx.glance.layout.*
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.*
import androidx.glance.unit.ColorProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import npsprojects.darkweather.*
import npsprojects.darkweather.R
import npsprojects.darkweather.models.*
import npsprojects.darkweather.services.WeatherDataApi
import npsprojects.darkweather.ui.theme.*
import java.io.IOException
import java.util.*
import kotlin.math.log
import kotlin.math.roundToInt


// val checked = prefs[booleanPreferencesKey(keyOfCheckedState)] ?: false
//GlanceModifier.clickable(actionRunCallback<UpdateAction>())
//  .clickable(actionStartActivity<MainActivity>())


private val curTemp = intPreferencesKey("curTemp")
private val minTemp = intPreferencesKey("minTemp")
private val maxTemp = intPreferencesKey("maxTemp")
private val feelsTemp = intPreferencesKey("feelsTemp")
private val curImage= stringPreferencesKey("curImage")
private val curDescription = stringPreferencesKey("curDescription")
private val back = intPreferencesKey("back")
private val name = stringPreferencesKey("name")
@OptIn(ExperimentalFoundationApi::class,
    androidx.compose.animation.ExperimentalAnimationApi::class,
    androidx.compose.material.ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class
)
class WeatherWidget : GlanceAppWidget() {


    override var stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override val sizeMode: SizeMode = SizeMode.Single




    @Composable
    override fun Content() {
       val prefs = currentState<Preferences>()
        val context = LocalContext.current
        val size = LocalSize.current
//
//        var color:ColorProvider = ColorProvider(day = Color.White, night = Color.Black)
//        var textColor:ColorProvider = ColorProvider(day = Color.Black, night = Color.White)
//        var secondaryTextColor:ColorProvider = ColorProvider(day = Color.DarkGray, night = Color.Gray)
//        var borderColor:ColorProvider = ColorProvider(day = Color.Black, night = Color.White)

        MediumBox()
//        when (LocalSize.current) {
//            EXTRA_SMALL_BOX-> ExtraSmallBox()
//            COLUMN -> SmallBox()
//            ROW -> RowWidget()
//            LARGE_ROW -> RowWidget()
//            SMALL_BOX -> MediumBox()
//            BIG_BOX -> LargeBox()
//            else -> throw IllegalArgumentException("Invalid size not matching the provided ones")
//        }




    }
    companion object {
        private val EXTRA_SMALL_BOX = DpSize(48.dp, 48.dp)
        private val SMALL_BOX = DpSize(80.dp, 100.dp)
        private val BIG_BOX = DpSize(160.dp, 200.dp)
        private val ROW = DpSize(180.dp, 48.dp)
        private val LARGE_ROW = DpSize(300.dp, 48.dp)
        private val COLUMN = DpSize(48.dp, 180.dp)

    }

}


@OptIn(ExperimentalFoundationApi::class, androidx.compose.animation.ExperimentalAnimationApi::class,
    androidx.compose.material.ExperimentalMaterialApi::class
)
@Composable
fun MediumBox(){
    val prefs = currentState<Preferences>()
    val context = LocalContext.current
    val back:Long = prefs[back]?.toLong() ?: 0xFFFFFFFF

    fun initModifier():GlanceModifier{

       return if(Build.VERSION.SDK_INT > 30) {
            GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(day = Color(back), night = Color(back)))
                .cornerRadius(22.dp)
        }
        else{
            GlanceModifier
                .fillMaxSize().background(ImageProvider(R.drawable.mywidgetbackground))
        }
    }

    Column(
        modifier = initModifier()
            .appWidgetBackground()
            .padding(16.dp)
            .clickable(actionStartActivity<MainActivity>()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(modifier = GlanceModifier.fillMaxWidth()) {
            Text(
                text = ((prefs[curTemp] ?: 0).toString() + "°"),
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 50.sp,
                    color = ColorProvider(if(Color(back).isDark()) Color.White else Color.Black),
                ),
            )
        }
        Spacer(modifier = GlanceModifier.height(35.dp))

        Row(modifier = GlanceModifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End) {
       Column(horizontalAlignment = Alignment.End,
       verticalAlignment = Alignment.CenterVertically) {
           Image(
               provider = ImageProvider(getWeatherImage(prefs[curImage] ?: "01d")),
               contentDescription = "",
               modifier = GlanceModifier.size(60.dp)
           )

               Text((prefs[name] ?: ""), style = TextStyle(
                   fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
               color = ColorProvider(day = Color.White, night = Color.White)))
           }

        }
        // Text("h${size.height.value.toInt()}w${size.width.value.toInt()}")


    }

}




object DataService{
    fun getData(mContext:Context, completion: (WidgetModel?) -> Unit){

        var lang = Lang.EN

        val locale = Locale.getDefault().displayLanguage
        lang = when(locale.lowercase()){
            "french" -> Lang.FR
            "greek" -> Lang.EL
            else -> Lang.EN
        }
        val pref: SharedPreferences = mContext
            .getSharedPreferences("MyPref", 0) // 0 - for private mode

        val savedUnit = pref.getString("unit", null)
        val units = if (savedUnit != null) {
            if (savedUnit == "si") WeatherUnits.SI else if (savedUnit == "us") WeatherUnits.US else WeatherUnits.AUTO
        } else {
            WeatherUnits.SI
        }
        val location = pref.getString("widgetLocation",null)
        val locationList = location?.split(",")
        if(locationList != null && locationList.size == 3) {
            val name = locationList.elementAt(0)
            val lat = locationList.elementAt(1)
            val lon = locationList.elementAt(2)
            Log.d("Location for widget", location.toString())
            getLocationData(
                name = name,
                lang = lang,
                location = Coordinates(latitude = lat.toDouble(), longitude = lon.toDouble()),
                units = units
            ){
                completion(it)
            }

        } else {
            completion( null)
        }

    }
    private fun getLocationData(location: Coordinates, lang: Lang, units: WeatherUnits, name:String, completion:(WidgetModel?)->Unit) {
        val searchLanguage = if (lang == Lang.EL)  "el" else if (lang == Lang.FR) "fr" else "en"
        val unit = if(units == WeatherUnits.SI || units == WeatherUnits.AUTO) "metric" else "imperial"
        Log.d("name",name)
        GlobalScope.launch {
            val data = WeatherDataApi.retrofitService.currentWeather(location.latitude,location.longitude, unit,searchLanguage,
                openWeatherKey)
            data.let { dat ->
                completion(WidgetModel(name = name, data = dat))
            }
        }
    }

}


@OptIn(ExperimentalFoundationApi::class,
    androidx.compose.animation.ExperimentalAnimationApi::class,
    ExperimentalMaterialApi::class
)
class UpdateAction : ActionCallback {


    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {

        DataService.getData(context) { data ->
            val temp = (data?.data?.main?.temp ?: 0.0).toInt()
            val min = (data?.data?.main?.temp_min ?: 5.0).toInt()
            val max = (data?.data?.main?.temp_max ?: 10.0).toInt()
            val feel = (data?.data?.main?.feels_like?: 9.0).toInt()
            val image = (data?.data?.weather?.first()?.icon ?: "02d")
           val  description = (data?.data?.weather?.first()?.description ?: "N/A")
            val locationName = data?.name ?: ""

            val background = getBackColorHex(data?.data?.weather?.first()?.icon ?: "02d")

         GlobalScope.launch {
             delay(2000)
             Log.d("Updating widget","Updating")
             updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) {
                 it.toMutablePreferences()
                     .apply {
                         this[intPreferencesKey("curTemp")] = temp ?: 0
                         this[intPreferencesKey("minTemp")] = min ?: 0
                         this[intPreferencesKey("maxTemp")] = max ?: 0
                         this[intPreferencesKey("feelsTemp")] = feel ?: 0
                         this[stringPreferencesKey("curImage")] = image ?: "01d"
                         this[stringPreferencesKey("curDescription")] = description ?: "N/A"
                         this[intPreferencesKey("back")] = background.toInt()
                         this[stringPreferencesKey("name")] = locationName.toString()
                     }
             }

             WeatherWidget().update(context, glanceId)
//             actionStartActivity<MainActivity>()
       }
        }
        }
}

class InitAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) {
            it.toMutablePreferences()
                .apply {
//                    val glassesOfWater = this[intPreferencesKey(WATER_WIDGET_PREFS_KEY)] ?: 0
//                    if (glassesOfWater < MAX_GLASSES) {
//                        this[intPreferencesKey(WATER_WIDGET_PREFS_KEY)] = glassesOfWater + 1
//                    }
                }
        }
        WeatherWidget().update(context, glanceId)

    }
}


class WeatherWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = WeatherWidget()
    private val coroutineScope = MainScope()


    override fun onEnabled(context: Context?) {
        super.onEnabled(context)

    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == WidgetRefreshCallback.UPDATE_ACTION) {
            observeData(context)
        }
    }

    override fun onRestored(context: Context?, oldWidgetIds: IntArray?, newWidgetIds: IntArray?) {
        super.onRestored(context, oldWidgetIds, newWidgetIds)

    }
    class WidgetRefreshCallback : ActionCallback {

        override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
            val intent = Intent(context, WeatherWidget::class.java).apply {
                action = UPDATE_ACTION
            }
            context.sendBroadcast(intent)
        }

        companion object {
            const val UPDATE_ACTION = "updateAction"
        }
    }


    private  fun observeData(context: Context){
        coroutineScope.launch {
           GlanceAppWidgetManager(context).getGlanceIds(WeatherWidget::class.java)
                    .firstOrNull()?.let {
                    var lang = "en"

                    val locale = Locale.getDefault().displayLanguage
                    lang = when (locale.lowercase()) {
                        "french" -> "fr"
                        "greek" -> "el"
                        else -> "en"
                    }
                    val pref: SharedPreferences = context
                        .getSharedPreferences("MyPref", 0) // 0 - for private mode

                    val savedUnit = pref.getString("unit", null)
                    val units = if (savedUnit != null) {
                        if (savedUnit == "si") "metric" else if (savedUnit == "us") "imperial" else "metric"
                    } else {
                        "metric"
                    }
                    val location = pref.getString("widgetLocation", null)
                    val locationList = location?.split(",")
                    if (locationList != null && locationList.size == 3) {
                        val name = locationList.elementAt(0)
                        val lat = locationList.elementAt(1)
                        val lon = locationList.elementAt(2)
                       val openData =  WeatherDataApi.retrofitService.currentWeather(lon = lon.toDouble(), lat = lat.toDouble(), lang = lang, unit = units, appid = openWeatherKey)

                        openData.let { data ->

                            val temp = (data.main?.temp ?: 0.0).toInt()
                            val min = (data.main?.temp_min ?: 5.0).toInt()
                            val max = (data.main?.temp_max ?: 10.0).toInt()
                            val feel = (data.main?.feels_like ?: 9.0).toInt()
                            val image = (data.weather?.first()?.icon ?: "02d")
                            val description = (data.weather?.first()?.description ?: "N/A")
                            val background = getBackColorHex(data.weather?.first()?.icon ?: "02d")
                            val locationName = data?.name ?: ""

                            updateAppWidgetState(context, PreferencesGlanceStateDefinition, it) {
                                it.toMutablePreferences()
                                    .apply {
                                        this[intPreferencesKey("curTemp")] = temp ?: 0
                                        this[intPreferencesKey("minTemp")] = min ?: 0
                                        this[intPreferencesKey("maxTemp")] = max ?: 0
                                        this[intPreferencesKey("feelsTemp")] = feel ?: 0
                                        this[stringPreferencesKey("curImage")] = image ?: "01d"
                                        this[stringPreferencesKey("curDescription")] =
                                            description ?: "N/A"
                                        this[intPreferencesKey("back")] = background.toInt()
                                        this[stringPreferencesKey("name")] = locationName.toString()


                                    }
                            }

                            WeatherWidget().update(context, it)
                    //
                        }
                    }
                }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        observeData(context)

    }

}

class WidgetModel constructor(
    val name: String,
    val data: CurrentWeather,

)