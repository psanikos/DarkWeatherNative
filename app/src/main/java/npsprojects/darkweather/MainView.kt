package npsprojects.darkweather

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.twotone.Warning
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import npsprojects.darkweather.ui.theme.red_500

@ExperimentalAnimationApi
@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterialApi
@Composable
fun MainPageView(model: WeatherViewModel){
    LaunchedEffect(key1 = "GetData" ){
        model.getCurrentLocationWeather()
    }
    val scope = rememberCoroutineScope()
    var index:Int by remember { mutableStateOf(0) }
    var offset:Float by remember { mutableStateOf(0f) }
    val state  = rememberScaffoldState(
        rememberDrawerState(DrawerValue.Closed)
    )
    var currentPage by remember { mutableStateOf("Main") }

    val swipableModifier = Modifier.draggable(
        orientation = Orientation.Horizontal,
        state = rememberDraggableState { delta ->
            offset = delta

        },
        onDragStopped = {
            if (offset > 0){
                if (index > 0){
                    index --
                }
                offset = 0f
            }
            else if (offset < 0){
                if (index < model.locations.count() - 1) {
                    index++
                }
                offset = 0f
            }
        }

    )

    Scaffold(modifier = swipableModifier.fillMaxSize(),
        scaffoldState = state,
        topBar = {
            TopAppBar(

                backgroundColor = if(currentPage == "AddNew") Color(0xFF9AABBC) else if (!model.isLoading && model.locations.isNotEmpty()) getWeatherColor(input = model.locations[index].data.currently.icon!!) else getWeatherColor(input = ""),
                elevation = 0.dp,

                modifier = Modifier.height(90.dp),contentPadding = PaddingValues(top = 30.dp,start = 8.dp,end = 8.dp)
            ){

AnimatedVisibility(visible = !model.isLoading) {
    Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween,verticalAlignment = Alignment.CenterVertically){
        IconButton(onClick = {

            scope.launch {
                state.drawerState.open()
            }

        }) {
            Image(
                painter = painterResource(id = R.drawable.menu), contentDescription = "",
                colorFilter = ColorFilter.tint(color = Color.White),
                modifier = Modifier.size(20.dp)
            )
        }
        Row(

            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if(currentPage == "AddNew") {
                Text(
                    text = "Add new location",
                    style = MaterialTheme.typography.body2.copy(color = Color.White)
                )
            }
            else {
                if (!model.isLoading && model.locations.isNotEmpty()) {

                    if (model.locations[index].isCurrent) {
                        Icon(
                            Icons.Filled.LocationOn,
                            contentDescription = "",
                            modifier = Modifier.size(18.dp),
                            tint = Color.White
                        )

                        Spacer(modifier = Modifier.width(4.dp))


                    }
                    Text(
                        text = model.locations[index].name,
                        style = MaterialTheme.typography.body2.copy(color = Color.White)
                    )
                } else {
                    if (model.error == WeatherError.NOGPS) {
                        Icon(
                            Icons.TwoTone.Warning,
                            modifier = Modifier.size(20.dp),
                            contentDescription = "",
                            tint = red_500
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = if (model.error == WeatherError.NOGPS) "No location access" else "No location",
                        style = MaterialTheme.typography.body2.copy(color = Color.White)
                    )
                }
                IconButton(onClick = {
                    model.getCurrentLocationWeather()
                }) {
                    Icon(Icons.Filled.Refresh,tint = Color.White,contentDescription = "")
                }
            }
        }



        IconButton(onClick = {
            currentPage = if(currentPage == "Main"){
                "AddNew"
            } else {
                "Main"
            }
        }) {
            Icon(if(currentPage == "AddNew") Icons.Filled.ArrowBack else Icons.Filled.Add,tint = Color.White, modifier = Modifier.size(18.dp),contentDescription = "")
        }


    }

}            }
        },
        drawerContent = {
            SettingsView(model = model)
        },
        drawerBackgroundColor = Color(0xFFD7E0EB),
        drawerElevation = 0.dp,
        drawerShape = RoundedCornerShape(0),
        drawerGesturesEnabled = true


    ) {
        when(model.isLoading) {
            true ->  LoadingView(model = model)

            false -> Crossfade(targetState = currentPage) { screen ->
                when (screen) {

                    "Main" -> if (model.locations.count() > 0) {
                        MainWeatherCard(
                            locationData = model.locations[index].data,

                        )
                    } else {
                        Box(modifier = Modifier
                            .background(color = getWeatherColor(""))
                            .fillMaxSize(),contentAlignment = Alignment.Center) {
                      Column() {

                          Text(
                              "No places added yet and there is no access to your location.",
                              style = MaterialTheme.typography.body1.copy(
                                  color = Color.White,

                                  ),
                              modifier = Modifier.padding(20.dp),
                              textAlign = TextAlign.Center
                          )
                          Spacer(modifier = Modifier.height(80.dp))
                      }
                        }
                    }
                    "AddNew" -> AddPlaceView(model = model,resetIndex = {
                        index = 0
                    })

                }
            }

        }

    }

    }


