package npsprojects.darkweather.views

import android.location.Address
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import npsprojects.darkweather.Coordinates
import npsprojects.darkweather.models.WeatherViewModel
import npsprojects.darkweather.ui.theme.DarkWeatherTheme


@Composable
fun SearchView(model: WeatherViewModel, controller: NavController) {
    var searchTerm by remember {
        mutableStateOf("")
    }
    var searchedAddresses: MutableList<Address> by remember { mutableStateOf(mutableListOf()) }
    val scope = rememberCoroutineScope()
    var showAlert: Boolean by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text("Add new place", style = MaterialTheme.typography.h2)
            }, navigationIcon = {
                IconButton(onClick = {
                    controller.popBackStack()
                }) {
                    Icon(
                        Icons.Default.ChevronLeft, contentDescription = "",
                        modifier = Modifier.size(40.dp)
                    )
                }
            },
                elevation = 0.dp,
                backgroundColor = Color.Transparent,
                modifier = Modifier.height(140.dp)
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {


            BasicTextField(value = searchTerm, onValueChange = {
                searchTerm = it
            }, keyboardActions = KeyboardActions(onSearch = {

                scope.launch(Dispatchers.IO) {
                    val addresses =
                        model.getCoordinatesFromLocation(
                            searchTerm
                        )
                    launch(Dispatchers.Main) {
                        if (addresses.isNullOrEmpty()) {
                            showAlert = true
                        } else {
                            searchedAddresses = addresses
                        }
                    }
                }
            }), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                modifier = Modifier
                    .padding(20.dp)
                    .background(
                        color = Color.Gray.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(40)
                    )
                    .height(40.dp)
                    .fillMaxWidth()
                    .padding(10.dp),
//                decorationBox = {
//                    if (searchTerm == "") Text(
//                        text = "Search",
//                        style = MaterialTheme.typography.body2
//                    ) else null
//                },
                textStyle = MaterialTheme.typography.body1,

            )
            searchedAddresses.forEach {

                if (it.locality != null || it.featureName != null) {

                    Row(
                        modifier = Modifier
                            .padding(vertical = 5.dp, horizontal = 20.dp)
                            .fillMaxWidth()
                            .height(50.dp)
                            .clickable {
                                scope.launch {

                                    model.getCoordinatesWeather(
                                        location = Coordinates(
                                            it.latitude,
                                            it.longitude
                                        )
                                    )
                                    searchTerm = ""
                                    searchedAddresses.clear()
                                    delay(1000)

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

                        Text(
                            it.countryName ?: "",
                            style = MaterialTheme.typography.caption,
                            modifier = Modifier.padding(
                                horizontal = 20.dp,
                                vertical = 8.dp
                            )
                        )

                    }

                }
            }
        }
    }

}

//@Preview
//@Composable
//fun SearchPreview(){
//    DarkWeatherTheme {
//        SearchView()
//    }
//}