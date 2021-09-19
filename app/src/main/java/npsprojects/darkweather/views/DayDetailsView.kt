package npsprojects.darkweather.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import npsprojects.darkweather.R
import npsprojects.darkweather.models.Daily
import npsprojects.darkweather.models.WeatherViewModel
import npsprojects.darkweather.ui.theme.DarkWeatherTheme
import npsprojects.darkweather.ui.theme.blue_grey_500
import java.text.SimpleDateFormat
import java.util.*

//@Preview
//@Composable
//fun DayDetailsPreview(){
//    DarkWeatherTheme {
//        DayDetailsView()
//    }
//}

fun moonIcon(input:Double):Int{

return when(input){
    0.0 -> R.drawable.moon0
    in 0.01 .. 0.17 -> R.drawable.moon1
    in 0.18 .. 0.34 -> R.drawable.moon2
    in 0.35 .. 0.49 -> R.drawable.moon3
    0.5 -> R.drawable.moon4
    in 0.51 .. 0.67 -> R.drawable.moon5
    in 0.68 .. 0.84 -> R.drawable.moon6
    in 0.85 .. 0.99 -> R.drawable.moon7
    1.0 -> R.drawable.moon0
    else -> R.drawable.moon0
}

}
fun moonDecription(input:Double):String{

    return when(input){
        0.0 -> "New moon"
        in 0.01 .. 0.49 -> "Filling"
        0.5 -> "Full moon"
        in 0.51 .. 0.99 -> "Reducing"
        1.0 -> "New moon"
        else -> "Moon"
    }

}

@Composable
fun DayDetailsView(model: WeatherViewModel){
    val index:Int by  model.index.observeAsState(initial = 0)
    var day:Daily? by remember {
        mutableStateOf(null)
    }
    LaunchedEffect(key1 = index + model.locations.size, block ={
        if(!model.locations.isEmpty()) {
            day = model.locations[model.index.value!!].data.daily[0]
        }
    })
   if (day != null) {





        //Text("Today", style= MaterialTheme.typography.h2.copy(fontSize = 12.sp,color = Color.DarkGray))
        Row(
            modifier = Modifier
                .height(20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(

                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.sunrise),
                    contentDescription = "Sun rise",
                    modifier = Modifier.size(20.dp)
                )
                Text( SimpleDateFormat("HH:mm", Locale.getDefault()).format(
                    Date(1000 * day!!.sunrise)
                ), style = MaterialTheme.typography.body2)
            }
            Row(

                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.sunset),
                    contentDescription = "Sun set",
                    modifier = Modifier.size(20.dp)
                )
                Text( SimpleDateFormat("HH:mm", Locale.getDefault()).format(
                    Date(1000 * day!!.sunset)
                ), style = MaterialTheme.typography.body2)
            }
            Row(

                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = moonIcon(day?.moon_phase ?: 0.0)), contentDescription = "moon",
                    modifier = Modifier.size(20.dp)
                )
                Text(moonDecription(day?.moon_phase ?: 0.0), style = MaterialTheme.typography.body2)
            }
        }

    }
}