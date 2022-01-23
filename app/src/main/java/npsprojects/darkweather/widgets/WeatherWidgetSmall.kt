package npsprojects.darkweather.widgets
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.Range
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
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

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import npsprojects.darkweather.Coordinates
import npsprojects.darkweather.MainActivity
import npsprojects.darkweather.MyApp.context
import npsprojects.darkweather.WeatherUnits
import npsprojects.darkweather.getWeatherImage
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

@OptIn(ExperimentalFoundationApi::class,
    androidx.compose.animation.ExperimentalAnimationApi::class,
    androidx.compose.material.ExperimentalMaterialApi::class
)

private val curTemp = intPreferencesKey("curTemp")
private val minTemp = intPreferencesKey("minTemp")
private val maxTemp = intPreferencesKey("maxTemp")
private val feelsTemp = intPreferencesKey("feelsTemp")
private val curImage= stringPreferencesKey("curImage")
private val curDescription = stringPreferencesKey("curDescription")

enum class WidgetSize{
    SMALL,SMALL_WIDE,BIG,SMALL_TALL
}

class WeatherWidgetSmall : GlanceAppWidget() {
    private val widgetSizes = setOf<DpSize>(DpSize(width = 40.dp, height = 110.dp),DpSize(width = 250.dp, height = 40.dp),DpSize(width = 110.dp, height = 40.dp),
        DpSize(width = 110.dp, height = 110.dp),DpSize(width = 250.dp, height = 250.dp))
    override var stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override val sizeMode: SizeMode = SizeMode.Responsive(widgetSizes)

    override suspend fun onDelete(glanceId: GlanceId) {
        super.onDelete(glanceId)
    }


    @Composable
    override fun Content() {
       val prefs = currentState<Preferences>()
        val context = LocalContext.current
        val size = LocalSize.current


        var color:ColorProvider = ColorProvider(day = Color.White, night = Color.Black)
        var textColor:ColorProvider = ColorProvider(day = Color.Black, night = Color.White)
        var secondaryTextColor:ColorProvider = ColorProvider(day = Color.DarkGray, night = Color.Gray)
        var borderColor:ColorProvider = ColorProvider(day = Color.Black, night = Color.White)

//    color =
//        ColorProvider(day = MaterialTheme.colorScheme.primaryContainer, night = MaterialTheme.colorScheme.primaryContainer)
//    textColor = ColorProvider(
//        day = MaterialTheme.colorScheme.onPrimaryContainer, night = MaterialTheme.colorScheme.onPrimaryContainer
//    )
//    secondaryTextColor =
//        ColorProvider(day = MaterialTheme.colorScheme.onTertiaryContainer, night = MaterialTheme.colorScheme.onTertiaryContainer)
//    borderColor = ColorProvider(day = MaterialTheme.colorScheme.tertiaryContainer, night = MaterialTheme.colorScheme.tertiaryContainer)
//


        val sizeClass:WidgetSize = when(size.height.value.toInt()){
            in 0 .. 45 -> if(size.width.value < 105f) WidgetSize.SMALL else WidgetSize.SMALL_WIDE
            else -> if(size.width.value < 105f) WidgetSize.SMALL_TALL else WidgetSize.BIG
        }


    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(color)
            .cornerRadius(30.dp)

            .clickable(actionRunCallback<UpdateAction>()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = GlanceModifier
                .padding(5.dp)
                .wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                provider = ImageProvider(getWeatherImage(prefs[curImage] ?: "01d")),
                contentDescription = "",
                modifier = GlanceModifier.size(if (sizeClass == WidgetSize.BIG) 90.dp else if (sizeClass == WidgetSize.SMALL_WIDE) 60.dp else if (sizeClass == WidgetSize.SMALL_TALL) 50.dp else 50.dp)
            )
            // Text("h${size.height.value.toInt()}w${size.width.value.toInt()}")
            if (sizeClass == WidgetSize.SMALL || sizeClass == WidgetSize.SMALL_TALL) {
                Text(
                    text = ((prefs[curTemp] ?: 0).toString() + "°"),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = textColor
                    ),
                )
                Spacer(modifier = GlanceModifier.height(5.dp))
            }

            if (sizeClass == WidgetSize.BIG) {

                Text(
                    text = ((prefs[curTemp] ?: 0).toString() + "°"),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = if (size.height > 80.dp) 28.sp else 18.sp,
                        color = textColor
                    ),
                )
                Spacer(modifier = GlanceModifier.height(5.dp))
                if (size.width > 140.dp) {
                    Text(
                        text = (prefs[curDescription] ?: "N/A").toString(),
                        style = TextStyle(
                            fontWeight = FontWeight.Normal,
                            fontSize = 10.sp,
                            color = secondaryTextColor
                        ),
                    )
                    Spacer(modifier = GlanceModifier.height(10.dp))

                    Row(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = GlanceModifier.padding(top = 8.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = GlanceModifier.padding(horizontal = 5.dp)
                        ) {
                            Text(
                                text = "Min",
                                style = TextStyle(
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 9.sp,
                                    color = secondaryTextColor
                                ),
                            )

                            Text(
                                text = ((prefs[minTemp] ?: 0).toString() + "°"),
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = ColorProvider(
                                        day = light_blue_800,
                                        night = light_blue_500
                                    )
                                ),
                            )

                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = GlanceModifier.padding(horizontal = 10.dp)
                        ) {
                            Text(
                                text = "Max",
                                style = TextStyle(
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 9.sp,
                                    color = secondaryTextColor
                                ),
                            )

                            Text(
                                text = ((prefs[maxTemp] ?: 0).toString() + "°"),
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = ColorProvider(day = pink_800, night = pink_500)
                                ),
                            )


                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = GlanceModifier.padding(horizontal = 5.dp)
                        ) {
                            Text(
                                text = "Feels",
                                style = TextStyle(
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 9.sp,
                                    color = secondaryTextColor
                                ),
                            )

                            Text(
                                text = ((prefs[feelsTemp] ?: 1).toString() + "°"),
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = ColorProvider(day = teal_800, night = teal_500)
                                ),
                            )

                        }
                    }
                }
            }
        }
        if (sizeClass == WidgetSize.SMALL_WIDE) {
            Column(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .padding(end = 16.dp, top = 2.dp, bottom = 2.dp),
                horizontalAlignment = Alignment.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ((prefs[curTemp] ?: 0).toString() + "°"),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = textColor,
                        textAlign = TextAlign.End
                    ),
                )
                Text(
                    text = (prefs[curDescription] ?: "N/A").toString(),
                    style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 10.sp,
                        color = secondaryTextColor
                    ),
                )
            }
        }
    }


    }

    companion object DataService{
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

            GlobalScope.launch {
               val data = WeatherDataApi.retrofitService.currentWeather(location.latitude,location.longitude, unit,searchLanguage)
                    data?.let { dat ->
                        completion(WidgetModel(name = name, data = dat))
                    }
            }
        }

    }
}

//actionRunCallback<ButtonClickAction>()
class UpdateAction : ActionCallback {

    override suspend fun onRun(context: Context, glanceId: GlanceId, parameters: ActionParameters) {

        WeatherWidgetSmall.DataService.getData(context) { data ->
            val temp = (data?.data?.main?.temp ?: 0.0).toInt()
            val min = (data?.data?.main?.temp_min ?: 5.0).toInt()
            val max = (data?.data?.main?.temp_max ?: 10.0).toInt()
            val feel = (data?.data?.main?.feels_like?: 9.0).toInt()
            val image = (data?.data?.weather?.first()?.icon ?: "02d")
           val  description = (data?.data?.weather?.first()?.description ?: "N/A")

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
                     }
             }

             WeatherWidgetSmall().update(context, glanceId)
         }
        }
        }
}

class InitAction : ActionCallback {
    override suspend fun onRun(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) {
            it.toMutablePreferences()
                .apply {
//                    val glassesOfWater = this[intPreferencesKey(WATER_WIDGET_PREFS_KEY)] ?: 0
//                    if (glassesOfWater < MAX_GLASSES) {
//                        this[intPreferencesKey(WATER_WIDGET_PREFS_KEY)] = glassesOfWater + 1
//                    }
                }
        }
        WeatherWidgetSmall().update(context, glanceId)

    }
}


class WeatherWidgetSmallReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = WeatherWidgetSmall()


    override fun onEnabled(context: Context?) {
        super.onEnabled(context)

    }

    override fun onRestored(context: Context?, oldWidgetIds: IntArray?, newWidgetIds: IntArray?) {
        super.onRestored(context, oldWidgetIds, newWidgetIds)

    }

}

class WidgetModel constructor(
    val name: String,
    val data: CurrentWeather,

)