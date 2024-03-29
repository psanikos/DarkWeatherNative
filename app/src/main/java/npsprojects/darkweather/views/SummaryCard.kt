package npsprojects.darkweather.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Child
import compose.icons.fontawesomeicons.solid.Tint
import compose.icons.fontawesomeicons.solid.Umbrella
import npsprojects.darkweather.getWeatherImage
import npsprojects.darkweather.models.Current
import npsprojects.darkweather.ui.theme.DarkWeatherTheme
import npsprojects.darkweather.ui.theme.frosted
import npsprojects.darkweather.ui.theme.iceBlack
import kotlin.math.roundToInt
import npsprojects.darkweather.R

//current: Current,daySummary:String

@Composable
fun SummaryCard(current: Current,dayDetails:String){
    BoxWithConstraints(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()) {
        val isSmall = this.maxWidth < 360.dp
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .background(
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp)

        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Top
            ) {
                Image(
                    painter = painterResource(id = getWeatherImage(current.weather.first().icon!!)),
                    contentDescription = "",
                    modifier = Modifier.size(if(isSmall) 70.dp else 100.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Column(
                    modifier = Modifier.width(100.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        current.weather.first().description!!,
                        style = if(isSmall) MaterialTheme.typography.labelSmall.copy(if (isSystemInDarkTheme()) Color.White else Color.Black) else  MaterialTheme.typography.labelMedium.copy(if (isSystemInDarkTheme()) Color.White else Color.Black)
                    )
                    Text(
                        current.temp!!.roundToInt().toString() + "°",
                        style = androidx.compose.material3.MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                            fontSize = if(isSmall) 34.sp else 40.sp
                        )
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Icon(
                            FontAwesomeIcons.Solid.Umbrella, contentDescription = "",
                            modifier = Modifier.size(15.dp), tint = Color.Gray
                        )
                        Text(
                            ((current.pop?.times(100)) ?: 0.0).roundToInt().toString() + "%",
                            style = MaterialTheme.typography.labelMedium.copy(color = if (isSystemInDarkTheme()) Color.White else Color.Black)
                        )
                    }

                }
                Divider(
                    modifier = Modifier
                        .width(1.dp)
                        .height(if (isSmall) 20.dp else 100.dp)
                        .padding(2.dp)
                )
                Column(
                    modifier = Modifier.width(100.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Icon(
                            FontAwesomeIcons.Solid.Tint, contentDescription = "",
                            modifier = Modifier.size(15.dp), tint = Color.Gray
                        )
                        Text(
                            stringResource(id = R.string.humidity),
                            style = if(isSmall) MaterialTheme.typography.labelSmall else  MaterialTheme.typography.labelMedium,
                            color = Color.Gray
                        )
                    }
                    Text(
                        current.humidity.toString() + "%",
                        style = MaterialTheme.typography.displayLarge.copy(color = if (isSystemInDarkTheme()) Color.White else Color.Black)
                    )
                    Divider()
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Icon(
                            FontAwesomeIcons.Solid.Child, contentDescription = "",
                            modifier = Modifier.size(15.dp), tint = Color.Gray
                        )
                        Text(
                            stringResource(id = R.string.Feels),
                            style = if(isSmall) MaterialTheme.typography.labelSmall else  MaterialTheme.typography.labelMedium,
                            color = Color.Gray
                        )
                    }
                    Text(
                        current.feels_like!!.roundToInt().toString() + "°",
                        style = MaterialTheme.typography.displayLarge.copy(color = if (isSystemInDarkTheme()) Color.White else Color.Black)
                    )
                }

            }
            Text(
                dayDetails,
                style = MaterialTheme.typography.labelMedium.copy(color = if (isSystemInDarkTheme()) Color.White else Color.Black),
                modifier = Modifier.padding(10.dp)
            )

        }
    }

}


//@Preview
//@Composable
//fun SummaryPreview(){
//    DarkWeatherTheme {
//        SummaryCard()
//    }
//}