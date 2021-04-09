package npsprojects.darkweather

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.twotone.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import npsprojects.darkweather.ui.theme.green_200
import npsprojects.darkweather.ui.theme.green_600
import npsprojects.darkweather.ui.theme.orange_500

enum class WeatherUnits{
    SI,US,AUTO
}


@Composable
fun SettingsView(model: WeatherViewModel,controller: NavController){

    val itemsModifier = Modifier
        .padding(vertical = 5.dp, horizontal = 16.dp)
        .fillMaxWidth()
    var units: WeatherUnits by remember { mutableStateOf(model.units) }


Scaffold() {
    Column(
        modifier = Modifier

            .fillMaxWidth()
            .height(500.dp)
        ,
        verticalArrangement = Arrangement.SpaceBetween

    ) {
        Surface(color = Color.White,modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)) {
   Column() {
       Spacer(modifier = Modifier.height(40.dp))
       Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Start) {
           IconButton(onClick = {
               controller.popBackStack()
           }) {
               Icon(Icons.Rounded.ArrowBackIosNew,contentDescription = "",tint =  Color.Black )
           }
       }
       Text("Settings", style = MaterialTheme.typography.h1.copy(color = Color.Black),modifier = Modifier.padding(horizontal = 16.dp,vertical = 20.dp))
   }
        }
        Row(modifier = itemsModifier,horizontalArrangement = Arrangement.SpaceBetween,verticalAlignment = Alignment.CenterVertically){
            Text("Units",style = MaterialTheme.typography.body1.copy(color = if (isSystemInDarkTheme()) Color.White else Color.Black))
            Surface(modifier = Modifier
                .height(30.dp)
                .width(180.dp)
                .clip(RoundedCornerShape(20)),color = Color.LightGray) {
                Row(){
                    Box(modifier = Modifier
                        .height(30.dp)
                        .width(60.dp)
                        .background(color = if (units == WeatherUnits.AUTO) Color.DarkGray else Color.LightGray)
                        .clickable {
                            units = WeatherUnits.AUTO
                            model.saveUnit(inputUnit = units)
                        }
                        ,contentAlignment = Alignment.Center) {
                        Text("Auto",style = MaterialTheme.typography.button.copy(color = if (units == WeatherUnits.AUTO) Color.White else Color.Gray))
                    }
                    Box(modifier = Modifier
                        .height(30.dp)
                        .width(60.dp)
                        .background(color = if (units == WeatherUnits.SI) Color.DarkGray else Color.LightGray)
                        .clickable {
                            units = WeatherUnits.SI
                            model.saveUnit(inputUnit = units)
                        }
                        ,contentAlignment = Alignment.Center) {
                        Text("SI",style = MaterialTheme.typography.button.copy(color = if (units == WeatherUnits.SI) Color.White else Color.Gray))
                    }
                    Box(modifier = Modifier
                        .height(30.dp)
                        .width(60.dp)
                        .background(color = if (units == WeatherUnits.US) Color.DarkGray else Color.LightGray)
                        .clickable {
                            units = WeatherUnits.US
                            model.saveUnit(inputUnit = units)
                        }
                        ,contentAlignment = Alignment.Center) {
                        Text("US",style = MaterialTheme.typography.button.copy(color = if (units == WeatherUnits.US) Color.White else Color.Gray))
                    }
                }
            }
        }


        Box(modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)) {
            if (model.error == WeatherError.NOPERMISSION || model.error == WeatherError.NOGPS) {
                Box(modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .clip(RoundedCornerShape(10))
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
                            "No location access is provided, if you want to see your current location please tap to allow access.",
                            style = MaterialTheme.typography.body2.copy(
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
                            )
                        ) {
                            Text("Allow access", style = MaterialTheme.typography.button)
                        }
                    }
                }
            }
        }



    }
}
}