package npsprojects.darkweather.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import npsprojects.darkweather.models.WeatherModel
import npsprojects.darkweather.round
import npsprojects.darkweather.timeAgo
import npsprojects.darkweather.ui.theme.DarkWeatherTheme
import java.time.Instant
import java.util.*


@ExperimentalCoilApi
@Composable
fun MainCard(weather:WeatherModel) {

        Column(
            modifier = Modifier.cardModifier(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
            Box(modifier = Modifier
                .background(
                    color = Color.Gray.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(50)
                )
                .padding(4.dp)){
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if(weather.isCurrent) {
                        Icon(
                            Icons.Filled.LocationOn,
                            contentDescription = "",
                            modifier = Modifier.size(12.dp),
                            tint = if(isSystemInDarkTheme()) Color.White else Color.Black
                        )
                    }
                    Text(weather.name, style = MaterialTheme.typography.body1)
                }
            }
                //yyyy-MM-dd hh:mm:ss
                Text(Date.from(Instant.ofEpochSecond(weather.data.current.dt)).timeAgo(), style = MaterialTheme.typography.caption.copy(color = Color.Gray))
            }
            Column(

                verticalArrangement = Arrangement.spacedBy(0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = rememberImagePainter(data = "https://openweathermap.org/img/wn/${weather.data.current.weather[0].icon}@4x.png"),
                    contentDescription = "weather image",
                    modifier = Modifier.size(120.dp),
                    contentScale = ContentScale.Fit
                )
                Text(text = "${weather.data.current.temp.toUInt()}°", style = MaterialTheme.typography.h1.copy(fontSize = 36.sp))
            }
            Text(weather.data.current.weather[0].description,style = MaterialTheme.typography.body2)
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(25.dp)
                            .background(
                                color = Color(0xFF303030).copy(alpha = 0.2f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            Icons.Filled.Navigation,
                            contentDescription = "",
                            modifier = Modifier
                                .size(12.dp)
                                .rotate(weather.data.current.wind_deg.toFloat()),
                            tint = if(isSystemInDarkTheme()) Color.White else Color.Black,

                        )
                    }
                    Text(text = "${weather.data.current.wind_speed.round(1)} mph", style = MaterialTheme.typography.body1)
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(25.dp)
                            .background(
                                color = Color.Red.copy(alpha = 0.2f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.ArrowDropUp,
                            contentDescription = "",
                            modifier = Modifier.size(30.dp),
                            tint = Color.Red
                        )
                    }
                    Text(text = "${weather.data.daily[0].temp.max.toUInt()}°", style = MaterialTheme.typography.body1)
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(25.dp)
                            .background(
                                color = Color.Blue.copy(alpha = 0.2f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.ArrowDropDown,
                            contentDescription = "",
                            modifier = Modifier.size(30.dp),
                            tint = Color.Blue
                        )
                    }
                    Text(text = "${weather.data.daily[0].temp.min.toUInt()}°", style = MaterialTheme.typography.body1)
                }
            }
        }
    }



//@Preview
//@Composable
//fun MainPreview(){
//    MaterialTheme {
//      MainCard()
//    }
//}
