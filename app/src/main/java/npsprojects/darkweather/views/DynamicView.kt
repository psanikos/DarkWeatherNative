package npsprojects.darkweather.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import npsprojects.darkweather.WeatherUnits
import npsprojects.darkweather.getWeatherBackground
import npsprojects.darkweather.getWeatherColor
import npsprojects.darkweather.getWeatherIcon
import npsprojects.darkweather.ui.theme.DarkWeatherTheme
import npsprojects.darkweather.ui.theme.blue_500

@Composable
fun DynamicView(){

    Scaffold(
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            getWeatherColor("01d"),
                            MaterialTheme.colors.background,
                            MaterialTheme.colors.background,
                            MaterialTheme.colors.background
                        )
                    )
                )
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(40.dp)
            ) {
                item {
                    Image(
                        painter = painterResource(id = getWeatherIcon("01d")),
                        contentDescription = "weather",
                        modifier = Modifier
                            .offset(y = 40.dp)
                            .size(160.dp)
                    )
                }
                item {
                    Text("22", style = MaterialTheme.typography.h4.copy(fontSize = 70.sp))
                }
                item {
                    Text("Clear", style = MaterialTheme.typography.body1.copy(fontSize = 30.sp))
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {


                            ColoredIcon(
                                Icons.Filled.Navigation,
                                contentDescription = "",
                                modifier = Modifier
                                    .size(30.dp)
                                    .rotate(11.2F),
                                tint = if (isSystemInDarkTheme()) Color.White else Color.Black,

                                )

                            Text(text = "3 mph", style = MaterialTheme.typography.body1)
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            ColoredIcon(
                                Icons.Filled.ArrowUpward,
                                contentDescription = "",
                                modifier = Modifier.size(30.dp),
                                tint = if (isSystemInDarkTheme()) Color.White else Color.Black
                            )

                            Text(text = "23", style = MaterialTheme.typography.body1)
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            ColoredIcon(
                                Icons.Filled.ArrowDownward,
                                contentDescription = "",
                                modifier = Modifier.size(30.dp),
                                tint = if (isSystemInDarkTheme()) Color.White else Color.Black
                            )

                            Text(text = "17", style = MaterialTheme.typography.body1)
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ColoredIcon(
                                Icons.Filled.Umbrella, contentDescription = "",
                                modifier = Modifier.size(30.dp),
                                tint = if (isSystemInDarkTheme()) Color.White else Color.Black
                            )

                            Text(
                                text = "20%",
                                style = MaterialTheme.typography.body1
                            )
                        }
                    }
                }
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Today", style = MaterialTheme.typography.h4)
                        }
                        Divider(modifier = Modifier.fillMaxWidth())
                        LazyRow(modifier = Modifier.fillMaxWidth()) {
                            (0..8).forEach {
                                item {
                                    Column(
                                        modifier = Modifier
                                            .width(80.dp)
                                            .height(120.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text("21:00", style = MaterialTheme.typography.body2)
                                        Image(
                                            painter = painterResource(id = getWeatherIcon("01d")),
                                            contentDescription = "weather",
                                            modifier = Modifier
                                                .size(40.dp)
                                        )
                                        Text("21", style = MaterialTheme.typography.body1)
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Filled.Umbrella, contentDescription = "",
                                                tint = Color.Gray
                                            )
                                            Text(
                                                "17%",
                                                style = MaterialTheme.typography.body2.copy(color = Color.Gray)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("This Week", style = MaterialTheme.typography.h4)
                        }
                        Divider(modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }


}


@Preview
@Composable
fun DynamicPreview(){
    DarkWeatherTheme {
        DynamicView()
    }
}