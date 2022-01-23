package npsprojects.darkweather

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
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
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.*
import npsprojects.darkweather.models.WeatherViewModel
import npsprojects.darkweather.ui.theme.DarkWeatherTheme
import npsprojects.darkweather.views.*


@SuppressLint("StaticFieldLeak")
object MyApp {
    lateinit var context: Context
    lateinit var activity: Activity
    fun setAppContext(con: Context) {
        context = con
    }

    fun setAppActivity(act: Activity) {
        activity = act
    }
}

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {

    private val model by viewModels<WeatherViewModel>()
    private var mInterstitialAd: InterstitialAd? = null
    private final var TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        MyApp.setAppContext(this)
        MyApp.setAppActivity(act = this)


        setContent {
            MobileAds.initialize(
                this
            ) {
                val adRequest = AdRequest.Builder().build()
                val adId = "ca-app-pub-9340838273925003/2192844146"
                val testId = "ca-app-pub-3940256099942544/1033173712"
                InterstitialAd.load(this,adId, adRequest, object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.d(TAG, adError?.message)
                        mInterstitialAd = null
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        Log.d(TAG, "Ad was loaded.")
                        mInterstitialAd = interstitialAd
                        GlobalScope.launch(Dispatchers.Main){

                            delay(8000)
                            if (mInterstitialAd != null) {
                                mInterstitialAd?.show(MyApp.activity)
                            } else {
                                Log.d("TAG", "The interstitial ad wasn't ready yet.")
                            }
                        }
                    }
                })
                mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        Log.d(TAG, "Ad was dismissed.")
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                        Log.d(TAG, "Ad failed to show.")
                    }

                    override fun onAdShowedFullScreenContent() {
                        Log.d(TAG, "Ad showed fullscreen content.")
                        mInterstitialAd = null
                    }
                }
            }

            DarkWeatherTheme {
                ProvideWindowInsets {
                    if (model.hasInit) {
                        MyApp(model = model)
                    } else {
                        FirstScreen(model = model)
                    }
                }
            }
        }

    }


    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        println("GAVE PERMISSION")
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            model.onPermissionGranted()
            model.hasRun()
            if (model.error.value == WeatherError.NOPERMISSION || model.error.value == WeatherError.NOGPS) {
                model.error.value = WeatherError.NONE
            }
        }
    }
}



@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun MyApp(model: WeatherViewModel) {
    val controller = rememberNavController()
    NavHost(navController = controller, startDestination = "Main") {
        composable("Main") {
            MainPageView(model = model, controller = controller)
        }
        composable("Settings") {
            SettingsView(model = model, controller = controller)
        }
        composable("Search") {
            FullSearchView(model = model, controller = controller)
        }
        composable("Map"){
            FullMapView(model = model, controller = controller)
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