package npsprojects.darkweather.views

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Warning
import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.accompanist.insets.statusBarsPadding
import compose.icons.FontAwesomeIcons
import compose.icons.WeatherIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.ArrowLeft
import compose.icons.fontawesomeicons.solid.LocationArrow
import compose.icons.weathericons.Celsius
import compose.icons.weathericons.Fahrenheit
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import npsprojects.darkweather.R
import npsprojects.darkweather.WeatherError
import npsprojects.darkweather.WeatherUnits
import npsprojects.darkweather.models.LOCATIONERRORVALUES
import npsprojects.darkweather.models.WeatherViewModel
import npsprojects.darkweather.ui.theme.*


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("RestrictedApi", "SuspiciousIndentation")
@Composable
fun SettingsView(model: WeatherViewModel, controller: NavController) {

    val itemsModifier = Modifier
        .padding(vertical = 5.dp, horizontal = 16.dp)
        .fillMaxWidth()
    var units: WeatherUnits by remember { mutableStateOf(model.units) }
    val context = LocalContext.current
    fun requestLocationPermission(context: Context) {
        val MY_PERMISSIONS_REQUEST_LOCATION = 99
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            MY_PERMISSIONS_REQUEST_LOCATION
        )
        model.resetError()
    }
    var hasAccess by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = "access", block ={
        hasAccess = WeatherViewModel.LocationFetcher.canGetLocation(context)
    } )

    val error by model.error.observeAsState()

    val scope = rememberCoroutineScope()

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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
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

            if(!hasAccess) {
                IconButton(onClick = {
                    if (error != LOCATIONERRORVALUES.LOCATION_FULL_DENIED) {
                        requestLocationPermission(context = context)
                    } else {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = Uri.parse("package:" + context.packageName)
                        ContextCompat.startActivity(context, intent, null)
                    }
                    scope.launch {
                        delay(2000)
                        hasAccess = WeatherViewModel.LocationFetcher.canGetLocation(context)
                    }
                },
                modifier = Modifier
                    .padding(20.dp)
                    .height(45.dp)
                    .fillMaxWidth()
                    .background(
                        color = teal_500.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(6.dp)
                    )
                    ) {
                    Text(
                        "Enable location access",
                        style = MaterialTheme.typography.labelMedium.copy(color = teal_500),
                        textAlign = TextAlign.Center
                    )
                }
            }else{
                Spacer(modifier = Modifier.height(2.dp))
            }
        }
    }
}