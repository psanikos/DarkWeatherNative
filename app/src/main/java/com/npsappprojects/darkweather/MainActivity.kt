package com.npsappprojects.darkweather

import android.annotation.SuppressLint
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

   fun setAppContext(con:Context){
       context = con
   }

}

class MainActivity : ComponentActivity() {

    private val model by viewModels<WeatherViewModel>()

    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        MyApp.setAppContext(this)

        model.getCurrentLocation()
        
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