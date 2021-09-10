package npsprojects.darkweather.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import npsprojects.darkweather.models.WeatherViewModel
import npsprojects.darkweather.timeAgo
import java.time.Instant
import java.util.*

@Composable
fun TopBarView(model: WeatherViewModel, controller: NavController){

    var dropExtended by remember {
        mutableStateOf(false)
    }
    Row(modifier = Modifier
        .padding(top = 30.dp)
        .padding(horizontal = 20.dp)
        .fillMaxWidth()
        .height(80.dp),
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
            IconButton(onClick = {
                controller.navigate("AddPage")
            }) {
                Icon(
                    Icons.Default.Add, contentDescription = "",
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
