package npsprojects.darkweather.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import npsprojects.darkweather.models.WeatherViewModel

@ExperimentalCoilApi
@Composable
fun TopWeatherCard(model: WeatherViewModel){


    Row(modifier = Modifier
        .padding(horizontal = 20.dp)
        .height(120.dp)
        .fillMaxWidth()
        ,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        if(!model.locations.isEmpty()) {
            Image(
                painter = rememberImagePainter(data = "https://openweathermap.org/img/wn/${model.locations[model.index.value!!].data.current.weather[0].icon}@4x.png"),
                contentDescription = "weather image",
                modifier = Modifier.size(120.dp),
                contentScale = ContentScale.Fit
            )
        }
        Column(modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceEvenly) {

            Text(if(model.locations.isEmpty()) "__°" else (model.locations[model.index.value!!].data.current.temp.toUInt().toString() + "°"),style = MaterialTheme.typography.h1.copy(fontSize = 40.sp))
            Text(if(model.locations.isEmpty()) "N/A" else model.locations[model.index.value!!].data.current.weather[0].description,style = MaterialTheme.typography.body2)
        }

    }
}