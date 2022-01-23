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
import androidx.compose.material.TextField
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.insets.statusBarsPadding
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.ArrowLeft
import compose.icons.fontawesomeicons.solid.LocationArrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import npsprojects.darkweather.Coordinates
import npsprojects.darkweather.R
import npsprojects.darkweather.models.WeatherViewModel
import npsprojects.darkweather.ui.theme.*

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
//                        style = MaterialTheme.typography.bodySmall
//                    ) else null
//                },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = if(isSystemInDarkTheme()) Color.White else Color.Black),

                    )

                if (searchTerm == "") {
                    Text(
                        text = "Search",
                        style = MaterialTheme.typography.bodySmall.copy(color = if(isSystemInDarkTheme()) Color.White else Color.Black),
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
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 8.dp
                            )
                        )

                        Text(
                            it.countryName ?: "",
                            style = MaterialTheme.typography.bodySmall,
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
                title = { Text(text = stringResource(id = R.string.NoResults), style = MaterialTheme.typography.displayLarge) },

                // below line is use to display
                // description to our alert dialog.
                text = { Text(stringResource(id = R.string.ChangeSearch), style = MaterialTheme.typography.bodySmall) },

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
                        Text("OK", style = MaterialTheme.typography.displayMedium.copy(color = teal_500))
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
                        Text(stringResource(id = R.string.Back), style = MaterialTheme.typography.displayMedium.copy(color = red_500))
                    }
                }
            )
        }

    }

}


@OptIn(ExperimentalMaterial3Api::class)
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
            MediumTopAppBar(
                title = {
                    Text(stringResource(R.string.add_location),
                        style = androidx.compose.material3.MaterialTheme.typography.headlineLarge
                      )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        controller.popBackStack()
                    }) {
                        Icon(
                            FontAwesomeIcons.Solid.ArrowLeft,contentDescription = "",
                            modifier = Modifier.size(25.dp))
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
                ,
                modifier = Modifier.statusBarsPadding()
            )


        },
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 15.dp, vertical = 20.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
                Row(modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(40)
                    )
                    .height(40.dp)
                    .fillMaxWidth()


               ,horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Search, contentDescription = "",
                    modifier = Modifier
                        .size(30.dp)
                        .padding(start = 15.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
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
//                },
                        textStyle = MaterialTheme.typography
                            .bodyMedium.copy(color = MaterialTheme.colorScheme.onPrimaryContainer),
                        decorationBox = { innerTextField ->


                                if (searchTerm.isEmpty()) {
                                    Text("Search for location", style = MaterialTheme.typography.bodySmall)
                                }

                                innerTextField()

                        }

                    )

            }
            searchedAddresses.forEach {

                if (it.locality != null || it.featureName != null) {

                    Row(
                        modifier = Modifier
                            .padding(vertical = 5.dp)
                            .fillMaxWidth()
                            .height(40.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface,
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
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 8.dp
                            )
                        )

                        Text(
                            it.countryName ?: "",
                            style = MaterialTheme.typography.bodySmall,
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
                title = { Text(text = stringResource(id = R.string.NoResults), style = MaterialTheme.typography.displayLarge) },

                // below line is use to display
                // description to our alert dialog.
                text = { Text(stringResource(id = R.string.ChangeSearch), style = MaterialTheme.typography.bodySmall) },

                // in below line we are displaying
                // our confirm button.
                confirmButton = {
                    // below line we are adding on click
                    // listener for our confirm button.
                    TextButton(onClick = {
                        showAlert = false
                        searchTerm = ""
                    }) {
                        Text("OK", style = MaterialTheme.typography.displayMedium)

                    }

                },
                // in below line we are displaying
                // our dismiss button.
                dismissButton = {
                    TextButton(onClick = {
                        showAlert = false
                    },
                    ) {
                        Text(stringResource(id = R.string.Back), style = MaterialTheme.typography.displayMedium)

                    }

                }, icon = {
                    Icon(Icons.Default.Warning,contentDescription = null)
                    }

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