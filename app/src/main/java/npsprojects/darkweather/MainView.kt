package npsprojects.darkweather

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationCity
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.navigate

@ExperimentalAnimationApi
@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterialApi
@Composable
fun MainPageView(model: WeatherViewModel, controller: NavController) {
    if ((model.myLocations.size + 1 > model.locations.size) || model.locations.isEmpty()) {
        LaunchedEffect(key1 = "GetData") {
            model.getCurrentLocationWeather()
        }
    }
    val scope = rememberCoroutineScope()
    var index: Int by remember { mutableStateOf(0) }

    val state = rememberScaffoldState(
        rememberDrawerState(DrawerValue.Closed)
    )
    var currentPage by remember { mutableStateOf("Main") }
    var offset: Float by remember { mutableStateOf(0f) }
    val swipableModifier = Modifier.draggable(
        orientation = Orientation.Horizontal,
        state = rememberDraggableState { delta ->
            offset = delta

        },
        onDragStopped = {
            if (offset > 0) {
                if (index > 0) {
                    index--
                }
                offset = 0f
            } else if (offset < 0) {
                if (index < model.locations.size - 1) {
                    index++
                }
                offset = 0f
            }
        }

    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),


        ) {
        when (model.isLoading) {
            true -> LoadingView(model = model)

            false ->  if (model.locations.isNotEmpty()) {
                NewMapView(model = model,controller=controller)

            }
            else {
                Text("No data")
            }
//                if (model.locations.size > 0) {
//                Box(contentAlignment = Alignment.BottomCenter) {
//                    MainWeatherCard(
//                        data = model,
//                        index = index,
//
//                        units = model.units,
//                        updateIndex = {
//                            index = it
//                        }, controller = controller
//                    )
//
//                }
//            } else {
//                Box(
//                    modifier = Modifier
//                        .background(color = getWeatherColor(""))
//                        .fillMaxSize(), contentAlignment = Alignment.Center
//                ) {
//                    Column(
//                        modifier = Modifier.fillMaxSize(),
//                        verticalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        Row(
//                            modifier = Modifier
//                                .padding(top = 50.dp, start = 16.dp, end = 16.dp)
//                                .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            IconButton(onClick = {
//                                controller.navigate("Settings")
//                            }) {
//                                Icon(
//                                    Icons.Rounded.Sort,
//                                    tint = Color.White,
//                                    modifier = Modifier.size(18.dp),
//                                    contentDescription = ""
//                                )
//                            }
//
//                            IconButton(onClick = {
//                                controller.navigate("Add")
//                            }) {
//                                Icon(
//                                    Icons.Rounded.LocationCity,
//                                    tint = Color.White,
//                                    modifier = Modifier.size(18.dp),
//                                    contentDescription = ""
//                                )
//                            }
//                        }
//                        Text(
//                            "No places added yet and there is no access to your location.",
//                            style = MaterialTheme.typography.body1.copy(
//                                color = Color.White,
//
//                                ),
//                            modifier = Modifier.padding(20.dp),
//                            textAlign = TextAlign.Center
//                        )
//                        Spacer(modifier = Modifier.height(80.dp))
//                    }
//                }
//
//            }

        }

    }
}





