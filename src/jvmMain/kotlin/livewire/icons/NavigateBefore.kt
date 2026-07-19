package livewire.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.NavigateBefore: ImageVector
  get() {
    if (_NavigateBefore != null) {
      return _NavigateBefore!!
    }
    _NavigateBefore = ImageVector.Builder(
      name = "NavigateBefore",
      defaultWidth = 24.dp,
      defaultHeight = 24.dp,
      viewportWidth = 24f,
      viewportHeight = 24f
    ).apply {
      path(fill = SolidColor(Color.Black)) {
        moveTo(15.41f, 7.41f)
        lineTo(14f, 6f)
        lineToRelative(-6f, 6f)
        lineToRelative(6f, 6f)
        lineToRelative(1.41f, -1.41f)
        lineTo(10.83f, 12f)
        close()
      }
    }.build()

    return _NavigateBefore!!
  }

@Suppress("ObjectPropertyName")
private var _NavigateBefore: ImageVector? = null
