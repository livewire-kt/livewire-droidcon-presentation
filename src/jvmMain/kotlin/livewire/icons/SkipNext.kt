package livewire.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.SkipNext: ImageVector
  get() {
    if (_SkipNext != null) {
      return _SkipNext!!
    }
    _SkipNext = ImageVector.Builder(
      name = "SkipNext",
      defaultWidth = 24.dp,
      defaultHeight = 24.dp,
      viewportWidth = 24f,
      viewportHeight = 24f
    ).apply {
      path(fill = SolidColor(Color.Black)) {
        moveTo(6f, 18f)
        lineToRelative(8.5f, -6f)
        lineTo(6f, 6f)
        verticalLineToRelative(12f)
        close()
        moveTo(16f, 6f)
        verticalLineToRelative(12f)
        horizontalLineToRelative(2f)
        verticalLineTo(6f)
        close()
      }
    }.build()

    return _SkipNext!!
  }

@Suppress("ObjectPropertyName")
private var _SkipNext: ImageVector? = null
