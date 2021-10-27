package npsprojects.darkweather.views

import android.util.Range
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import npsprojects.darkweather.R
import npsprojects.darkweather.WeatherUnits
import npsprojects.darkweather.getWeatherIcon
import npsprojects.darkweather.models.Current
import npsprojects.darkweather.models.WeatherViewModel
import npsprojects.darkweather.round
import npsprojects.darkweather.ui.theme.*
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

@ExperimentalCoilApi
@Composable
fun MainCard(model:WeatherViewModel,controller:NavController) {

    var icon by rememberSaveable {
        mutableStateOf("02d")
    }
    var iconId by rememberSaveable {
        mutableStateOf(500)
    }
    var temp by rememberSaveable {
        mutableStateOf("N/A°")
    }
    var feel by rememberSaveable {
        mutableStateOf("N/A°")
    }
    var tempHigh by rememberSaveable {
        mutableStateOf("N/A°")
    }
    var tempLow by rememberSaveable {
        mutableStateOf("N/A°")
    }
    var description by rememberSaveable {
        mutableStateOf("N/A")
    }
    var angle by rememberSaveable {
        mutableStateOf(0f)
    }
    var air by rememberSaveable {
        mutableStateOf(0.0)
    }
    var pop by rememberSaveable {
        mutableStateOf(0)
    }
    var dayDescription by rememberSaveable {
        mutableStateOf("")
    }
    val index: Int by model.index.observeAsState(initial = 0)

    LaunchedEffect(key1 = index + model.locations.size, block = {
        if (model.locations.isNotEmpty() && model.locations.size > index) {
            icon = model.locations[index].data.current.weather[0].icon
            iconId = model.locations[index].data.current.weather[0].id.toInt()
            temp = model.locations[index].data.current.temp.toUInt().toString() + "°"
            tempHigh = model.locations[index].data.daily[0].temp.max.toUInt().toString() + "°"
            tempLow = model.locations[index].data.daily[0].temp.min.toUInt().toString() + "°"
            description = model.locations[index].data.current.weather[0].description
            angle = model.locations[index].data.current.wind_deg.toFloat()
            air = model.locations[index].data.current.wind_speed.round(1)
            pop = (100 * (model.locations[index].data.daily[0].pop ?: 0.0)).roundToInt()
            feel = model.locations[index].data.current.feels_like.toUInt().toString() + "°"
            dayDescription = model.locations[index].data.daily[0].weather[0].description
        }
    })


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalArrangement = Arrangement.spacedBy(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Column(
                modifier = Modifier
                    .padding(horizontal = 15.dp)
                    .padding(top = 20.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box() {

                  Icon(imageVector =  getWeatherIcon(iconId,icon.contains("n")), contentDescription = "",Modifier.size(150.dp))
                }

                Text(
                    text = temp,
                    style = MaterialTheme.typography.h1.copy(
                        fontSize = 60.sp,
                        fontWeight = FontWeight.Black
                    )
                )


                Text(description, style = MaterialTheme.typography.body1)
                Text(
                    "Feels like: $feel",
                    style = MaterialTheme.typography.body2.copy(
                        color = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {


                        ColoredIcon(
                            Icons.Filled.Navigation,
                            contentDescription = "",
                            modifier = Modifier
                                .size(25.dp)
                                .rotate(angle),
                            tint = if (isSystemInDarkTheme()) Color.White else Color.Black,

                            )

                        Text(
                            text = "$air " + if (model.units == WeatherUnits.SI) "km/h" else "mph",
                            style = MaterialTheme.typography.body1
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        ColoredIcon(
                            Icons.Filled.ArrowUpward,
                            contentDescription = "",
                            modifier = Modifier.size(25.dp),
                            tint = Color.Red
                        )

                        Text(text = tempHigh, style = MaterialTheme.typography.body1)
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        ColoredIcon(
                            Icons.Filled.ArrowDownward,
                            contentDescription = "",
                            modifier = Modifier.size(25.dp),
                            tint = purple_500
                        )

                        Text(text = tempLow, style = MaterialTheme.typography.body1)
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ColoredIcon(
                            Icons.Filled.Umbrella, contentDescription = "",
                            modifier = Modifier.size(25.dp),
                            tint = light_blue_500
                        )

                        Text(
                            text = "${pop}%",
                            style = MaterialTheme.typography.body1
                        )
                    }
                }

            }

    }

    @Composable
    fun RainAlert(model: WeatherViewModel) {
        val index by model.index.observeAsState(initial = 0)
        var hourly: List<Current>? = null
        var range: Range<Date>? by remember {
            mutableStateOf(null)
        }

        fun getRange(): Range<Date>? {
            var start: Date? = null
            var end: Date? = null

            hourly?.forEach {
                if (start == null && (it.pop!! > 0.49)) {
                    start = Date.from(Instant.ofEpochMilli(it.dt * 1000))
                    end = start
                } else if ((it.pop!! > 0.49)) {

                    val dif = if (end != null) TimeUnit.HOURS.convert(
                        it.dt * 1000 - end!!.time,
                        TimeUnit.MILLISECONDS
                    ) else 99

                    if (dif < 2) {
                        end = Date.from(Instant.ofEpochMilli(it.dt * 1000))
                    }

                    println("ENDDD" + SimpleDateFormat("HH:mm", Locale.getDefault()).format(end!!))
                }
            }

            return if (start != null && end != null) Range(start, end) else null
        }

        LaunchedEffect(key1 = "$index ${model.locations.size}", block = {
            hourly =
                if (model.locations.size > 0 && index < model.locations.size) model.locations[index].data.hourly else listOf()
            range = getRange()
        })
        if (range != null && range?.lower != null && range?.upper != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(
                        color = Color.Blue.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12)
                    )
                    .padding(10.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Filled.Warning,
                        contentDescription = "",
                        tint = Color.Blue,
                        modifier = Modifier.size(40.dp)
                    )
                    Text(
                        stringResource(id = R.string.phenomena),
                        style = MaterialTheme.typography.h3.copy(
                            fontSize = 14.sp
                        )
                    )
                    Text(
                        stringResource(id = R.string.expect) + " " +
                                SimpleDateFormat(
                                    "EEEE HH:mm",
                                    Locale.getDefault()
                                ).format(range!!.lower)
                                + " "
                                + stringResource(id = R.string.until) + " " +
                                SimpleDateFormat(
                                    "EEEE HH:mm",
                                    Locale.getDefault()
                                ).format(range!!.upper),
                        style = MaterialTheme.typography.body2,
                        maxLines = 3
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

    }



//@Preview
//@Composable
//fun MainPreview(){
//    MaterialTheme {
//      MainCard()
//    }
//}
