package com.npsappprojects.darkweather

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.twotone.CheckBox
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.npsappprojects.darkweather.ui.theme.green_400
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterialApi
@Composable
fun MainPageView(model: WeatherViewModel){
    var index:Int by remember { mutableStateOf(0) }
    var offset:Float by remember { mutableStateOf(0f) }
    val state = rememberScaffoldState()
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
                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween,verticalAlignment = Alignment.CenterVertically){
                    IconButton(onClick = {
                        GlobalScope.launch {
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
                                Text(
                                    text = "No location",
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
            }
        },
        drawerContent = {
            SettingsView(model = model)
        },
        drawerBackgroundColor = Color(0xFFD7E0EB),
        drawerElevation = 0.dp,
        drawerShape = RoundedCornerShape(0),
        drawerGesturesEnabled = false


    ) {
        when(model.isLoading) {
            true -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            false -> Crossfade(targetState = currentPage) { screen ->
                when (screen) {

                    "Main" -> if (model.locations.count() > 0) {
                        MainWeatherCard(
                            locationData = model.locations[index].data,
                            locationName = model.locations[index].name,
                            isCurrent = model.locations[index].isCurrent
                        )
                    } else {
                        LoadingView()
                    }
                    "AddNew" -> AddPlaceView(model = model)

                }
            }

        }

    }

}


@ExperimentalFoundationApi
@Composable
fun AddPlaceView(model: WeatherViewModel){
  var searchTerm:String by remember { mutableStateOf("")}

Box(modifier = Modifier
    .fillMaxSize()
    .background(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF9AABBC),
                Color(0xFF4A526D)
            )
        )
    )) {
 Column(modifier = Modifier.fillMaxSize(),verticalArrangement = Arrangement.Top,horizontalAlignment = Alignment.CenterHorizontally) {
     Spacer(modifier = Modifier.height(30.dp))
Box(modifier = Modifier
    .padding(horizontal = 16.dp)
    .fillMaxWidth()

    .height(40.dp)
    .background(color = Color.White, shape = RoundedCornerShape(12)),contentAlignment = Alignment.CenterStart){
    BasicTextField(value = searchTerm,onValueChange = {
        searchTerm = it

    },keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text,imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
        model.getCoordinatesFromLocation(searchTerm)
        }),
        modifier = Modifier
            .padding(start = 20.dp)
            .fillMaxWidth(0.9f)
        ,textStyle = MaterialTheme.typography.caption,

        )
    if (searchTerm == "") {
        Text(
            "Search a new place",
            style = MaterialTheme.typography.caption.copy(color = Color.DarkGray),
            modifier = Modifier
                .padding(start = 20.dp)
        )
    }
}
     Spacer(modifier = Modifier.height(30.dp))
     model.searchedAdresses.forEach {
    Row(modifier = Modifier
        .padding(horizontal = 20.dp, vertical = 5.dp)
        .fillMaxWidth()
        .height(50.dp)
        .background(color = Color.White.copy(alpha = 0.5f), shape = RoundedCornerShape(12))
        .clickable {
            model.saveLocation(address = it)
            searchTerm = ""
            model.searchedAdresses.clear()
        },
    horizontalArrangement = Arrangement.SpaceBetween,verticalAlignment = Alignment.CenterVertically) {
    Text(it.locality,style = MaterialTheme.typography.body2,modifier = Modifier.padding(horizontal = 16.dp,vertical = 8.dp))
    Icon(Icons.TwoTone.CheckBox,contentDescription = "",tint = green_400,modifier = Modifier
        .size(25.dp)
        .padding(end = 10.dp))
}
    }
     LazyVerticalGrid(cells = GridCells.Fixed(count = 2),modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth()) {

             model.myLocations.forEach {
                 item {
                     Box(
                         modifier = Modifier
                             .height(100.dp)
                             .width(100.dp)
                             .padding(10.dp)
                             .background(
                                 brush = Brush.verticalGradient(
                                     colors = listOf(
                                         Color.White.copy(
                                             alpha = 0.3f
                                         ), Color.White.copy(alpha = 0.1f)
                                     )
                                 ), shape = RoundedCornerShape(20)
                             ),
                         contentAlignment = Alignment.Center){
                         Text(it.name, style = MaterialTheme.typography.body2)
                     }
                 }

         }

     }
 }
}
}

@Composable
fun LoadingView(){
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.DarkGray
    ) {

    }
}