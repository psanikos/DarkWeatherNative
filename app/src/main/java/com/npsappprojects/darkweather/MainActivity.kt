package com.npsappprojects.darkweather

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.ExperimentalMaterialApi
import androidx.core.view.WindowCompat
import com.npsappprojects.darkweather.ui.theme.DarkWeatherTheme


 @SuppressLint("StaticFieldLeak")
 object MyApp {
   lateinit var context: Context
     lateinit var activity: Activity
   fun setAppContext(con:Context){
       context = con
   }
     fun setAppActivity(act: Activity){
         activity = act
     }
}

class MainActivity : ComponentActivity() {

    private val model by viewModels<WeatherViewModel>()

    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        MyApp.setAppContext(this)
        MyApp.setAppActivity(act = this)
        model.getCurrentLocationWeather()
        
        setContent {

            DarkWeatherTheme {
             MainPageView(model = model)
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