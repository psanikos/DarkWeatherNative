package npsprojects.darkweather

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.twotone.CheckBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.common.util.CollectionUtils.listOf
import npsprojects.darkweather.ui.theme.*

@ExperimentalFoundationApi
@Composable
fun AddPlaceView(model: WeatherViewModel,controller: NavController){
    var searchTerm:String by remember { mutableStateOf("") }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.DarkGray,
                    Color(0xFF4A526D)
                )
            )
        )) {
        Column(modifier = Modifier
            .padding(top = 50.dp, start = 16.dp, end = 16.dp)
            .fillMaxSize(),verticalArrangement = Arrangement.Top,horizontalAlignment = Alignment.CenterHorizontally) {
         Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Start) {
             IconButton(onClick = {
                 controller.popBackStack()
             }) {
                 Icon(Icons.Rounded.ArrowBackIosNew,contentDescription = "",tint = Color.White)
             }
         }

            Spacer(modifier = Modifier.height(30.dp))
            Box(modifier = Modifier

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
                if (it.subLocality != null || it.locality != null || it.featureName != null) {

                    Row(
                        modifier = Modifier
                            .padding(vertical = 5.dp)
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12)
                            )
                            .clickable {
                                model.saveLocation(address = it)
                                searchTerm = ""
                                model.searchedAdresses.clear()
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            if (it.featureName != null) it.featureName else if (it.subLocality != null) it.subLocality else it.locality,
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        Icon(
                            Icons.TwoTone.CheckBox,
                            contentDescription = "",
                            tint = green_400,
                            modifier = Modifier
                                .size(25.dp)
                                .padding(end = 10.dp)
                        )
                    }
                }
            }
            LazyColumn(modifier = Modifier
                .padding(vertical = 6.dp)
                .fillMaxWidth()) {
                items(model.myLocations){




                        Box(
                            modifier = Modifier
                                .padding(vertical = 5.dp)
                                .height(50.dp)

                                .fillMaxWidth()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.White.copy(
                                                alpha = 0.4f
                                            ), Color.White.copy(alpha = 0.2f)
                                        )
                                    ), shape = RoundedCornerShape(12)
                                ),
                            contentAlignment = Alignment.Center){
                          Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween,
                              verticalAlignment = Alignment.CenterVertically) {

                              Text(it.name, style = MaterialTheme.typography.body2,modifier = Modifier.padding(horizontal = 10.dp))
                              Button(onClick = {

                                  model.remove(it)
                              },modifier = Modifier
                                  .width(100.dp)
                                  .height(50.dp),colors = ButtonDefaults.buttonColors(
                                  contentColor = red_800,backgroundColor = red_400.copy(alpha = 0.85f)),shape = RoundedCornerShape( bottomEndPercent = 12,topEndPercent = 12)) {
                                  Text("Remove",style = MaterialTheme.typography.body2 )
                              }
                          }
                        }


                }

            }
        }
    }
}
