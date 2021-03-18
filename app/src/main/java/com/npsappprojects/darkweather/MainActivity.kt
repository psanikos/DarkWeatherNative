package com.npsappprojects.darkweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.npsappprojects.darkweather.ui.theme.DarkWeatherTheme

class MainActivity : ComponentActivity() {

    private val model by viewModels<WeatherViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        model.getCurrentLocation(this)
        
        setContent {

            DarkWeatherTheme {
              MyApp(model = model)
            }
        }
    }
}

@Composable
fun MyApp(model:WeatherViewModel){


    Scaffold() {
    when(model.isLoading){
        true -> Box(modifier = Modifier.fillMaxSize(),contentAlignment = Alignment.Center){
            CircularProgressIndicator()
        }
       false -> when(model.currentLocation) {
           null -> Text("No location data")
           model.currentLocation!! ->
               when(model.currentLocationData){
                null -> Text("Couldn't load weather information")
               model.currentLocationData!! -> MainWeatherCard(locationData = model.currentLocationData!!, locationName = model.currentLocationName, isCurrent = true)

            }
       }

    }

}
}

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    DarkWeatherTheme {
//        Greeting("Android")
//    }
//}