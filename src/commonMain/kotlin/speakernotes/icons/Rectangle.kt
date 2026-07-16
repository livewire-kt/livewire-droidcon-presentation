package speakernotes.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rectangle: ImageVector
  get() {
    if (_Rectangle != null) {
      return _Rectangle!!
    }
    _Rectangle = ImageVector.Builder(
      name = "Rectangle",
      defaultWidth = 24.dp,
      defaultHeight = 24.dp,
      viewportWidth = 960f,
      viewportHeight = 960f,
    ).apply {
      path(fill = SolidColor(Color.Black)) {
        moveTo(80f, 800f)
        verticalLineToRelative(-640f)
        horizontalLineToRelative(800f)
        verticalLineToRelative(640f)
        lineTo(80f, 800f)
        close()
        moveTo(160f, 720f)
        horizontalLineToRelative(640f)
        verticalLineToRelative(-480f)
        lineTo(160f, 240f)
        verticalLineToRelative(480f)
        close()
        moveTo(160f, 720f)
        verticalLineToRelative(-480f)
        verticalLineToRelative(480f)
        close()
      }
    }.build()

    return _Rectangle!!
  }

@Suppress("ObjectPropertyName")
private var _Rectangle: ImageVector? = null
