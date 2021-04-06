package npsprojects.darkweather

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import npsprojects.darkweather.ui.theme.DarkWeatherTheme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue



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

    @ExperimentalFoundationApi
    @ExperimentalAnimationApi
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        MyApp.setAppContext(this)
        MyApp.setAppActivity(act = this)

        
        setContent {
            MobileAds.initialize(
                this
            ) {

            }
            DarkWeatherTheme {
           if(model.hasInit){
             MyApp(model = model)
           }
                else {
               FirstScreen(model = model)
                }
            }
        }
    }



    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        println("GAVE PERMISSION")
if(ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED){
        model.onPermissionGranted()
    model.hasRun()
    if (model.error == WeatherError.NOPERMISSION) {
        model.error = WeatherError.NONE
    }
    }
    }
}


@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun MyApp(model: WeatherViewModel){
    val controller = rememberNavController()
    NavHost(navController = controller, startDestination = "Main") {
      composable("Main"){
          MainPageView(model = model,controller = controller)
      }
        composable("Add"){
            AddPlaceView(model = model,controller = controller)
        }
        composable("Settings"){
            SettingsView(model = model,controller = controller)
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