package npsprojects.darkweather.views

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import npsprojects.darkweather.R
import npsprojects.darkweather.ui.theme.*

@Composable
fun AirQualityView(index:Int){
    val color = when(index){
        1-> teal_500
        2-> yellow_500
        3-> orange_500
        4-> pink_500
        else -> red_500
    }
    val name = when(index){
        1-> stringResource(id = R.string.Good)
        2-> stringResource(id = R.string.Fair)
        3-> stringResource(id = R.string.Moderate)
        4-> stringResource(id = R.string.Poor)
        else -> stringResource(id = R.string.VeryPoor)
    }
    val summary = when(index){
        1-> stringResource(id = R.string.GoodText)
        2-> stringResource(id = R.string.FairText)
        3-> stringResource(id = R.string.ModerateText)
        4-> stringResource(id = R.string.PoorText)
        else -> stringResource(id = R.string.VeryPoorText)
    }
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(160.dp)
        .background(
            color = if (isSystemInDarkTheme()) Color.Black else Color.White,
            shape = RoundedCornerShape(20.dp)
        )){
        Column(modifier = Modifier.fillMaxSize(),horizontalAlignment = Alignment.Start,verticalArrangement = Arrangement.SpaceBetween) {
            Row(modifier = Modifier
                .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                .fillMaxWidth(),verticalAlignment = Alignment.CenterVertically,horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Filled.Air,contentDescription = "")
                Text(stringResource(R.string.AirQ),style = MaterialTheme.typography.caption)
            }
            Row(modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth(),verticalAlignment = Alignment.CenterVertically,horizontalArrangement = Arrangement.Start) {

                Text("$index",style = MaterialTheme.typography.h1.copy(fontWeight = FontWeight.Black,color = color),
                    modifier = Modifier.padding(end = 10.dp))

                Column(horizontalAlignment = Alignment.Start,verticalArrangement = Arrangement.Top) {

                    Text(name, style = MaterialTheme.typography.body1.copy(color = Color.Gray,fontWeight = FontWeight.Bold),modifier = Modifier.padding(bottom = 4.dp))

                    Text(
                        summary,
                        style = MaterialTheme.typography.caption,
                        letterSpacing = 1.1.sp,lineHeight = 14.sp

                    )

                } }
            Text("")

        }
    }
}