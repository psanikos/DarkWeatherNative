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
import androidx.compose.material.*
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.launch
import npsprojects.darkweather.*
import npsprojects.darkweather.R

@Composable
fun ErrorCard(model: WeatherViewModel){

    val icon = when(model.error.value){
        WeatherError.NOGPS -> Icons.TwoTone.GpsOff
        WeatherError.NONETWORK -> Icons.TwoTone.SignalWifiOff
        WeatherError.NOPERMISSION -> Icons.TwoTone.ShareLocation
        else -> Icons.TwoTone.Error
    }
    val text = when(model.error.value){
        WeatherError.NOGPS -> "Your location service is disabled"
        WeatherError.NONETWORK -> "Error loading data"
        WeatherError.NOPERMISSION -> "Location access denied"
        else -> "Error"
    }
    val subText = when(model.error.value){
        WeatherError.NOGPS -> "Add a new location or enable the location services"
        WeatherError.NONETWORK -> if (isOnline()) "Please check your internet connection" else "There was an error please reload"
        WeatherError.NOPERMISSION -> "To see your current location please allow access to the application"
        else -> "There was an error please reload"
    }
 Column(
     modifier = Modifier.cardModifier(),
     verticalArrangement = Arrangement.SpaceEvenly,
     horizontalAlignment = Alignment.CenterHorizontally
 ) {
    Icon(icon, contentDescription = "",tint = blue_grey_500,
    modifier = Modifier.size(80.dp))
     Text(text,style = MaterialTheme.typography.h2.copy(color = MaterialTheme.colors.primary))
     Text(subText,style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.secondary))

    IOSButton(icon = Icons.Default.Refresh, text = "Refresh", color = teal_600) {
        model.error.value = WeatherError.NONE
        model.initActions()
    }


 }
}
@Composable
fun NoLocationCard(model: WeatherViewModel,onShowAlert:()->Unit){
  var searchTerm by remember {
      mutableStateOf("")
  }
    var searchedAddresses:MutableList<Address> by remember { mutableStateOf(mutableListOf())}
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.cardModifier(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(verticalAlignment = Alignment.CenterVertically,horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {


                Icon(
                    Icons.TwoTone.ShareLocation, contentDescription = "", tint = blue_grey_500,
                    modifier = Modifier.size(30.dp)
                )
                Text(
                    "No location",
                    style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.primary)
                )
            }
            IOSButton(icon = Icons.Default.Refresh, text = "Refresh", color = teal_600) {
                model.error.value = WeatherError.NONE
                model.initActions()
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
                .background(
                    color = blue_grey_100.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(50)
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = searchTerm,
                onValueChange = {
                    searchTerm = it

                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(onSearch = {

                        scope.launch {
                            val addresses =  model.getCoordinatesFromLocation(searchTerm)
                            if (addresses.isNullOrEmpty()){
                                onShowAlert()
                            }
                            else {
                                searchedAddresses = addresses
                            }
                        }

                }),
                modifier = Modifier
                    .padding(start = 20.dp)
                    .fillMaxWidth(0.9f),
                textStyle = MaterialTheme.typography.caption.copy(color = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray),cursorBrush = Brush.horizontalGradient(colors = listOf(Color.Blue,Color.Gray))

            )
            if (searchTerm == "") {
                Text(
                    stringResource(R.string.searchText),
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier
                        .padding(start = 20.dp)
                )
            }
        }

        searchedAddresses.forEach {
            if(it.locality != null || it.featureName != null) {

                    Row(
                        modifier = Modifier
                            .padding(vertical = 5.dp)
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(
                                color = Color.Gray.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable {

                                    scope.launch {
                                        model.getCoordinatesWeather(
                                            location = Coordinates(it.latitude,it.longitude)
                                        )
                                        searchTerm = ""


                                }


                            },
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            it.locality ?: it.featureName,
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.padding(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            )
                        )

                        Text(it.countryName ?: "",   style = MaterialTheme.typography.caption,
                            modifier = Modifier.padding( horizontal = 20.dp,vertical = 8.dp))

                }
            }
        }

    }
}





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