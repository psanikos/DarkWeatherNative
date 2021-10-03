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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import npsprojects.darkweather.R
import npsprojects.darkweather.models.Daily
import npsprojects.darkweather.models.WeatherViewModel
import npsprojects.darkweather.moonDecription
import npsprojects.darkweather.moonIcon
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



@Composable
fun DayDetailsView(model: WeatherViewModel){
    val index:Int by  model.index.observeAsState(initial = 0)
    var day:Daily? by remember {
        mutableStateOf(null)
    }
    LaunchedEffect(key1 = index + model.locations.size, block ={
        if (model.locations.isNotEmpty() && model.locations.size > index){
            day = model.locations[index].data.daily[0]
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
                Text(stringResource(id = moonDecription(day?.moon_phase ?: 0.0)), style = MaterialTheme.typography.body2)
            }
        }

    }
}