package npsprojects.darkweather.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import compose.icons.FeatherIcons
import compose.icons.feathericons.Heart
import compose.icons.feathericons.Menu
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
fun TopBarView(model: WeatherViewModel, controller: NavController){

    var dropExtended by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    Row(modifier = Modifier
        .padding(top = 20.dp)
        .padding(horizontal = 20.dp)
        .fillMaxWidth()
        .height(90.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth(0.55f)) {

            Box() {
                if (model.locations.isEmpty()) {
                    Text("N/A", style = MaterialTheme.typography.h4)
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable(onClick = { dropExtended = !dropExtended  })
                    ) {
                        if (model.locations[model.index.value!!].isCurrent) {
                            Icon(Icons.Default.LocationOn, contentDescription = "")
                        }
                        Text(
                            model.locations[model.index.value!!].name,
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
                                    Icon(Icons.Default.LocationOn, contentDescription = "")
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

            Text(if(model.locations.isEmpty()) "No data" else
                Date.from(Instant.ofEpochSecond(model.locations[model.index.value!!].data.current.dt)).ago(),
                style =  MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.primary.copy(alpha = 0.7f)),modifier = Modifier.padding(start = 5.dp))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp),verticalAlignment = Alignment.CenterVertically) {
            if(!model.locations.isEmpty()) {
                if (model.myLocations.any { it.name == model.locations[model.index.value!!].name }) {
                    if (!model.locations[model.index.value!!].isCurrent) {
                        Box(
                            modifier = Modifier
                                .clickable {
                                    val oldIndex = model.index.value!!
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
                    if (!model.locations[model.index.value!!].isCurrent) {
                        Box(
                            modifier = Modifier

                                .clickable {
                                    if (!model.myLocations.any { it.name == model.locations[model.index.value!!].name }) {
                                        scope.launch {
                                            model.saveLocation(
                                                SavedLocation(
                                                    model.locations[model.index.value!!].name,
                                                    model.locations[model.index.value!!].data.lat.round(
                                                        2
                                                    ),
                                                    model.locations[model.index.value!!].data.lon.round(
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
                        Icons.Default.Search, contentDescription = "",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            IconButton(onClick = {
                controller.navigate("Settings")
            }) {
                Box() {

                    Icon(
                        FeatherIcons.Menu, contentDescription = "",
                        modifier = Modifier.size(30.dp)
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
                        if (model.locations[model.index.value!!].isCurrent) {
                            Icon(Icons.Default.LocationOn, contentDescription = "")
                        }
                        Text(
                            model.locations[model.index.value!!].name,
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
                                    Icon(Icons.Default.LocationOn, contentDescription = "")
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

            Text(if(model.locations.isEmpty()) "No data" else
                Date.from(Instant.ofEpochSecond(model.locations[model.index.value!!].data.current.dt)).timeAgo(),
                style =  MaterialTheme.typography.body2.copy(color = Color.Gray),modifier = Modifier.padding(start = 5.dp))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp),verticalAlignment = Alignment.CenterVertically) {
            if(!model.locations.isEmpty()) {
                if (model.myLocations.any { it.name == model.locations[model.index.value!!].name }) {
                    if (!model.locations[model.index.value!!].isCurrent) {
                        Box(
                            modifier = Modifier
                                .width(30.dp)
                                .height(30.dp)
                                .background(
                                    color = red_700,
                                    shape = CircleShape
                                )
                                .clickable {
                                    val oldIndex = model.index.value!!
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
                            Icon(Icons.Default.Clear, contentDescription = "",tint = Color.White)
                        }
                    }
                } else {
                    if (!model.locations[model.index.value!!].isCurrent) {
                        Box(
                            modifier = Modifier
                                .width(30.dp)
                                .height(30.dp)
                                .background(
                                    color = teal_500,
                                    shape = CircleShape
                                )
                                .clickable {
                                    if (!model.myLocations.any { it.name == model.locations[model.index.value!!].name }) {
                                        scope.launch {
                                            model.saveLocation(
                                                SavedLocation(
                                                    model.locations[model.index.value!!].name,
                                                    model.locations[model.index.value!!].data.lat.round(
                                                        2
                                                    ),
                                                    model.locations[model.index.value!!].data.lon.round(
                                                        2
                                                    )
                                                )
                                            )
                                        }
                                    }
                                }, contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PushPin, contentDescription = "",tint = Color.White)
                        }
                    }
                }
            }

            IconButton(onClick = {
              controller.navigate("Search")
            }) {
                Icon(
                    Icons.Default.Search, contentDescription = "",
                    modifier = Modifier.size(30.dp))
            }
            IconButton(onClick = {
                controller.navigate("Settings")
            }) {
                Icon(
                    Icons.Default.Menu, contentDescription = "",
                    modifier = Modifier.size(30.dp))
            }
        }
    }
}
