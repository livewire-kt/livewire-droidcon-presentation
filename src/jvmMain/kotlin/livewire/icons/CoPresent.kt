package livewire.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.CoPresent: ImageVector
  get() {
    if (_CoPresent != null) {
      return _CoPresent!!
    }
    _CoPresent = ImageVector.Builder(
      name = "CoPresent",
      defaultWidth = 24.dp,
      defaultHeight = 24.dp,
      viewportWidth = 960f,
      viewportHeight = 960f
    ).apply {
      path(fill = SolidColor(Color.Black)) {
        moveTo(840f, 840f)
        verticalLineToRelative(-640f)
        lineTo(120f, 200f)
        verticalLineToRelative(280f)
        quadToRelative(0f, 17f, -11.5f, 28.5f)
        reflectiveQuadTo(80f, 520f)
        quadToRelative(-17f, 0f, -28.5f, -11.5f)
        reflectiveQuadTo(40f, 480f)
        verticalLineToRelative(-280f)
        quadToRelative(0f, -33f, 23.5f, -56.5f)
        reflectiveQuadTo(120f, 120f)
        horizontalLineToRelative(720f)
        quadToRelative(33f, 0f, 56.5f, 23.5f)
        reflectiveQuadTo(920f, 200f)
        verticalLineToRelative(560f)
        quadToRelative(0f, 33f, -23.5f, 56.5f)
        reflectiveQuadTo(840f, 840f)
        close()
        moveTo(247f, 513f)
        quadToRelative(-47f, -47f, -47f, -113f)
        reflectiveQuadToRelative(47f, -113f)
        quadToRelative(47f, -47f, 113f, -47f)
        reflectiveQuadToRelative(113f, 47f)
        quadToRelative(47f, 47f, 47f, 113f)
        reflectiveQuadToRelative(-47f, 113f)
        quadToRelative(-47f, 47f, -113f, 47f)
        reflectiveQuadToRelative(-113f, -47f)
        close()
        moveTo(416.5f, 456.5f)
        quadTo(440f, 433f, 440f, 400f)
        reflectiveQuadToRelative(-23.5f, -56.5f)
        quadTo(393f, 320f, 360f, 320f)
        reflectiveQuadToRelative(-56.5f, 23.5f)
        quadTo(280f, 367f, 280f, 400f)
        reflectiveQuadToRelative(23.5f, 56.5f)
        quadTo(327f, 480f, 360f, 480f)
        reflectiveQuadToRelative(56.5f, -23.5f)
        close()
        moveTo(120f, 880f)
        quadToRelative(-33f, 0f, -56.5f, -23.5f)
        reflectiveQuadTo(40f, 800f)
        verticalLineToRelative(-32f)
        quadToRelative(0f, -34f, 17.5f, -62.5f)
        reflectiveQuadTo(104f, 662f)
        quadToRelative(62f, -31f, 126f, -46.5f)
        reflectiveQuadTo(360f, 600f)
        quadToRelative(66f, 0f, 130f, 15.5f)
        reflectiveQuadTo(616f, 662f)
        quadToRelative(29f, 15f, 46.5f, 43.5f)
        reflectiveQuadTo(680f, 768f)
        verticalLineToRelative(32f)
        quadToRelative(0f, 33f, -23.5f, 56.5f)
        reflectiveQuadTo(600f, 880f)
        lineTo(120f, 880f)
        close()
        moveTo(120f, 800f)
        horizontalLineToRelative(480f)
        verticalLineToRelative(-32f)
        quadToRelative(0f, -11f, -5.5f, -20f)
        reflectiveQuadTo(580f, 734f)
        quadToRelative(-54f, -27f, -109f, -40.5f)
        reflectiveQuadTo(360f, 680f)
        quadToRelative(-56f, 0f, -111f, 13.5f)
        reflectiveQuadTo(140f, 734f)
        quadToRelative(-9f, 5f, -14.5f, 14f)
        reflectiveQuadToRelative(-5.5f, 20f)
        verticalLineToRelative(32f)
        close()
        moveTo(360f, 400f)
        close()
        moveTo(360f, 800f)
        close()
      }
    }.build()

    return _CoPresent!!
  }

@Suppress("ObjectPropertyName")
private var _CoPresent: ImageVector? = null
