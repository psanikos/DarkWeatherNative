package npsprojects.darkweather.views

import android.location.Address
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.twotone.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContentProviderCompat.requireContext
import npsprojects.darkweather.models.WeatherViewModel
import npsprojects.darkweather.ui.theme.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.launch
import npsprojects.darkweather.*
import npsprojects.darkweather.R

//@Composable
//fun ErrorCard(model: WeatherViewModel){
//    val context = LocalContext.current
//
//    val icon = when(model.error.value){
//        WeatherError.NOGPS -> Icons.TwoTone.GpsOff
//        WeatherError.NONETWORK -> Icons.TwoTone.SignalWifiOff
//        WeatherError.NOPERMISSION -> Icons.TwoTone.ShareLocation
//        else -> Icons.TwoTone.Error
//    }
//    val text = when(model.error.value){
//        WeatherError.NOGPS -> "Your location service is disabled"
//        WeatherError.NONETWORK -> "Error loading data"
//        WeatherError.NOPERMISSION -> "Location access denied"
//        else -> "Error"
//    }
//    val subText = when(model.error.value){
//        WeatherError.NOGPS -> "Add a new location or enable the location services"
//        WeatherError.NONETWORK -> if (isOnline()) "Please check your internet connection" else "There was an error please reload"
//        WeatherError.NOPERMISSION -> "To see your current location please allow access to the application"
//        else -> "There was an error please reload"
//    }
// Column(
//     modifier = Modifier.cardModifier(),
//     verticalArrangement = Arrangement.SpaceEvenly,
//     horizontalAlignment = Alignment.CenterHorizontally
// ) {
//    Icon(icon, contentDescription = "",tint = blue_grey_500,
//    modifier = Modifier.size(80.dp))
//     Text(text,style = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.primary))
//     Text(subText,style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.secondary))
//
//    IOSButton(icon = Icons.Default.Refresh, text = "Refresh", color = teal_600) {
//        model.error.value = WeatherError.NONE
//        model.initActions(context = context )
//    }
//
//
// }
//}




//@Preview(name = "Error card")
//@Composable
//fun ErrorPreviews(){
//Column() {
//
//    DarkWeatherTheme(darkTheme = false) {
//        Box(modifier = Modifier.background(Color.Gray)) {
//           NoLocationCard()
//        }
//    }
//    DarkWeatherTheme(darkTheme = true) {
//        Box(modifier = Modifier.background(Color.Gray)) {
//            NoLocationCard()
//        }
//    }
//}
//}