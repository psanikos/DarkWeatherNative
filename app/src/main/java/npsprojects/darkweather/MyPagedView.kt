package npsprojects.darkweather

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PreviewItem(index:Int){
    Box(modifier = Modifier
        .fillMaxSize()
        .background(if (index == 0) Color.Blue else Color.Red)){
        Text("$index",style= MaterialTheme.typography.h1)
    }
}

@Composable
fun MyPagedView(){
    var offset:Float by remember { mutableStateOf(0f) }
    var currentIndex:Int by remember { mutableStateOf(0)}

val state = rememberScrollState()

    BoxWithConstraints() {
    val width = this.maxWidth

  Row(modifier = Modifier
      .fillMaxHeight()
      .width(width = width)
      .horizontalScroll(state =state,enabled = true)
  ) {
      (0..5).forEachIndexed{
          index,item ->

              Box(modifier = Modifier
                  .fillMaxHeight()
                  .width(width = width).draggable(
                      orientation = Orientation.Horizontal,
                      state = rememberDraggableState { delta ->
                          offset = delta

                      },
                      onDragStopped = {
                          if (offset > 0){
                              if (currentIndex > 0){
                                 state.animateScrollTo(value = currentIndex - 1)
                                  currentIndex --
                              }
                              offset = 0f
                          }
                          else if (offset < 0){
                              if (currentIndex < 4) {
                                  state.animateScrollTo(value = currentIndex + 1)
                                  currentIndex++
                              }
                              offset = 0f
                          }
                      }

                  )) {
                  PreviewItem(index = item)
              }

      }
  }

}
}

@Preview
@Composable
fun PreviewPaged(){
    MyPagedView()
}