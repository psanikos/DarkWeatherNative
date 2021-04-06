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
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.rounded.Sort
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
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import npsprojects.darkweather.ui.theme.red_500

@ExperimentalAnimationApi
@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterialApi
@Composable
fun MainPageView(model: WeatherViewModel,controller:NavController) {
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
                if (index < model.locations.count() - 1) {
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

            false -> if (model.locations.count() > 0) {
                Box(contentAlignment = Alignment.BottomCenter) {
                    MainWeatherCard(
                        data = model,
                        index = index,

                        units = model.units,
                        updateIndex = {
                            index = it
                        }, controller = controller
                    )

                }
            } else {
                Box(
                    modifier = Modifier
                        .background(color = getWeatherColor(""))
                        .fillMaxSize(), contentAlignment = Alignment.Center
                ) {
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

        }

    }
}





