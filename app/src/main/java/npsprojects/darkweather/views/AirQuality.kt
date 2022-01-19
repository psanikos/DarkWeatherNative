package npsprojects.darkweather.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import npsprojects.darkweather.R
import npsprojects.darkweather.ui.theme.*

@Composable
fun AirQuality(aqi:Int){
    val color = when (aqi) {
        1 -> teal_500
        2 -> yellow_500
        3 -> orange_500
        4 -> pink_500
        else -> red_500
    }
    val name = when (aqi) {
        1 -> stringResource(id = R.string.Good)
        2 -> stringResource(id = R.string.Fair)
        3 -> stringResource(id = R.string.Moderate)
        4 -> stringResource(id = R.string.Poor)
        else -> stringResource(id = R.string.VeryPoor)
    }
    val summary = when (aqi) {
        1 -> stringResource(id = R.string.GoodText)
        2 -> stringResource(id = R.string.FairText)
        3 -> stringResource(id = R.string.ModerateText)
        4 -> stringResource(id = R.string.PoorText)
        else -> stringResource(id = R.string.VeryPoorText)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .background(color = if (isSystemInDarkTheme()) iceBlack else frosted, shape = RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ){
            Column(horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Icon(
                        Icons.Default.Air, contentDescription = "",
                        modifier = Modifier.size(15.dp), tint = Color.Gray)
                    Text("Air Quality", style = MaterialTheme.typography.caption.copy(color = Color.Gray))

                }
                Text("$aqi", style = MaterialTheme.typography.h3.copy(color = if(isSystemInDarkTheme()) Color.White else Color.Black))
                Text(name, style = MaterialTheme.typography.body1.copy(color = if(isSystemInDarkTheme()) Color.White else Color.Black))
            }
            Box(
                modifier = Modifier
                    .height(100.dp)
                    .width(30.dp)
                    .border(width = 2.dp, color = Color.DarkGray,
                        shape = RoundedCornerShape(20.dp)), contentAlignment = Alignment.BottomCenter
            ){
                Box(modifier = Modifier
                    .offset(y = (10-16*aqi).dp)
                    .size(20.dp).background(color = color, shape = CircleShape))
            }
        }
        Divider(modifier = Modifier.fillMaxWidth(0.7f).padding(5.dp))
        Text(summary, style = MaterialTheme.typography.caption.copy(color = Color.Gray))

    }

}

@Preview
@Composable
fun AirPreview(){
    DarkWeatherTheme {
        AirQuality(aqi = 1)
    }
}