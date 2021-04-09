package npsprojects.darkweather

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import npsprojects.darkweather.ui.theme.*
import java.lang.reflect.Array.get
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class ChartData constructor(
    val index:Int,
    val offsets:Offset,
    val time:Long,
    val displayedValue:String
)


@Composable
fun MyChartView(rainProbability:List<DataX>) {
    var category: RainTimeCategory by remember { mutableStateOf(RainTimeCategory.HOURLY) }
    var timeUntilRain:Long? = null
    var timeUntilEnd:Long? = null
    var firstRainTimeIndex:Int? = rainProbability.indexOfFirst { it.precipProbability!! >= 0.5 }

    if (firstRainTimeIndex != null && firstRainTimeIndex >= 0 ) {
        timeUntilRain =
            (1000 * rainProbability[firstRainTimeIndex].time!!.toLong() - Calendar.getInstance().timeInMillis)
    }
    if (firstRainTimeIndex != null && firstRainTimeIndex >= 0 ) {
        rainProbability.forEachIndexed { index, item ->
            if (index > firstRainTimeIndex) {
                if (timeUntilEnd == null) {
                    if (item.precipProbability!! <= 0.4) {
                        timeUntilEnd = 1000*item.time!!.toLong() - Calendar.getInstance().timeInMillis
                    }
                }
            }
        }
    }
Box(modifier = Modifier
    .height(320.dp)
    .fillMaxWidth().background(brush = Brush.verticalGradient(colors = listOf(Color.Black.copy(alpha = 0.25f),Color.Black.copy(alpha = 0.5f))),
        shape = RoundedCornerShape(12.dp))){
    Column(
        modifier = Modifier.padding(10.dp)
        .fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {

        Text(
            "Rain probability"
            , style = MaterialTheme.typography.h4.copy(color = Color.White,fontSize = 16.sp),modifier = Modifier.padding(start = 10.dp,top = 5.dp,bottom = 5.dp)
        )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                   ,contentAlignment = Alignment.CenterStart
            ) {
                if(timeUntilRain != null) {
                if (timeUntilRain > 0) {
            Text(
                "Rain starts in " +
                        String.format(
                            "%d hours and %d min",
                            TimeUnit.MILLISECONDS.toHours(timeUntilRain),
                            TimeUnit.MILLISECONDS.toMinutes(timeUntilRain) -
                                    TimeUnit.HOURS.toMinutes(
                                        TimeUnit.MILLISECONDS.toHours(
                                            timeUntilRain
                                        )
                                    )
                        ), style = MaterialTheme.typography.body2.copy(color = Color.White),modifier = Modifier.padding(start = 10.dp)
            )
        }
                else {
                    if (timeUntilEnd != null){
                    Text(
                        "Rain ends in " +
                                String.format(
                                    "%d hours and %d min",
                                    TimeUnit.MILLISECONDS.toHours(timeUntilEnd!!),
                                    TimeUnit.MILLISECONDS.toMinutes(timeUntilEnd!!) -
                                            TimeUnit.HOURS.toMinutes(
                                                TimeUnit.MILLISECONDS.toHours(
                                                    timeUntilEnd!!
                                                )
                                            )
                                ), style = MaterialTheme.typography.body2.copy(color = Color.White),modifier = Modifier.padding(start = 10.dp)
                    )
                }
                    else {
                        Text(
                            "The Rain will continue ", style = MaterialTheme.typography.body2,modifier = Modifier.padding(start = 10.dp)  )
                    }
            }
            }

        }
        Box(
             modifier = Modifier
                .height(260.dp)
                .fillMaxWidth()
        ) {


            MyChart(rainProbability = rainProbability)



        }
    }
}
}

@Composable
fun MyChart(rainProbability:List<DataX>) {
    val freeSpace = 50f
    val dataSpace = 140f
    var dataPoints = mutableListOf<ChartData>()
    var dataOffsets = mutableListOf<Offset>()
    val state = rememberScrollState()
    val lineColor = if(isSystemInDarkTheme()) red_700 else blue_700



    BoxWithConstraints(contentAlignment = Alignment.TopStart, modifier = Modifier.fillMaxSize() ) {
    val heightOfBox = this.maxHeight.value - freeSpace*2

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomStart) {
            LazyRow(modifier = Modifier.fillMaxSize()) {

                item {
                    BoxWithConstraints(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomStart
                    ) {

                        val maxHeight = this.maxHeight
                        val perCentHeight = maxHeight.value / 100

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val canvasWidth = size.width
                            val canvasHeight = size.height - freeSpace

                                    rainProbability.forEachIndexed { index, item ->
                                        val offset = Offset(
                                            x = index * dataSpace,
                                            y = (canvasHeight - item.precipProbability!! * canvasHeight).toFloat()
                                        )
                                        val input = ChartData(
                                            index = index, offsets = offset, time = item.time!!.toLong(),
                                            displayedValue = "${(100 * item.precipProbability!!).roundToInt()}%"
                                        )
                                        dataPoints.add(input)
                                    }





                            //Create Chart------------

                            drawPath(
                                path = Path().apply {

                                    dataPoints.forEachIndexed { i, value ->

                                        when (i) {
                                            0 -> {
                                                moveTo(x = freeSpace, y = canvasHeight)
                                                relativeLineTo(
                                                    dx = 0f,
                                                    dy = value.offsets.y - (canvasHeight)
                                                )
                                            }
                                            dataPoints.size - 1 -> {
                                                val preValue = dataPoints[i - 1].offsets
//                                                relativeCubicTo(
//                                                    dx1 = dataSpace/4,
//                                                    dy1 =  (value.offsets.y - preValue.y)/4,
//                                                    dy2 =  3*(value.offsets.y - preValue.y)/4,
//                                                    dx2 = 3*dataSpace/4,
//                                                    dx3 = dataSpace,
//                                                    dy3 = value.offsets.y - preValue.y
//                                                )
                                                relativeQuadraticBezierTo(
                                                    dx1 = dataSpace/2,dy1 = (value.offsets.y - preValue.y)/2,dx2 =  dataSpace,dy2 = value.offsets.y - preValue.y
                                                )
//                                                relativeLineTo(
//                                                    dx = dataSpace,
//                                                    dy = value.offsets.y - preValue.y
//                                                )
                                                relativeLineTo(
                                                    dx = 0f,
                                                    dy = canvasHeight - (value.offsets.y)
                                                )
                                                close()
                                            }
                                            else -> {
                                                val preValue = dataPoints[i - 1].offsets
//                                                val curveRadius = 20
//
//                                                val x1 = 0f
//                                                val x2 = dataSpace
//                                                val y1 = 0f
//                                                val y2 = value.offsets.y - preValue.y
//                                                val midX: Float = x1 + (x2 - x1) / 2
//                                                val midY: Float = y1 + (y2 - y1) / 2
//                                                val xDiff: Float = midX - x1
//                                                val yDiff: Float = midY - y1
//                                                val angle = Math.atan2(
//                                                    yDiff.toDouble(),
//                                                    xDiff.toDouble()
//                                                ) * (180 / Math.PI) - 90
//                                                val angleRadians = Math.toRadians(angle)
//                                                val multiplier = if(value.offsets.y - preValue.y > 0 ) -1 else 1
//                                                val pointX =
//                                                    (midX + curveRadius * Math.cos(angleRadians)).toFloat()
//                                                val pointY =
//                                                    (midY + curveRadius * Math.sin(angleRadians)).toFloat()
//                                                relativeCubicTo(x1,y1,pointX, pointY, x2, y2)
//                                                relativeCubicTo(
//                                                    dx1 = dataSpace/4,
//                                                    dy1 =  (value.offsets.y - preValue.y)/4,
//                                                    dy2 =  3*(value.offsets.y - preValue.y)/4,
//                                                    dx2 = 3*dataSpace/4,
//                                                    dx3 = dataSpace,
//                                                    dy3 = value.offsets.y - preValue.y
//                                                )
                                                relativeQuadraticBezierTo(
                                                    dx1 = dataSpace/2,dy1 = (value.offsets.y - preValue.y)/2,dx2 =  dataSpace,dy2 = value.offsets.y - preValue.y
                                                )
//                                                relativeLineTo(
//                                                    dx = dataSpace,
//                                                    dy = value.offsets.y - preValue.y
//                                                )
                                         }
                                       }


                                    }
                                    val lastValue = dataPoints.last().offsets

                                }, brush = Brush.verticalGradient(
                                    colors = listOf(
                                        lineColor, lineColor.copy(alpha = 0.7f),
                                        lineColor.copy(alpha = 0.3f), lineColor.copy(alpha = 0.1f)
                                    )
                                )
                            )

                            //---------------------
                        }

                        // Times-------------
                        Row(
                            modifier = Modifier
                                .height(20.dp)
                                .padding(start = 12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy((dataSpace / 2 - 49).dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            rainProbability.forEach {
                                Box(
                                    modifier = Modifier.width(30.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        SimpleDateFormat("HH").format(1000*it.time!!.toLong())
                                        ,
                                        style = MaterialTheme.typography.caption.copy(fontSize = 10.sp,color = Color.White)
                                    )
                                }
                                // Text(it.displayedValue, style = MaterialTheme.typography.caption.copy(fontSize = 10.sp))

                            }
                        }
                        //--------------------
                    }
                }
                item {
                    Spacer(modifier = Modifier.width(20.dp))
                }
            }


            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height - freeSpace
                // Axis****************
                // Horizontal--------
                drawLine(
                    color = Color.White,
                    start = Offset(x = freeSpace, y = canvasHeight),
                    end = Offset(x = canvasWidth, y = canvasHeight),
                    strokeWidth = 5f
                )
                //------------------------

                //Vertical-------------
//                drawLine(
//                    color = Color.Gray,
//                    start = Offset(x = freeSpace, y = canvasHeight),
//                    end = Offset(x = freeSpace, y = 0f),
//                    strokeWidth = 5f
//                )
                //---------------
                //****************
            }
        }
   Column(modifier = Modifier
       .fillMaxHeight(),
       verticalArrangement = Arrangement.SpaceEvenly
       ) {
       Text("High",style = MaterialTheme.typography.caption.copy(fontSize = 9.sp,color = Color.White))
       Text("Medium",style = MaterialTheme.typography.caption.copy(fontSize = 9.sp,color = Color.White))
       Text("Low",style = MaterialTheme.typography.caption.copy(fontSize = 9.sp,color = Color.White))
//       (0 until 110 step 10).reversed().forEach {
//           Text("$it",style = MaterialTheme.typography.caption.copy(fontSize = 7.sp,color = Color.White),
//           modifier = Modifier.padding(bottom = (heightOfBox/12).dp))
//       }
   }

    }
}
@Composable
fun MyChartDaily(rainProbability:List<Data>) {
    val freeSpace = 40f
    val dataSpace = 140f
    var dataPoints = mutableListOf<ChartData>()
    var dataOffsets = mutableListOf<Offset>()
    val state = rememberScrollState()

val lineColor = if(isSystemInDarkTheme()) red_700 else blue_700


    BoxWithConstraints(contentAlignment = Alignment.TopStart, modifier = Modifier.fillMaxSize() ) {
        val heightOfBox = this.maxHeight.value - freeSpace*2

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomStart) {
            LazyRow(modifier = Modifier.fillMaxSize()) {

                item {
                    BoxWithConstraints(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomStart
                    ) {

                        val maxHeight = this.maxHeight
                        val perCentHeight = maxHeight.value / 100

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val canvasWidth = size.width
                            val canvasHeight = size.height - freeSpace

                            rainProbability.forEachIndexed { index, item ->
                                val offset = Offset(
                                    x = index * dataSpace,
                                    y = (canvasHeight - item.precipProbability!! * canvasHeight).toFloat()
                                )
                                val input = ChartData(
                                    index = index, offsets = offset, time = item.time!!.toLong(),
                                    displayedValue = "${(100 * item.precipProbability!!).roundToInt()}%"
                                )
                                dataPoints.add(input)
                            }





                            //Create Chart------------

                            drawPath(
                                path = Path().apply {

                                    dataPoints.forEachIndexed { i, value ->

                                        when (i) {
                                            0 -> {
                                                moveTo(x = freeSpace, y = canvasHeight)
                                                relativeLineTo(
                                                    dx = 0f,
                                                    dy = value.offsets.y - (canvasHeight)
                                                )
                                            }
                                            dataPoints.size - 1 -> {
                                                val preValue = dataPoints[i - 1].offsets
//                                                relativeCubicTo(
//                                                    dx1 = dataSpace/4,
//                                                    dy1 =  (value.offsets.y - preValue.y)/4,
//                                                    dy2 =  3*(value.offsets.y - preValue.y)/4,
//                                                    dx2 = 3*dataSpace/4,
//                                                    dx3 = dataSpace,
//                                                    dy3 = value.offsets.y - preValue.y
//                                                )
                                                relativeQuadraticBezierTo(
                                                    dx1 = dataSpace/2,dy1 = (value.offsets.y - preValue.y)/2,dx2 =  dataSpace,dy2 = value.offsets.y - preValue.y
                                                )
//                                                relativeLineTo(
//                                                    dx = dataSpace,
//                                                    dy = value.offsets.y - preValue.y
//                                                )
                                                relativeLineTo(
                                                    dx = 0f,
                                                    dy = canvasHeight - (value.offsets.y)
                                                )
                                                close()
                                            }
                                            else -> {
                                                val preValue = dataPoints[i - 1].offsets

//                                                relativeCubicTo(
//                                                    dx1 = dataSpace/4,
//                                                    dy1 =  (value.offsets.y - preValue.y)/4,
//                                                    dy2 =  3*(value.offsets.y - preValue.y)/4,
//                                                    dx2 = 3*dataSpace/4,
//                                                    dx3 = dataSpace,
//                                                    dy3 = value.offsets.y - preValue.y
//                                                )
                                                        relativeQuadraticBezierTo(
                                                            dx1 = dataSpace/2,dy1 = (value.offsets.y - preValue.y)/2,dx2 =  dataSpace,dy2 = value.offsets.y - preValue.y
                                                        )

//                                                    relativeLineTo(
//                                                        dx = dataSpace,
//                                                        dy = value.offsets.y - preValue.y
//                                                    )
//                                                val curveRadius = 20
//                                                val x1 = 0f
//                                                    val x2 = dataSpace
//                                                        val y1 = 0f
//                                                            val y2 = value.offsets.y - preValue.y
//                                                val midX: Float = x1 + (x2 - x1) / 2
//                                                val midY: Float = y1 + (y2 - y1) / 2
//                                                val xDiff: Float = midX - x1
//                                                val yDiff: Float = midY - y1
//                                                val angle = Math.atan2(
//                                                    yDiff.toDouble(),
//                                                    xDiff.toDouble()
//                                                ) * (180 / Math.PI) - 90
//                                                val angleRadians = Math.toRadians(angle)
//                                                val multiplier = if(value.offsets.y - preValue.y > 0 ) -1 else 1
//                                                val pointX =
//                                                    (midX + curveRadius * Math.cos(angleRadians)).toFloat()
//                                                val pointY =
//                                                    (midY + curveRadius * Math.sin(angleRadians)).toFloat()
//                                                relativeCubicTo(x1,y1,pointX, pointY, x2, y2)
                                            }
                                        }


                                    }
                                    val lastValue = dataPoints.last().offsets

                                }, brush = Brush.verticalGradient(
                                    colors = listOf(
                                        lineColor, lineColor.copy(alpha = 0.6f),
                                        lineColor.copy(alpha = 0.4f), lineColor.copy(alpha = 0.2f)
                                    ),

                                )
                            )

                            //---------------------
                        }

                        // Times-------------
                        Row(
                            modifier = Modifier
                                .height(20.dp)
                                .padding(start = 12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy((dataSpace / 2 - 50).dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            rainProbability.forEach {
                                Box(
                                    modifier = Modifier.width(30.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        SimpleDateFormat("EEE").format(1000*it.time!!.toLong())
                                        ,
                                        style = MaterialTheme.typography.caption.copy(fontSize = 9.sp)
                                    )
                                }
                                // Text(it.displayedValue, style = MaterialTheme.typography.caption.copy(fontSize = 10.sp))

                            }
                        }
                        //--------------------
                    }
                }
                item {
                    Spacer(modifier = Modifier.width(20.dp))
                }
            }


            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height - freeSpace
                // Axis****************
                // Horizontal--------
                drawLine(
                    color = Color.Gray,
                    start = Offset(x = freeSpace, y = canvasHeight),
                    end = Offset(x = canvasWidth, y = canvasHeight),
                    strokeWidth = 4f
                )
                //------------------------

                //Vertical-------------
//                drawLine(
//                    color = Color.Gray,
//                    start = Offset(x = freeSpace, y = canvasHeight),
//                    end = Offset(x = freeSpace, y = 0f),
//                    strokeWidth = 4f
//                )
                //---------------
                //****************
            }
        }
        Column(modifier = Modifier
            .fillMaxHeight()
            .width(12.dp)
      ) {
            (0 until 110 step 10).reversed().forEach {
                Text("$it",style = MaterialTheme.typography.caption.copy(fontSize = 7.sp),
                    modifier = Modifier.padding(bottom = (heightOfBox/11.5).dp))
            }
        }

    }
}
//@Preview
//@Composable
//fun ChartPreview(){
//    val previewData = listOf(0.1,0.2,0.0,0.6,0.3,0.1,0.3,0.6,0.3,1.0,0.3)
//    val previewTimes = listOf<Long>(12,13,14,15,16,17,18,19,20,21,22)
//    DarkWeatherTheme {
//        Box(modifier = Modifier
//            .height(200.dp)
//           ) {
//            MyChartView(data = previewData, times = previewTimes)
//        }
//    }
//
//}