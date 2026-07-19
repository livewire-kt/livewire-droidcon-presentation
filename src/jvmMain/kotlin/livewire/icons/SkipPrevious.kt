package livewire.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.SkipPrevious: ImageVector
  get() {
    if (_SkipPrevious != null) {
      return _SkipPrevious!!
    }
    _SkipPrevious = ImageVector.Builder(
      name = "SkipPrevious",
      defaultWidth = 24.dp,
      defaultHeight = 24.dp,
      viewportWidth = 24f,
      viewportHeight = 24f
    ).apply {
      path(fill = SolidColor(Color.Black)) {
        moveTo(6f, 6f)
        horizontalLineToRelative(2f)
        verticalLineToRelative(12f)
        horizontalLineTo(6f)
        close()
        moveTo(9.5f, 12f)
        lineToRelative(8.5f, 6f)
        verticalLineTo(6f)
        close()
      }
    }.build()

    return _SkipPrevious!!
  }

@Suppress("ObjectPropertyName")
private var _SkipPrevious: ImageVector? = null
