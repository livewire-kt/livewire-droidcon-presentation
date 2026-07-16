package widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Standard content slide: small amber mono kicker (`// THE PROBLEM`) above a big Black Han Sans
 * title, then free-form content.
 */
@Composable
fun TitledSlide(
  title: String,
  kicker: String? = null,
  titleSize: TextUnit = 22.sp,
  horizontalAlignment: Alignment.Horizontal = Alignment.Start,
  content: @Composable ColumnScope.() -> Unit = {},
) {
  val fonts = LocalLivewireFonts.current
  Column(
    modifier = Modifier.fillMaxSize().padding(horizontal = 36.dp, vertical = 22.dp),
    horizontalAlignment = horizontalAlignment,
  ) {
    if (kicker != null) {
      Text(
        text = kicker,
        fontFamily = fonts.mono,
        color = Livewire.Amber,
        fontSize = 7.sp,
        lineHeight = 14.sp
      )
    }
    if (title.isNotBlank()) {
      Text(
        text = title,
        fontFamily = fonts.title,
        color = Livewire.Cream,
        fontSize = titleSize,
        lineHeight = titleSize
      )
      Spacer(Modifier.height(12.dp))
    }
    content()
  }
}

/** Section divider: `[ SECTION ]`, big amber number, title, gray subtitle. */
@Composable
fun SectionSlide(number: String, title: String, subtitle: String) {
  val fonts = LocalLivewireFonts.current
  Column(
    modifier = Modifier.fillMaxSize().padding(36.dp),
    horizontalAlignment = Alignment.Start,
    verticalArrangement = Arrangement.Center,
  ) {
    Text(text = "[ SECTION ]", fontFamily = fonts.mono, color = Livewire.Amber, fontSize = 8.sp)

    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(text = number, fontFamily = fonts.title, color = Livewire.Red, fontSize = 90.sp)

      Spacer(Modifier.width(8.dp))

      Canvas(modifier = Modifier.size(width = 40.dp, height = 12.dp)) {
        drawLine(
          color = Livewire.Amber,
          start = Offset(0f, size.height / 2),
          end = Offset(size.width, size.height / 2),
          strokeWidth = 1.dp.toPx(),
        )
        drawCircle(color = Livewire.Red, radius = 3.dp.toPx())
      }

      Spacer(Modifier.width(8.dp))

      Column {
        Text(
          text = title,
          fontFamily = fonts.title,
          color = Livewire.Cream,
          fontSize = 32.sp,
          lineHeight = 32.sp,
        )
        Text(text = subtitle, color = Livewire.Gray, fontFamily = fonts.mono, fontSize = 11.sp)
      }
    }
  }
}

@Composable
fun ColumnScope.Bullet(
  text: AnnotatedString,
  visible: Boolean = true,
  indent: Int = 0,
  style: TextStyle = TextStyle.Default,
) {
  AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
    Row(
      modifier = Modifier.padding(
        start = (indent * 14).dp,
        top = 3.dp,
        bottom = 3.dp
      ),
      verticalAlignment = Alignment.Top
    ) {
      Text("⭕", color = Livewire.Amber, fontSize = 12.sp)
      Spacer(Modifier.width(8.dp))
      Text(
        text,
        style = style,
        lineHeight = 15.sp,
        modifier = Modifier.padding(top = 3.dp)
      )
    }
  }
}

@Composable
fun ColumnScope.Bullet(
  text: String,
  visible: Boolean = true,
  indent: Int = 0,
  style: TextStyle = TextStyle.Default,
) = Bullet(AnnotatedString(text), visible, indent, style)


@Composable
internal fun CodeBox(
  modifier: Modifier = Modifier,
  contentAlignment: Alignment = Alignment.TopStart,
  content: @Composable BoxScope.() -> Unit,
) {
  val cornerSize = 8.dp
  val lineWidth = 1.dp
  Box(
    modifier = modifier
      .background(Livewire.Background)
      .drawWithContent {
        drawContent()

        drawRect(
          color = Livewire.Slate,
          style = Stroke(1.dp.toPx())
        )

        // TOP LEFT
        drawLine(
          color = Livewire.Amber,
          start = Offset(0f, 0f),
          end = Offset(cornerSize.toPx(), 0f),
          strokeWidth = lineWidth.toPx(),
          cap = StrokeCap.Round,
        )
        drawLine(
          color = Livewire.Amber,
          start = Offset(0f, 0f),
          end = Offset(0f, cornerSize.toPx()),
          strokeWidth = lineWidth.toPx(),
          cap = StrokeCap.Round,
        )

        // TOP RIGHT
        drawLine(
          color = Livewire.Amber,
          start = Offset(size.width, 0f),
          end = Offset(size.width - cornerSize.toPx(), 0f),
          strokeWidth = lineWidth.toPx(),
          cap = StrokeCap.Round,
        )
        drawLine(
          color = Livewire.Amber,
          start = Offset(size.width, 0f),
          end = Offset(size.width, cornerSize.toPx()),
          strokeWidth = lineWidth.toPx(),
          cap = StrokeCap.Round,
        )

        // BOTTOM LEFT
        drawLine(
          color = Livewire.Amber,
          start = Offset(0f, size.height),
          end = Offset(0f, size.height - cornerSize.toPx()),
          strokeWidth = lineWidth.toPx(),
          cap = StrokeCap.Round,
        )
        drawLine(
          color = Livewire.Amber,
          start = Offset(0f, size.height),
          end = Offset(cornerSize.toPx(), size.height),
          strokeWidth = lineWidth.toPx(),
          cap = StrokeCap.Round,
        )

        // BOTTOM LEFT
        drawLine(
          color = Livewire.Amber,
          start = Offset(size.width, size.height),
          end = Offset(size.width, size.height - cornerSize.toPx()),
          strokeWidth = lineWidth.toPx(),
          cap = StrokeCap.Round,
        )
        drawLine(
          color = Livewire.Amber,
          start = Offset(size.width, size.height),
          end = Offset(size.width - cornerSize.toPx(), size.height),
          strokeWidth = lineWidth.toPx(),
          cap = StrokeCap.Round,
        )
      }
      .padding(8.dp),
    contentAlignment = contentAlignment,
    content = content,
  )
}
