package livewire.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.NavigateNext: ImageVector
  get() {
    if (_NavigateNext != null) {
      return _NavigateNext!!
    }
    _NavigateNext = ImageVector.Builder(
      name = "NavigateNext",
      defaultWidth = 24.dp,
      defaultHeight = 24.dp,
      viewportWidth = 24f,
      viewportHeight = 24f
    ).apply {
      path(fill = SolidColor(Color.Black)) {
        moveTo(10f, 6f)
        lineTo(8.59f, 7.41f)
        lineTo(13.17f, 12f)
        lineToRelative(-4.58f, 4.59f)
        lineTo(10f, 18f)
        lineToRelative(6f, -6f)
        close()
      }
    }.build()

    return _NavigateNext!!
  }

@Suppress("ObjectPropertyName")
private var _NavigateNext: ImageVector? = null
