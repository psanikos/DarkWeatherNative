package npsprojects.darkweather.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.*
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
import npsprojects.darkweather.models.WeatherModel
import npsprojects.darkweather.models.WeatherViewModel
import npsprojects.darkweather.round
import npsprojects.darkweather.timeAgo
import npsprojects.darkweather.ui.theme.*
import java.time.Instant
import java.util.*

@Composable
fun TopBarView(model: WeatherViewModel, controller: NavController,color: Color){
    val locations by model.locations.observeAsState(initial = listOf<WeatherModel>())

    var dropExtended by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    val index:Int by model.index.observeAsState(initial = 0)
    SmallTopAppBar(
        title = {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Bottom) {

                Box() {
                    if (!(locations.isNotEmpty() && locations.size > index)) {
                        Text("N/A", style = MaterialTheme.typography.displayLarge.copy(color = Color.White))
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable(onClick = { dropExtended = !dropExtended  })
                        ) {
                            if (locations[index].isCurrent) {
                                Icon(FontAwesomeIcons.Solid.LocationArrow,modifier = Modifier.size(15.dp), contentDescription = "",
                                tint = Color.White)
                            }
                            Text(
                                locations[index].name,
                                style = MaterialTheme.typography.displayMedium.copy(color = Color.White)

                            )
                        }
                    }
                    DropdownMenu(expanded = dropExtended, onDismissRequest = { /*TODO*/ }) {


                        locations.forEachIndexed { index, item ->
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
                                        Icon(FontAwesomeIcons.Solid.LocationArrow,modifier = Modifier.size(15.dp), contentDescription = "",
                                        )
                                    }
                                    Text(
                                        item.name,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }

                    }
                }

                Text(  if (!(locations.isNotEmpty() && locations.size > index)) "No data" else
                    Date.from(Instant.ofEpochSecond(locations[index].data.current.dt)).ago(),
                    style =  MaterialTheme.typography.labelSmall.copy(color = Color.White),modifier = Modifier.padding(start = 5.dp))
            }
        },
        actions = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (locations.isNotEmpty() && locations.size > index) {

                        if (!locations[index].isCurrent) {
                            TextButton(onClick = {
                                if (model.myLocations.any { it.latitude.round(2) == locations[index].data.lat.round(2) && it.longitude.round(2) == locations[index].data.lon.round(2) }) {
                                    val oldIndex = index
                                    model.indexChange(0)
                                    scope.launch {
                                        model.remove(
                                            locations[oldIndex]
                                        )
                                    }
                                }
                                    else {
                                    scope.launch {
                                        model.saveLocation(
                                            SavedLocation(
                                                locations[index].name,
                                                locations[index].data.lat.round(
                                                    2
                                                ),
                                                locations[index].data.lon.round(
                                                    2
                                                )
                                            )
                                        )
                                    }
                                    }
                                }) {

                                ColoredIcon(  if (model.myLocations.any { it.latitude.round(2) == locations[index].data.lat.round(2) && it.longitude.round(2) == locations[index].data.lon.round(2) })
                                    Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = "",
                                    modifier = Modifier.size(25.dp), padding = 6.dp,tint = MaterialTheme.colorScheme.tertiaryContainer)
                            }

                        }

                }
               TextButton(onClick = {
                    controller.navigate("Search")
                }) {
                    Box() {

                        ColoredIcon(imageVector =  FontAwesomeIcons.Solid.Search, contentDescription = "",
                             modifier = Modifier.size(25.dp), padding = 6.dp, tint = MaterialTheme.colorScheme.tertiaryContainer)
                    }
                }
                TextButton(onClick = {
                    controller.navigate("Settings")
                }) {

                    ColoredIcon(imageVector =  FontAwesomeIcons.Solid.EllipsisV, contentDescription = "",
                         modifier = Modifier.size(25.dp), padding = 6.dp, tint = MaterialTheme.colorScheme.tertiaryContainer)


                }
            }

        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            titleContentColor = MaterialTheme.colorScheme.tertiary,
            containerColor = Color.Transparent
        ),
        modifier = Modifier.height(100.dp)

    )

}

@Composable
fun MapTopBarView(model: WeatherViewModel, controller: NavController){
    val locations by model.locations.observeAsState(initial = listOf<WeatherModel>())

    var dropExtended by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    val index:Int by model.index.observeAsState(initial = 0)

    Row(modifier = Modifier
        .background(
            color = if (isSystemInDarkTheme()) iceBlack else frosted
        )

        .height(150.dp)
        .fillMaxWidth()


        ,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = {
            controller.popBackStack()
        }) {
            Icon(FontAwesomeIcons.Solid.ArrowLeft,contentDescription = "",modifier = Modifier.size(25.dp))
        }
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth(0.8f)) {

            Box() {
                if (!(locations.isNotEmpty() && locations.size > index)) {
                    Text("N/A", style = androidx.compose.material3.MaterialTheme.typography.headlineLarge)
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable(onClick = { dropExtended = !dropExtended  })
                    ) {
                        if (locations[index].isCurrent) {
                            Icon(FontAwesomeIcons.Solid.LocationArrow,modifier = Modifier.size(25.dp), contentDescription = "")
                        }
                        Text(
                            locations[index].name,
                            style = androidx.compose.material3.MaterialTheme.typography.headlineLarge

                        )
                    }
                }
                DropdownMenu(expanded = dropExtended, onDismissRequest = { /*TODO*/ }) {


                    locations.forEachIndexed { index, item ->
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
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                }
            }

           }

    }
}