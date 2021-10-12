package npsprojects.darkweather.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.insets.statusBarsPadding
import compose.icons.AllIcons
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.*
import kotlinx.coroutines.launch
import npsprojects.darkweather.R
import npsprojects.darkweather.ago
import npsprojects.darkweather.models.SavedLocation
import npsprojects.darkweather.models.WeatherViewModel
import npsprojects.darkweather.round
import npsprojects.darkweather.timeAgo
import npsprojects.darkweather.ui.theme.red_700
import npsprojects.darkweather.ui.theme.teal_500
import java.time.Instant
import java.util.*

@Composable
fun TopBarView(model: WeatherViewModel, controller: NavController,color: Color){

    var dropExtended by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    val index:Int by model.index.observeAsState(initial = 0)
    Row(modifier = Modifier
        .background(
            color = if (isSystemInDarkTheme()) Color(0xFF353535).copy(alpha = 0.1f) else Color.White.copy(
                alpha = 0.1f
            )
        )
        .statusBarsPadding()
        .padding(horizontal = 15.dp)
        .fillMaxWidth()


        ,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth(0.55f)) {

            Box() {
                if (!(model.locations.isNotEmpty() && model.locations.size > index)) {
                    Text("N/A", style = MaterialTheme.typography.h4)
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable(onClick = { dropExtended = !dropExtended  })
                    ) {
                        if (model.locations[index].isCurrent) {
                            Icon(FontAwesomeIcons.Solid.LocationArrow,modifier = Modifier.size(15.dp), contentDescription = "")
                        }
                        Text(
                            model.locations[index].name,
                            style = MaterialTheme.typography.h4

                        )
                    }
                }
                DropdownMenu(expanded = dropExtended, onDismissRequest = { /*TODO*/ }) {


                    model.locations.forEachIndexed { index, item ->
                        DropdownMenuItem(onClick = {
                            model.indexChange(index)

                            dropExtended = false
                        },
                            modifier = Modifier.width(160.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (item.isCurrent) {
                                    Icon(FontAwesomeIcons.Solid.LocationArrow,modifier = Modifier.size(15.dp), contentDescription = "")
                                }
                                Text(
                                    item.name,
                                    style = MaterialTheme.typography.body2
                                )
                            }
                        }
                    }

                }
            }

            Text(  if (!(model.locations.isNotEmpty() && model.locations.size > index)) "No data" else
                Date.from(Instant.ofEpochSecond(model.locations[index].data.current.dt)).ago(),
                style =  MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.primary.copy(alpha = 0.7f)),modifier = Modifier.padding(start = 5.dp))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp),verticalAlignment = Alignment.CenterVertically) {
            if (model.locations.isNotEmpty() && model.locations.size > index) {
                if (model.myLocations.any { it.latitude.round(2) == model.locations[index].data.lat.round(2) && it.longitude.round(2) == model.locations[index].data.lon.round(2) }) {
                    if (!model.locations[index].isCurrent) {
                        Box(
                            modifier = Modifier
                                .clickable {
                                    val oldIndex = index
                                    model.indexChange(0)
                                    scope.launch {
                                        model.remove(
                                            SavedLocation(
                                                model.locations[oldIndex].name,
                                                model.locations[oldIndex].data.lat.round(
                                                    2
                                                ),
                                                model.locations[oldIndex].data.lon.round(
                                                    2
                                                )
                                            )
                                        )
                                    }
                                }, contentAlignment = Alignment.Center
                        ) {

                            Icon(Icons.Default.Favorite, contentDescription = "",tint = Color.Red,
                            modifier = Modifier.size(30.dp))
                        }
                    }
                } else {
                    if (!model.locations[index].isCurrent) {
                        Box(
                            modifier = Modifier

                                .clickable {
                                    if (!model.myLocations.any { it.latitude.round(2) == model.locations[index].data.lat.round(2) && it.longitude.round(2) == model.locations[index].data.lon.round(2) }) {
                                        scope.launch {
                                            model.saveLocation(
                                                SavedLocation(
                                                    model.locations[index].name,
                                                    model.locations[index].data.lat.round(
                                                        2
                                                    ),
                                                    model.locations[index].data.lon.round(
                                                        2
                                                    )
                                                )
                                            )
                                        }
                                    }
                                }, contentAlignment = Alignment.Center
                        ) {
                                Icon(
                                    Icons.Default.FavoriteBorder,
                                    contentDescription = "",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(30.dp)
                                )
                            }

                    }
                }
            }
            IconButton(onClick = {
                controller.navigate("Search")
            }) {
                Box() {

                    Icon(
                        FontAwesomeIcons.Solid.Search, contentDescription = "",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            IconButton(onClick = {
                controller.navigate("Settings")
            }) {
                Box() {

                    Icon(
                        FontAwesomeIcons.Solid.EllipsisV, contentDescription = "",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
@Composable
fun CompactTopBarView(model: WeatherViewModel, controller: NavController){

    var dropExtended by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    val index:Int by model.index.observeAsState(initial = 0)
    Row(modifier = Modifier
        .padding(top = 20.dp)
        .padding(horizontal = 20.dp)
        .fillMaxWidth()
        .height(60.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth(0.75f)) {

            Box() {
                if (model.locations.isEmpty()) {
                    Text("N/A", style = MaterialTheme.typography.h3)
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable(onClick = { dropExtended = !dropExtended  })
                    ) {
                        if (model.locations[index].isCurrent) {
                            Icon(FontAwesomeIcons.Solid.LocationArrow,modifier = Modifier.size(15.dp), contentDescription = "")
                        }
                        Text(
                            model.locations[index].name,
                            style = MaterialTheme.typography.h3
                        )
                    }
                }
                DropdownMenu(expanded = dropExtended, onDismissRequest = { /*TODO*/ }) {


                    model.locations.forEachIndexed { index, item ->
                        DropdownMenuItem(onClick = {
                            model.indexChange(index)

                            dropExtended = false
                        },
                            modifier = Modifier.width(200.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (item.isCurrent) {
                                    Icon(FontAwesomeIcons.Solid.LocationArrow,modifier = Modifier.size(15.dp), contentDescription = "")
                                }
                                Text(
                                    item.name,
                                    style = MaterialTheme.typography.body1
                                )
                            }
                        }
                    }

                }
            }

            Text(if(  !(model.locations.isNotEmpty() && model.locations.size > index)) "No data" else
                Date.from(Instant.ofEpochSecond(model.locations[index].data.current.dt)).timeAgo(),
                style =  MaterialTheme.typography.body2.copy(color = Color.Gray),modifier = Modifier.padding(start = 5.dp))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp),verticalAlignment = Alignment.CenterVertically) {
            if(model.locations.isNotEmpty() && model.locations.size > index) {
                if (model.myLocations.any {  it.latitude.round(2) == model.locations[index].data.lat.round(2) && it.longitude.round(2) == model.locations[index].data.lon.round(2) }) {
                    if (!model.locations[index].isCurrent) {
                        Box(
                            modifier = Modifier
                                .width(30.dp)
                                .height(30.dp)

                                .clickable {
                                    val oldIndex = index
                                    model.indexChange(0)
                                    scope.launch {
                                        model.remove(
                                            SavedLocation(
                                                model.locations[oldIndex].name,
                                                model.locations[oldIndex].data.lat.round(
                                                    2
                                                ),
                                                model.locations[oldIndex].data.lon.round(
                                                    2
                                                )
                                            )
                                        )
                                    }
                                }, contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Favorite, contentDescription = "",tint = Color.Red,
                                modifier = Modifier.size(30.dp))
                        }
                    }
                } else {
                    if (!model.locations[index].isCurrent) {
                        Box(
                            modifier = Modifier
                                .width(30.dp)
                                .height(30.dp)

                                .clickable {
                                    if (!model.myLocations.any {
                                            it.latitude.round(2) == model.locations[index].data.lat.round(
                                                2
                                            ) && it.longitude.round(2) == model.locations[index].data.lon.round(
                                                2
                                            )
                                        }) {
                                        scope.launch {
                                            model.saveLocation(
                                                SavedLocation(
                                                    model.locations[index].name,
                                                    model.locations[index].data.lat.round(
                                                        2
                                                    ),
                                                    model.locations[index].data.lon.round(
                                                        2
                                                    )
                                                )
                                            )
                                        }
                                    }
                                }, contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.FavoriteBorder,
                                contentDescription = "",
                                tint = Color.Gray,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }
            }

            IconButton(onClick = {
                controller.navigate("Search")
            }) {
                Box() {

                    Icon(
                        FontAwesomeIcons.Solid.Search, contentDescription = "",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            IconButton(onClick = {
                controller.navigate("Settings")
            }) {
                Box() {

                    Icon(
                        FontAwesomeIcons.Solid.EllipsisV, contentDescription = "",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
@Composable
fun MapTopBarView(model: WeatherViewModel, controller: NavController){

    var dropExtended by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    val index:Int by model.index.observeAsState(initial = 0)
    Row(modifier = Modifier
        .background(
            color = if (isSystemInDarkTheme()) Color(0xFF353535).copy(alpha = 0.9f) else Color.White.copy(
                alpha = 0.9f
            )
        )
        .statusBarsPadding()

        .fillMaxWidth()


        ,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = {
            controller.popBackStack()
        }) {
            Icon(FontAwesomeIcons.Solid.ArrowLeft,contentDescription = "",modifier = Modifier.size(20.dp))
        }
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth(0.55f)) {

            Box() {
                if (!(model.locations.isNotEmpty() && model.locations.size > index)) {
                    Text("N/A", style = MaterialTheme.typography.h4)
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable(onClick = { dropExtended = !dropExtended  })
                    ) {
                        if (model.locations[index].isCurrent) {
                            Icon(FontAwesomeIcons.Solid.LocationArrow,modifier = Modifier.size(15.dp), contentDescription = "")
                        }
                        Text(
                            model.locations[index].name,
                            style = MaterialTheme.typography.h4

                        )
                    }
                }
                DropdownMenu(expanded = dropExtended, onDismissRequest = { /*TODO*/ }) {


                    model.locations.forEachIndexed { index, item ->
                        DropdownMenuItem(onClick = {
                            model.indexChange(index)

                            dropExtended = false
                        },
                            modifier = Modifier.width(160.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (item.isCurrent) {
                                    Icon(FontAwesomeIcons.Solid.LocationArrow,modifier = Modifier.size(15.dp), contentDescription = "")
                                }
                                Text(
                                    item.name,
                                    style = MaterialTheme.typography.body2
                                )
                            }
                        }
                    }

                }
            }

           }

    }
}