package npsprojects.darkweather.widgets
import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.*
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.unit.ColorProvider
import androidx.glance.layout.*
import androidx.glance.state.GlanceState
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.*
import coil.annotation.ExperimentalCoilApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import npsprojects.darkweather.MainActivity
import npsprojects.darkweather.getWeatherImage
import npsprojects.darkweather.ui.theme.frosted
import npsprojects.darkweather.ui.theme.iceBlack
import java.util.prefs.Preferences

@OptIn(ExperimentalFoundationApi::class,
    androidx.compose.animation.ExperimentalAnimationApi::class,
    androidx.compose.material.ExperimentalMaterialApi::class
)
class WeatherWidgetSmall : GlanceAppWidget() {
    override var stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override val sizeMode: SizeMode
        get() = super.sizeMode


    @OptIn(ExperimentalCoilApi::class)
    @Composable
    override fun Content() {
        //  val prefs = currentState<Preferences>()
        // val checked = prefs[booleanPreferencesKey(keyOfCheckedState)] ?: false
//GlanceModifier.clickable(actionRunCallback<UpdateAction>())
        val context = LocalContext.current
        val size = LocalSize.current
        val id = LocalGlanceId.current
        val color =
            ColorProvider(day = frosted.copy(alpha = 0.7f), night = iceBlack.copy(alpha = 0.7f))
        val textColor = ColorProvider(day = Color.Black, night = Color.White)
        val secondaryTextColor = ColorProvider(day = Color.DarkGray, night = Color.LightGray)
        fun getData() {

        }
        Row(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(color)
                .padding(5.dp)
                .clickable(actionStartActivity<MainActivity>()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = GlanceModifier
                    .wrapContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    provider = ImageProvider(getWeatherImage("01d")),
                    contentDescription = "",
                    modifier = GlanceModifier.size(if (size.width > 100.dp) 70.dp else 50.dp)
                )
                if (size.width < 140.dp) {

                    Text(
                        text = "21°",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = if (size.width > 100.dp) 26.sp else 22.sp,
                            color = textColor
                        ),
                    )

                    if (size.height > 70.dp) {
                        Text(
                            text = "Partly cloudy",
                            style = TextStyle(
                                fontWeight = FontWeight.Normal,
                                fontSize = 10.sp,
                                color = secondaryTextColor
                            ),
                        )

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
                                    text = "16:00",
                                    style = TextStyle(
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 9.sp,
                                        color = secondaryTextColor
                                    ),
                                )
                                Image(
                                    provider = ImageProvider(getWeatherImage("01d")),
                                    contentDescription = "",
                                    modifier = GlanceModifier.size(20.dp)
                                )

                                Text(
                                    text = "21°",
                                    style = TextStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = textColor
                                    ),
                                )

                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = GlanceModifier.padding(horizontal = 10.dp)
                            ) {
                                Text(
                                    text = "19:00",
                                    style = TextStyle(
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 9.sp,
                                        color = secondaryTextColor
                                    ),
                                )
                                Image(
                                    provider = ImageProvider(getWeatherImage("01d")),
                                    contentDescription = "",
                                    modifier = GlanceModifier.size(20.dp)
                                )

                                Text(
                                    text = "21°",
                                    style = TextStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = textColor
                                    ),
                                )


                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = GlanceModifier.padding(horizontal = 5.dp)
                            ) {
                                Text(
                                    text = "22:00",
                                    style = TextStyle(
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 9.sp,
                                        color = secondaryTextColor
                                    ),
                                )
                                Image(
                                    provider = ImageProvider(getWeatherImage("01d")),
                                    contentDescription = "",
                                    modifier = GlanceModifier.size(20.dp)
                                )

                                Text(
                                    text = "21°",
                                    style = TextStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = textColor
                                    ),
                                )

                            }
                        }
                    }
                }
            }
            if (size.width > 150.dp) {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .padding(end = 16.dp),
                    horizontalAlignment = Alignment.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "21°",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = if (size.width > 100.dp) 26.sp else 22.sp,
                            color = textColor,
                            textAlign = TextAlign.End
                        ),
                    )
                    Text(
                        text = "Partly cloudy",
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
}

//actionRunCallback<ButtonClickAction>()
class ButtonClickAction : ActionCallback {
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

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)

    }

}
class WeatherWidgetBigReceiver : GlanceAppWidgetReceiver() {



    override val glanceAppWidget: GlanceAppWidget = WeatherWidgetSmall()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)

    }

}
class WeatherWidgetWideReceiver : GlanceAppWidgetReceiver() {



    override val glanceAppWidget: GlanceAppWidget = WeatherWidgetSmall()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)

    }

}