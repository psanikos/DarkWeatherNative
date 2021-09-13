package npsprojects.darkweather.views

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import npsprojects.darkweather.DeviceType
import npsprojects.darkweather.MyApp
import npsprojects.darkweather.models.WeatherViewModel
import npsprojects.darkweather.ui.theme.*
import npsprojects.darkweather.R


@ExperimentalAnimationApi
@Preview
@Composable
fun FirstPreview(){

    DarkWeatherTheme() {
        FirstScreen(model = WeatherViewModel())
    }
}


@ExperimentalAnimationApi
@Composable
fun FirstScreen(model: WeatherViewModel) {

    val permissionGranted = model.permissionGranted.observeAsState()
    val configuration = LocalConfiguration.current
    val deviceType = when(configuration.smallestScreenWidthDp > 480){
        true -> DeviceType.BIGSCREEN
            false -> DeviceType.PHONE
    }
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier

                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        Text("")
            Text(
                "DarkWeather",
                style = MaterialTheme.typography.h2.copy(color = Color.White, fontSize = 50.sp,fontFamily = FontFamily(
                    Font(resId = R.font.pacificoregular,
                    weight = FontWeight.Normal)
                ))
            )
            Box(
                modifier = Modifier


                    .fillMaxWidth()
                    .fillMaxHeight(0.6f)
                    .background(
                        color = blue_grey_500.copy(alpha = 0.1f),

                        )

            ) {
                Column(modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize(),verticalArrangement = Arrangement.SpaceEvenly) {


                    Text(
                          stringResource(id = R.string.WelcomeText)  ,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(12.dp),
                        lineHeight = 26.sp,
                        textAlign = TextAlign.Start,

                    )
                    if (permissionGranted.value!!) {
                        Button(
                            onClick = {
                                model.hasRun()

                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(45.dp),
                            colors = ButtonDefaults.buttonColors(
                                contentColor = green_800, backgroundColor = green_400.copy(alpha = 0.85f)
                            ),
                            elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
                            shape = RoundedCornerShape(30)
                        ) {
                            Text(stringResource(id = R.string.Continue), style = MaterialTheme.typography.button)
                        }

                    } else {
                      if(deviceType == DeviceType.PHONE)
                          Column()      {

                              Button(
                                  onClick = {
                                      model.askPermission(){

                                      }

                                  },
                                  modifier = Modifier
                                      .fillMaxWidth()
                                      .height(45.dp),
                                  colors = ButtonDefaults.buttonColors(
                                      contentColor = green_800,
                                      backgroundColor = green_400.copy(alpha = 0.85f)
                                  ),
                                  elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
                                  shape = RoundedCornerShape(50)
                              ) {
                                  Text(stringResource(id = R.string.AllowAccess), style = MaterialTheme.typography.button)
                              }


                              Button(
                                  onClick = {
                                      model.askContinueWithout()
                                      GlobalScope.launch {
                                          model.hasRun()
                                      }
                                  },
                                  modifier = Modifier
                                      .fillMaxWidth()
                                      .height(45.dp),
                                  colors = ButtonDefaults.buttonColors(
                                      contentColor = blue_600,
                                      backgroundColor = Color.Transparent
                                  ),
                                  elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
                                  shape = RoundedCornerShape(50)
                              ) {
                                  Text(stringResource(id = R.string.Continue), style = MaterialTheme.typography.button)

                              }
                          }
                      else Row(horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically,
                      modifier = Modifier.fillMaxWidth())
                        {

                            Button(
                                onClick = {
                                    model.askPermission(){}

                                },
                                modifier = Modifier
                                    .fillMaxWidth(0.45f)
                                    .height(45.dp),
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = green_800,
                                    backgroundColor = green_400.copy(alpha = 0.85f)
                                ),
                                elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(stringResource(id = R.string.AllowAccess), style = MaterialTheme.typography.button)
                            }


                            Button(
                                onClick = {
                                    model.askContinueWithout()
                                    GlobalScope.launch {
                                        model.hasRun()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .height(45.dp),
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = blue_800,
                                    backgroundColor = light_blue_500.copy(alpha = 0.85f)
                                ),
                                elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(stringResource(id = R.string.Continue), style = MaterialTheme.typography.button)

                            }
                        }
                    }

                }

            }
        }
    }


}

