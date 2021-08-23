package npsprojects.darkweather.views

import android.location.Address
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Autorenew
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.insets.LocalWindowInsets
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileOverlay
import kotlinx.coroutines.launch
import npsprojects.darkweather.models.SavedLocation
import npsprojects.darkweather.models.WeatherViewModel
import npsprojects.darkweather.ui.theme.*
import java.text.SimpleDateFormat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import npsprojects.darkweather.*
import npsprojects.darkweather.R

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun NewMapViewBig(model: WeatherViewModel, controller: NavController) {
    val map = rememberMapViewWithLifecycle()
    val scope = rememberCoroutineScope()
    var mapType by remember { mutableStateOf("clouds_new") }
    var coordinates by remember {
        mutableStateOf(
            if (model.locations.isNotEmpty()) LatLng(
                model.locations[0].data.lat,
                model.locations[0].data.lon
            ) else LatLng(37.9838, 23.7275)
        )
    }
    var zoom by rememberSaveable(map) { mutableStateOf(InitialZoom) }
    var index: Int by remember { mutableStateOf(0) }
    val overlays: MutableList<TileOverlay> by remember { mutableStateOf(ArrayList<TileOverlay>()) }
    var searchTerm by remember { mutableStateOf("") }
    val backColor = if (isSystemInDarkTheme())  Color(0xFF303030) else Color(0xFFf0f0f7)
    val cardColor = if (isSystemInDarkTheme()) Color(0xFF151515) else Color.White
    var isLoading by remember { mutableStateOf(false) }
    var isAlertExpanded by remember { mutableStateOf(false) }
    val insets = LocalWindowInsets.current
    val state = rememberBottomSheetScaffoldState(drawerState = DrawerState(initialValue = DrawerValue.Closed))
    var showAlert:Boolean by remember { mutableStateOf(false) }
   var showBar by remember {
       mutableStateOf(false)
   }
    var searchedAddresses:MutableList<Address> by remember { mutableStateOf(mutableListOf())}

    var offset = animateOffsetAsState(targetValue = if(showBar) Offset(x = 0f, y = 0f) else Offset(x = 400f, y= 0f))

        if (!isLoading) {
            Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.fillMaxSize()) {


                Box(
                    contentAlignment = Alignment.CenterEnd, modifier = Modifier
                        .fillMaxSize()
                ) {
                    MapViewContainer(
                        map = map,
                        latitude = coordinates.latitude,
                        longitude = coordinates.longitude,
                        mapType = mapType,
                        model = model,
                        index = index
                    )
                        Row(modifier = Modifier
                            .wrapContentWidth()
                            .offset(x = offset.value.x.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Button(onClick = {
                                         showBar = !showBar
                        },
                            shape = CircleShape,
                        modifier = Modifier
                            .padding(top = 150.dp),contentPadding = PaddingValues(10.dp)
                           ) {
                            Icon(if(showBar) Icons.Default.Cancel else Icons.Default.ArrowBackIos,
                            contentDescription = "",tint = Color.White)
                        }

                            Surface(
                                modifier = Modifier
                                    .padding(top = 100.dp)
                                    .width(400.dp)
                                    .fillMaxHeight(),
                                shape = RoundedCornerShape(6),
                                color = MaterialTheme.colors.surface.copy(alpha = 0.3f),
                                contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black
                            ) {

                            }
                        }
                }
                    //Top bar
                Row(
                    modifier = Modifier
                        .padding(10.dp)
                        .padding(top = 30.dp)
                        .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {

                    Row{
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        Box(
                            modifier = Modifier
                                .width(360.dp)
                                .height(45.dp)
                                .background(
                                    color = cardColor,
                                    shape = RoundedCornerShape(20.dp)
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
                                            showAlert = true
                                        }
                                        else {
                                            searchedAddresses = addresses
                                        }
                                    }
                                }),
                                modifier = Modifier
                                    .padding(start = 20.dp)
                                    .fillMaxWidth(0.9f),
                                textStyle = MaterialTheme.typography.caption.copy(color = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray),
                                cursorBrush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Blue,
                                        Color.Gray
                                    )
                                )

                            )
                            if (searchTerm == "") {
                                Text(
                                    stringResource(R.string.searchText),
                                    style = MaterialTheme.typography.caption.copy(color = if (isSystemInDarkTheme()) Color.White else Color.Black),
                                    modifier = Modifier
                                        .padding(start = 20.dp)
                                )
                            }
                        }
                        LazyRow(

                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.width(400.dp)

                            ) {

                            itemsIndexed(model.locations) { i, item ->
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = if (index == i) cardColor else Color.Transparent,
                                            shape = RoundedCornerShape(50)
                                        )
                                        .clickable {
                                            coordinates =
                                                LatLng(
                                                    item.data.lat,
                                                    item.data.lon
                                                )
                                            index = i

                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(modifier = Modifier.padding(4.dp)) {
                                        if (item.isCurrent) {
                                            Icon(
                                                Icons.Rounded.LocationOn,
                                                tint = if (index == i) (if (isSystemInDarkTheme()) Color.White else Color.Black) else Color.Gray,
                                                contentDescription = "",
                                                modifier = Modifier.size(25.dp)
                                            )
                                        }
                                        Text(
                                            item.name,
                                            style = MaterialTheme.typography.button.copy(
                                                color = if (index == i) (if (isSystemInDarkTheme()) Color.White else Color.Black) else Color.Gray
                                            ),
                                            modifier = Modifier.padding(
                                                horizontal = 10.dp,
                                                vertical = 5.dp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        //Searches
                        searchedAddresses.forEach {
                            if (it.locality != null || it.featureName != null) {

                                Row(
                                    modifier = Modifier
                                        .padding(vertical = 5.dp)
                                        .width(340.dp)
                                        .height(40.dp)
                                        .background(
                                            color = cardColor,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .clickable {
                                            scope.launch {
                                                isLoading = true
                                                model.getCoordinatesWeather(
                                                  location = Coordinates(it.latitude,it.longitude))
                                                    searchTerm = ""
                                                delay(1000)
                                                isLoading = false
                                                }
                                        },
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        it.locality ?: it.featureName,
                                        style = MaterialTheme.typography.body2.copy(color = if(isSystemInDarkTheme()) Color.White else Color.Black),
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

                        //Alerts
                      AnimatedVisibility(visible = searchedAddresses.size == 0 ) {
                          if (model.locations.isNotEmpty()) {
                              Box(modifier = Modifier.width(360.dp)) {
                                  LazyRow(
                                      modifier = Modifier.fillMaxWidth(),
                                      horizontalArrangement = Arrangement.spacedBy(10.dp)
                                  ) {

                                  }
                              }
                          }
                      }
                    }
                    IconButton(onClick = {
                        model.error.value = WeatherError.NONE
                        model.initActions()
                    }) {
                        Box(
                            modifier = Modifier
                                .height(30.dp)
                                .width(30.dp)
                                .background(
                                    color = Color(0xFF101010),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Rounded.Autorenew,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp),
                                contentDescription = ""
                            )
                        }
                    }
                }

                    IconButton(onClick = {
                        controller.navigate("Settings")
                    }) {
                        Icon(
                            Icons.Rounded.Settings,
                            tint = if (isSystemInDarkTheme()) Color.White else Color.Black,
                            modifier = Modifier.size(25.dp),
                            contentDescription = ""
                        )


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

                Box(modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 60.dp)
                    .fillMaxSize(),
                contentAlignment = Alignment.BottomStart){
                    Column(

                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {

                                mapType = "clouds_new"

                            },
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.White,
                                backgroundColor = indigo_500.copy(alpha = if (mapType == "clouds_new") 0.6f else 0.3f)
                            ),
                            contentPadding = PaddingValues(10.dp),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .width(120.dp)
                                .height(40.dp),

                            elevation = ButtonDefaults.elevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 0.dp,
                                disabledElevation = 0.dp
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Icon(
                                    Icons.Filled.Cloud,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    stringResource(R.string.clouds),
                                    style = MaterialTheme.typography.caption
                                )
                            }
                        }

                        Button(
                            onClick = {
                                mapType = "precipitation_new"
                            },
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.White,
                                backgroundColor = indigo_500.copy(alpha = if (mapType == "precipitation_new") 0.6f else 0.3f)
                            ),
                            contentPadding = PaddingValues(10.dp),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .width(120.dp)
                                .height(40.dp)

                                ,
                            elevation = ButtonDefaults.elevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 0.dp,
                                disabledElevation = 0.dp
                            ),

                        ) {
                            Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start) {

                                Icon(
                                    Icons.Filled.Opacity,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    stringResource(R.string.rain),
                                    style = MaterialTheme.typography.caption
                                )
                            }
                        }
                        Button(
                            onClick = {
                                mapType = "temp_new"


                            },
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.White,
                                backgroundColor = indigo_500.copy(alpha = if (mapType == "temp_new") 0.6f else 0.3f)
                            ),
                            contentPadding = PaddingValues(10.dp),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .width(120.dp)
                                .height(40.dp),
                            elevation = ButtonDefaults.elevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 0.dp,
                                disabledElevation = 0.dp
                            )
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start) {
                                Icon(
                                    Icons.Filled.Thermostat,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    stringResource(R.string.Temperature),
                                    style = MaterialTheme.typography.caption
                                )
                            }
                        }
                        Button(
                            onClick = {
                                mapType = "wind_new"
                            },
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.White,
                                backgroundColor = indigo_500.copy(alpha = if (mapType == "wind_new") 0.6f else 0.3f)
                            ),
                            contentPadding = PaddingValues(10.dp),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .width(120.dp)
                                .height(40.dp),
                            elevation = ButtonDefaults.elevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 0.dp,
                                disabledElevation = 0.dp
                            )
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start) {
                                Icon(
                                    Icons.Filled.Air,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    stringResource(R.string.Wind),
                                    style = MaterialTheme.typography.caption
                                )
                            }
                        }
                    }
                }
            }
        }


}


