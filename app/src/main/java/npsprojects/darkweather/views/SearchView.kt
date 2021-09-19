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
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import npsprojects.darkweather.Coordinates
import npsprojects.darkweather.R
import npsprojects.darkweather.models.WeatherViewModel
import npsprojects.darkweather.ui.theme.DarkWeatherTheme
import npsprojects.darkweather.ui.theme.blue_grey_500
import npsprojects.darkweather.ui.theme.red_500
import npsprojects.darkweather.ui.theme.teal_500

@Composable
fun SearchView(model: WeatherViewModel,onSelected:()->Unit) {
    var searchTerm by remember {
        mutableStateOf("")
    }
    var searchedAddresses: MutableList<Address> by remember { mutableStateOf(mutableListOf()) }
    val scope = rememberCoroutineScope()
    var showAlert: Boolean by remember { mutableStateOf(false) }

    Box(contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .padding(horizontal = 0.dp, vertical = 20.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Box(contentAlignment = Alignment.CenterStart) {
                BasicTextField(
                    value = searchTerm,
                    onValueChange = {
                        searchTerm = it
                    },
                    keyboardActions = KeyboardActions(onSearch = {

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
                    }),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    modifier = Modifier
                        .padding(20.dp)
                        .background(
                            color = Color.Gray.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(20)
                        )
                        .height(50.dp)
                        .fillMaxWidth()
                        .padding(15.dp),
//                decorationBox = {
//                    if (searchTerm == "") Text(
//                        text = "Search",
//                        style = MaterialTheme.typography.body2
//                    ) else null
//                },
                    textStyle = MaterialTheme.typography.body1.copy(color = if(isSystemInDarkTheme()) Color.White else Color.Black),

                    )

                if (searchTerm == "") {
                    Text(
                        text = "Search",
                        style = MaterialTheme.typography.body2.copy(color = if(isSystemInDarkTheme()) Color.White else Color.Black),
                        modifier = Modifier.padding(start = 30.dp)
                    )
                }

            }

            searchedAddresses.forEach {

                if (it.locality != null || it.featureName != null) {

                    Row(
                        modifier = Modifier
                            .padding(vertical = 5.dp, horizontal = 20.dp)
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(
                                color = if (isSystemInDarkTheme()) Color(0xFF121212) else Color.White,
                                shape = RoundedCornerShape(20)
                            )
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
                                    onSelected()
                                }
                            },
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            it.locality ?: it.featureName,
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 8.dp
                            )
                        )

                        Text(
                            it.countryName ?: "",
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 8.dp
                            )
                        )

                    }

                }
            }
        }
        if (showAlert) {
            // below line is use to
            // display a alert dialog.
            AlertDialog(
                // on dialog dismiss we are setting
                // our dialog value to false.
                onDismissRequest = { showAlert = false },

                // below line is use to display title of our dialog
                // box and we are setting text color to white.
                title = { Text(text = stringResource(id = R.string.NoResults), style = MaterialTheme.typography.h4) },

                // below line is use to display
                // description to our alert dialog.
                text = { Text(stringResource(id = R.string.ChangeSearch), style = MaterialTheme.typography.body2) },

                // in below line we are displaying
                // our confirm button.
                confirmButton = {
                    // below line we are adding on click
                    // listener for our confirm button.
                    TextButton(
                        onClick = {
                            showAlert = false
                            searchTerm = ""

                        }
                    ) {
                        // in this line we are adding
                        // text for our confirm button.
                        Text("OK", style = MaterialTheme.typography.button.copy(color = teal_500))
                    }
                },
                // in below line we are displaying
                // our dismiss button.
                dismissButton = {
                    // in below line we are displaying
                    // our text button
                    TextButton(
                        // adding on click listener for this button
                        onClick = {
                            showAlert = false

                        }
                    ) {
                        // adding text to our button.
                        Text(stringResource(id = R.string.Back), style = MaterialTheme.typography.button.copy(color = red_500))
                    }
                },
                // below line is use to add background color to our alert dialog
                backgroundColor = if(isSystemInDarkTheme()) Color.DarkGray else Color.White,

                // below line is use to add content color for our alert dialog.
                contentColor = if(isSystemInDarkTheme()) Color.White else Color.Black
            )
        }

    }

}


@Composable
fun FullSearchView(model: WeatherViewModel,controller: NavController) {
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
            },
                navigationIcon = {
                    IconButton(onClick = {
                        controller.popBackStack()
                    }) {
                        Icon(Icons.Default.ChevronLeft,contentDescription = "",
                            modifier = Modifier.size(40.dp))
                    }
                },
                elevation = 0.dp,
                backgroundColor = Color.Transparent,
                modifier = Modifier.height(140.dp)
            )
        },
        backgroundColor = if(isSystemInDarkTheme()) Color(0xFF202020) else Color(0xFFEFEFEF)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 0.dp, vertical = 20.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Box(contentAlignment = Alignment.CenterStart) {
                BasicTextField(
                    value = searchTerm,
                    onValueChange = {
                        searchTerm = it
                    },
                    keyboardActions = KeyboardActions(onSearch = {

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
                    }),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    modifier = Modifier
                        .padding(20.dp)
                        .background(
                            color = Color.Gray.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(20)
                        )
                        .height(50.dp)
                        .fillMaxWidth()
                        .padding(15.dp),
//                decorationBox = {
//                    if (searchTerm == "") Text(
//                        text = "Search",
//                        style = MaterialTheme.typography.body2
//                    ) else null
//                },
                    textStyle = MaterialTheme.typography.body1.copy(color = if(isSystemInDarkTheme()) Color.White else Color.Black)

                    )

                if (searchTerm == "") {
                    Text(
                        text = "Search",
                        style = MaterialTheme.typography.body2.copy(color = if(isSystemInDarkTheme()) Color.White else Color.Black),
                        modifier = Modifier.padding(start = 30.dp)
                    )
                }

            }
            searchedAddresses.forEach {

                if (it.locality != null || it.featureName != null) {

                    Row(
                        modifier = Modifier
                            .padding(vertical = 5.dp, horizontal = 20.dp)
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(
                                color = if (isSystemInDarkTheme()) Color(0xFF121212) else Color.White,
                                shape = RoundedCornerShape(20)
                            )
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
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 8.dp
                            )
                        )

                        Text(
                            it.countryName ?: "",
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 8.dp
                            )
                        )

                    }

                }
            }

        }
        if (showAlert) {
            // below line is use to
            // display a alert dialog.
            AlertDialog(
                // on dialog dismiss we are setting
                // our dialog value to false.
                onDismissRequest = { showAlert = false },

                // below line is use to display title of our dialog
                // box and we are setting text color to white.
                title = { Text(text = stringResource(id = R.string.NoResults), style = MaterialTheme.typography.h4) },

                // below line is use to display
                // description to our alert dialog.
                text = { Text(stringResource(id = R.string.ChangeSearch), style = MaterialTheme.typography.body2) },

                // in below line we are displaying
                // our confirm button.
                confirmButton = {
                    // below line we are adding on click
                    // listener for our confirm button.
                    TextButton(
                        onClick = {
                            showAlert = false
                            searchTerm = ""

                        }
                    ) {
                        // in this line we are adding
                        // text for our confirm button.
                        Text("OK", style = MaterialTheme.typography.button.copy(color = teal_500))
                    }
                },
                // in below line we are displaying
                // our dismiss button.
                dismissButton = {
                    // in below line we are displaying
                    // our text button
                    TextButton(
                        // adding on click listener for this button
                        onClick = {
                            showAlert = false

                        }
                    ) {
                        // adding text to our button.
                        Text(stringResource(id = R.string.Back), style = MaterialTheme.typography.button.copy(color = red_500))
                    }
                },
                // below line is use to add background color to our alert dialog
                backgroundColor = if(isSystemInDarkTheme()) Color.DarkGray else Color.White,

                // below line is use to add content color for our alert dialog.
                contentColor = if(isSystemInDarkTheme()) Color.White else Color.Black
            )
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