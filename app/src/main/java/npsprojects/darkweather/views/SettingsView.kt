package npsprojects.darkweather.views

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Warning
import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.insets.statusBarsPadding
import compose.icons.FontAwesomeIcons
import compose.icons.WeatherIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.ArrowLeft
import compose.icons.fontawesomeicons.solid.LocationArrow
import compose.icons.weathericons.Celsius
import compose.icons.weathericons.Fahrenheit
import npsprojects.darkweather.R
import npsprojects.darkweather.WeatherError
import npsprojects.darkweather.WeatherUnits
import npsprojects.darkweather.models.WeatherViewModel
import npsprojects.darkweather.ui.theme.*


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("RestrictedApi")
@Composable
fun SettingsView(model: WeatherViewModel, controller: NavController) {

    val itemsModifier = Modifier
        .padding(vertical = 5.dp, horizontal = 16.dp)
        .fillMaxWidth()
    var units: WeatherUnits by remember { mutableStateOf(model.units) }
    val context = LocalContext.current


    Scaffold(

        topBar = {
            MediumTopAppBar(
                title = {
                    Text(stringResource(R.string.settings),
                        style = androidx.compose.material3.MaterialTheme.typography.headlineLarge)

                },
                navigationIcon = {
                    IconButton(onClick = {
                        controller.popBackStack()
                    }) {
                        Icon(FontAwesomeIcons.Solid.ArrowLeft,contentDescription = "",
                            modifier = Modifier.size(25.dp))
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.statusBarsPadding()
            )

        },
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Row(
                modifier = itemsModifier,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.Units),
                    style = MaterialTheme.typography.bodyMedium.copy(color = if (isSystemInDarkTheme()) Color.White else Color.Black)
                )

                    Row( horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically) {

                        IconButton(onClick = {
                            units = WeatherUnits.SI
                            model.saveUnit(inputUnit = units, context = context)
                        }) {
                            ColoredIcon(
                                imageVector = WeatherIcons.Celsius,
                                contentDescription = "",
                                modifier = Modifier.size(40.dp),
                                padding = 4.dp,
                                tint = if (units == WeatherUnits.SI) teal_600 else Color.LightGray
                            )
                        }
                        IconButton(onClick = {
                            units = WeatherUnits.US
                            model.saveUnit(inputUnit = units, context = context)
                        }) {
                            ColoredIcon(
                                imageVector = WeatherIcons.Fahrenheit,
                                contentDescription = "",
                                modifier = Modifier.size(40.dp),
                                padding = 4.dp,
                                tint = if (units == WeatherUnits.US) teal_600 else Color.LightGray
                            )
                        }

                    }

            }


        }
    }
}