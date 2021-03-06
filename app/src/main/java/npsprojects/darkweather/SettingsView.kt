package npsprojects.darkweather

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.twotone.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.res.TypedArrayUtils.getString
import androidx.navigation.NavController
import npsprojects.darkweather.ui.theme.green_200
import npsprojects.darkweather.ui.theme.green_600
import npsprojects.darkweather.ui.theme.light_blue_500
import npsprojects.darkweather.ui.theme.orange_500

enum class WeatherUnits {
    SI, US, AUTO
}


@SuppressLint("RestrictedApi")
@Composable
fun SettingsView(model: WeatherViewModel, controller: NavController) {

    val itemsModifier = Modifier
        .padding(vertical = 5.dp, horizontal = 16.dp)
        .fillMaxWidth()
    var units: WeatherUnits by remember { mutableStateOf(model.units) }


    Scaffold(
        backgroundColor = if(isSystemInDarkTheme()) Color(0xFF303030) else Color(0xFFf0f0f7)
    ) {
        Column(
            modifier = Modifier

                .fillMaxWidth()
                .height(500.dp),
            verticalArrangement = Arrangement.SpaceBetween

        ) {
            Surface(
                color = if(isSystemInDarkTheme()) Color.Black else Color.White, modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                Column() {
                    Spacer(modifier = Modifier.height(40.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        IconButton(onClick = {
                            controller.popBackStack()
                        }) {
                            Icon(
                                Icons.Rounded.ArrowBackIosNew,
                                contentDescription = "",
                                tint = light_blue_500
                            )
                        }
                    }
                    Text(
                        stringResource(R.string.SettingsTitle),
                        style = MaterialTheme.typography.h1,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)
                    )
                }
            }
            Row(
                modifier = itemsModifier,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.Units),
                    style = MaterialTheme.typography.body1.copy(color = if (isSystemInDarkTheme()) Color.White else Color.Black)
                )
                Surface(
                    modifier = Modifier
                        .height(30.dp)
                        .width(180.dp)
                        .clip(RoundedCornerShape(12.dp)), color = Color.LightGray
                ) {
                    Row() {
                        Box(modifier = Modifier
                            .height(30.dp)
                            .width(80.dp)
                            .background(color = if (units == WeatherUnits.AUTO) Color.DarkGray else Color.LightGray)
                            .clickable {
                                units = WeatherUnits.AUTO
                                model.saveUnit(inputUnit = units)
                            }, contentAlignment = Alignment.Center) {
                            Text(
                                stringResource(R.string.Auto),
                                style = MaterialTheme.typography.button.copy(color = if (units == WeatherUnits.AUTO) Color.White else Color.Gray)
                            )
                        }
                        Box(modifier = Modifier
                            .height(30.dp)
                            .width(60.dp)
                            .background(color = if (units == WeatherUnits.SI) Color.DarkGray else Color.LightGray)
                            .clickable {
                                units = WeatherUnits.SI
                                model.saveUnit(inputUnit = units)
                            }, contentAlignment = Alignment.Center) {
                            Text(
                                "SI",
                                style = MaterialTheme.typography.button.copy(color = if (units == WeatherUnits.SI) Color.White else Color.Gray)
                            )
                        }
                        Box(modifier = Modifier
                            .height(30.dp)
                            .width(60.dp)
                            .background(color = if (units == WeatherUnits.US) Color.DarkGray else Color.LightGray)
                            .clickable {
                                units = WeatherUnits.US
                                model.saveUnit(inputUnit = units)
                            }, contentAlignment = Alignment.Center) {
                            Text(
                                "US",
                                style = MaterialTheme.typography.button.copy(color = if (units == WeatherUnits.US) Color.White else Color.Gray)
                            )
                        }
                    }
                }
            }


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                if (model.error == WeatherError.NOPERMISSION || model.error == WeatherError.NOGPS) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.2f),
                                        Color.Black.copy(alpha = 0.1f)
                                    )
                                )
                            )
                    ) {
                        Column(
                            Modifier
                                .padding(16.dp), verticalArrangement = Arrangement.SpaceEvenly,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Icon(
                                Icons.TwoTone.Warning,
                                modifier = Modifier.size(50.dp),
                                contentDescription = "",
                                tint = orange_500
                            )
                            Text(
                                stringResource(id = R.string.LocationDenied)
                                ,style = MaterialTheme.typography.body2.copy(
                                    color = Color.White,

                                    ),

                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = {
                                    model.askPermission()
                                }, colors = ButtonDefaults.buttonColors(
                                    contentColor = green_200, backgroundColor = green_600
                                ),shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(stringResource(id = R.string.AllowAccess), style = MaterialTheme.typography.button)
                            }
                        }
                    }
                }
            }


        }
    }
}