package npsprojects.darkweather.views

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Address
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FabPosition
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import npsprojects.darkweather.R
import npsprojects.darkweather.WeatherUnits
import npsprojects.darkweather.ago
import npsprojects.darkweather.getWeatherBackIcon
import npsprojects.darkweather.models.LOCATIONERROR
import npsprojects.darkweather.models.LOCATIONERRORVALUES
import npsprojects.darkweather.models.WeatherModel
import npsprojects.darkweather.models.WeatherViewModel
import java.time.Instant
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.material.ExperimentalMaterialApi::class)
@Composable
fun SmallMain(model: WeatherViewModel, controller: NavController) {
    val index: Int by model.index.observeAsState(initial = 0)
    val isLoading: Boolean by model.isLoading.observeAsState(initial = true)
    val currentLocation by model.currentLocation.observeAsState(initial = listOf<WeatherModel>())
    val insets = LocalWindowInsets.current
    val bottomPadding = with(LocalDensity.current) { insets.systemGestures.bottom.toDp() }
    val isRefreshing by model.isLoading.observeAsState(initial = false)
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val locations by model.locations.observeAsState(listOf())
    var dropExtended by remember {
        mutableStateOf(false)
    }

    when (isLoading) {
        true -> LoadingAnimationScreen(model = model)
        false -> BottomSheetScaffold(
            sheetContent = {
                if(locations.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(state = ScrollState(0)),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(40.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .materialYouFrosted(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier.padding(15.dp),
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                SummaryCard(
                                    current = locations[index].data.current!!,
                                    dayDetails = locations[index].data.daily.first().weather.first().description!!
                                )
                                HourView(
                                    hourly = locations[index].data.hourly,
                                    inSi = model.units == WeatherUnits.SI
                                )
                                DetailsCard(
                                    current = locations[index].data.current!!,
                                    daily = locations[index].data.daily.first(),
                                    inSi = model.units == WeatherUnits.SI
                                )
                                if (locations[index].airQuality != null) {
                                    AirQuality(
                                        aqi = locations[index].airQuality?.list?.first()?.main?.aqi
                                            ?: 1
                                    )
                                }
                                MoonView(
                                    phase = locations[index].data.daily.first().moon_phase!!,
                                    moonrise = locations[index].data.daily.first().moonrise!!,
                                    moonset = locations[index].data.daily.first().moonset!!
                                )
                                WeeklyView(
                                    days = locations[index].data.daily,
                                    inSi = model.units == WeatherUnits.SI
                                )
                                Spacer(modifier = Modifier.height(40.dp))
                            }
                        }
                    }
                }
            },
            topBar = {

                AnimatedVisibility(visible = locations.isNotEmpty() || currentLocation.isNotEmpty()) {
                    SmallTopAppBar(
                        title = {
                            Column(
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.fillMaxWidth(0.8f)
                            ) {

                                Box() {
                                    if (!(locations.isNotEmpty() && locations.size > index) && currentLocation.isEmpty()) {
                                        Text(
                                            "N/A",
                                            style = MaterialTheme.typography.displayMedium.copy(
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        )
                                    } else {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.clickable(onClick = {
                                                dropExtended = !dropExtended
                                            })
                                        ) {
                                            if (locations[index].isCurrent) {
                                                Icon(
                                                    FontAwesomeIcons.Solid.LocationArrow,
                                                    modifier = Modifier.size(15.dp),
                                                    contentDescription = "",
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                            Text(
                                                locations[index].location.name,
                                                style = MaterialTheme.typography.displayMedium.copy(
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            )
                                        }
                                    }
                                    DropdownMenu(
                                        expanded = dropExtended,
                                        onDismissRequest = { /*TODO*/ }) {


                                        locations.forEachIndexed { index, item ->
                                            DropdownMenuItem(
                                                onClick = {
                                                    model.changeIndex(index)

                                                    dropExtended = false
                                                },
                                                modifier = Modifier.width(160.dp)
                                            ) {
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(
                                                        5.dp
                                                    ),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    if (item.isCurrent) {
                                                        Icon(
                                                            FontAwesomeIcons.Solid.LocationArrow,
                                                            modifier = Modifier.size(15.dp),
                                                            contentDescription = "",
                                                        )
                                                    }
                                                    Text(
                                                        item.location.name,
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                }
                                            }
                                        }

                                    }

                                }

                                Text(
                                    if (!(locations.isNotEmpty() && locations.size > index)) "No data" else
                                        Date.from(Instant.ofEpochSecond(locations[index].data.current!!.dt!!))
                                            .ago(),
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(start = 5.dp)
                                )
                            }
                        },
                        actions = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (locations.isNotEmpty() && locations.size > index) {

                                    if (!locations[index].isCurrent) {
                                        TextButton(onClick = {

                                            if (model.myLocations.firstOrNull {
                                                    it.latitude == locations[index].location.latitude
                                                            && it.longitude == locations[index].location.longitude
                                                } != null) {
                                                Log.i("Heart", "removing")
                                                model.isLoading.value = true
                                                val curIndex = index
                                                model.changeIndex(0)
                                                scope.launch {
                                                    model.remove(
                                                        locations[curIndex].location,
                                                        context = context
                                                    )
                                                }
                                                model.isLoading.value = false
                                            } else {
                                                Log.i("Heart", "saving")
                                                model.isLoading.value = true
                                                scope.launch {
                                                    model.saveLocation(
                                                        locations[index].location,
                                                        context = context
                                                    )
                                                }

                                                model.isLoading.value = false
                                            }
                                        }) {

                                            ColoredIcon(
                                                if (model.myLocations.firstOrNull {
                                                        it.latitude == locations[index].location.latitude
                                                                && it.longitude == locations[index].location.longitude
                                                    } != null)
                                                    Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                contentDescription = "",
                                                modifier = Modifier.size(30.dp),
                                                padding = 6.dp,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }

                                    }

                                    if (locations[index].isCurrent) {
                                        TextButton(onClick = {
                                            controller.navigate("Map")
                                        }) {
                                            ColoredIcon(
                                                imageVector = FontAwesomeIcons.Solid.Map,
                                                contentDescription = "",
                                                modifier = Modifier.size(30.dp),
                                                padding = 6.dp,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }

                                }
                                TextButton(onClick = {
                                    controller.navigate("Search")
                                }) {
                                    Box() {

                                        ColoredIcon(
                                            imageVector = FontAwesomeIcons.Solid.Search,
                                            contentDescription = "",
                                            modifier = Modifier.size(30.dp),
                                            padding = 6.dp,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                TextButton(onClick = {
                                    controller.navigate("Settings")
                                }) {

                                    ColoredIcon(
                                        imageVector = FontAwesomeIcons.Solid.EllipsisV,
                                        contentDescription = "",
                                        modifier = Modifier.size(30.dp),
                                        padding = 6.dp,
                                        tint = MaterialTheme.colorScheme.primary
                                    )


                                }
                            }

                        },
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            titleContentColor = MaterialTheme.colorScheme.tertiary,
                            containerColor = Color.Transparent
                        ),
                        modifier = Modifier.statusBarsPadding()
                    )

                }
            },
            sheetPeekHeight = if(locations.isNotEmpty()) (LocalConfiguration.current.screenHeightDp*0.55).dp else 0.dp,
            sheetShape = RoundedCornerShape(12.dp),
            backgroundColor = MaterialTheme.colorScheme.background,
            sheetBackgroundColor = MaterialTheme.colorScheme.tertiaryContainer

            ) {

            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing),
                onRefresh = {
                    scope.launch {
                        model.initActions(context = context)
                    }
                }
            ) {
                when (locations.size) {
                    0 -> EmptyView(model = model)
                    else -> Column(Modifier.verticalScroll(rememberScrollState())) {
                        Image(
                            painter = painterResource(
                                id = if (locations.isNotEmpty())
                                    getWeatherBackIcon(locations[index].data.current!!.weather.first().icon!!) else R.drawable.clearday
                            ),
                            contentDescription = "",
                            modifier = Modifier.size(500.dp), contentScale = ContentScale.Fit
                        )
                    }
                }
                }
            }


        }
    }







@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmptyView(model: WeatherViewModel){
    var searchTerm by remember {
        mutableStateOf("")
    }
    var searchedAddresses: MutableList<Address> by remember { mutableStateOf(mutableListOf()) }
    val scope = rememberCoroutineScope()
    var showAlert: Boolean by remember { mutableStateOf(false) }
    val context = LocalContext.current
     fun requestLocationPermission(context: Context) {
        val MY_PERMISSIONS_REQUEST_LOCATION = 99
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            MY_PERMISSIONS_REQUEST_LOCATION
        )
         model.resetError()
    }
    val error by model.error.observeAsState(null)

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 15.dp, vertical = 20.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(5.dp))
                Column(
                    modifier = Modifier
                        .padding(horizontal = 15.dp, vertical = 20.dp)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {

                    Icon(
                        FontAwesomeIcons.Solid.GlobeAmericas, contentDescription = "",
                        modifier = Modifier.size(140.dp), tint = MaterialTheme.colorScheme.primary
                    )
                    Row(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                shape = RoundedCornerShape(50)
                            )
                            .height(40.dp)
                            .fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "",
                            modifier = Modifier
                                .size(30.dp)
                                .padding(start = 15.dp),
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        BasicTextField(
                            value = searchTerm,
                            onValueChange = {
                                searchTerm = it
                            },
                            keyboardActions = KeyboardActions(onSearch = {

                                scope.launch(Dispatchers.IO) {
                                    val addresses =
                                        WeatherViewModel.getCoordinatesFromLocation(
                                            context = context,
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
                                    Text(
                                        stringResource(id = R.string.searchText),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                innerTextField()

                            }

                        )

                    }
//                    if (searchedAddresses.size == 0) {
//                        IconButton(onClick = {
//                            scope.launch {
//                                model.initActions(context = context)
//                            }
//                        }) {
//                            androidx.compose.material.Icon(
//                                Icons.Default.Refresh, contentDescription = "",
//                                tint = Color.White,
//                                modifier = Modifier
//                                    .size(45.dp)
//                                    .background(Color.DarkGray, shape = CircleShape)
//                                    .padding(5.dp)
//                            )
//                        }
//                    }
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
                                            model.getSearchedLocationData(
                                                name = it.locality ?: it.subLocality,
                                                longitude = it.longitude,
                                                latitude = it.latitude,
                                                context = context
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
                                        vertical = 8.dp),
                                    color = if(isSystemInDarkTheme()) Color.White else Color.DarkGray
                                )

                                Text(
                                    it.countryName ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(
                                        horizontal = 10.dp,
                                        vertical = 8.dp
                                    ),
                                    color = if(isSystemInDarkTheme()) Color.White else Color.DarkGray
                                )

                            }

                        }
                    }

                }
                if (searchedAddresses.size == 0) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 30.dp)
                    ) {

                        IconButton(onClick = {
                          if (error != LOCATIONERRORVALUES.LOCATION_FULL_DENIED){
                              requestLocationPermission(context = context)
                          }
                            else if(error == null){
                              scope.launch {
                                  model.initActions(context = context)
                              }
                          }
                            else{
                              val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                              intent.data = Uri.parse("package:" + context.packageName)
                              ContextCompat.startActivity(context, intent, null)
                          }
                        }) {
                            Text(
                                "Tap to allow access ",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                            )
                        }
                    }


                }
                else{
                    Spacer(modifier = Modifier.height(5.dp))
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
                    title = {
                        Text(
                            text = stringResource(id = R.string.NoResults),
                            style = MaterialTheme.typography.displayLarge
                        )
                    },

                    // below line is use to display
                    // description to our alert dialog.
                    text = {
                        Text(
                            if (WeatherViewModel.LocationFetcher.isOnline(context = context)) stringResource(
                                id = R.string.noInternet
                            ) else
                                stringResource(id = R.string.ChangeSearch),
                            style = MaterialTheme.typography.bodySmall
                        )
                    },

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
                        TextButton(
                            onClick = {
                                showAlert = false
                            },
                        ) {
                            Text(
                                stringResource(id = R.string.Back),
                                style = MaterialTheme.typography.displayMedium
                            )

                        }

                    }, icon = {
                        Icon(Icons.Default.Warning, contentDescription = null)
                    }

                )
            }
        }

}