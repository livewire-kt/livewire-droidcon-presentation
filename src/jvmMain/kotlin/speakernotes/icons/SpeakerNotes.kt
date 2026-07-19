package speakernotes.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.SpeakerNotes: ImageVector
  get() {
    if (_SpeakerNotes != null) {
      return _SpeakerNotes!!
    }
    _SpeakerNotes = ImageVector.Builder(
      name = "SpeakerNotes",
      defaultWidth = 24.dp,
      defaultHeight = 24.dp,
      viewportWidth = 960f,
      viewportHeight = 960f,
    ).apply {
      path(fill = SolidColor(Color.Black)) {
        moveTo(308.5f, 548.5f)
        quadTo(320f, 537f, 320f, 520f)
        reflectiveQuadToRelative(-11.5f, -28.5f)
        quadTo(297f, 480f, 280f, 480f)
        reflectiveQuadToRelative(-28.5f, 11.5f)
        quadTo(240f, 503f, 240f, 520f)
        reflectiveQuadToRelative(11.5f, 28.5f)
        quadTo(263f, 560f, 280f, 560f)
        reflectiveQuadToRelative(28.5f, -11.5f)
        close()
        moveTo(308.5f, 428.5f)
        quadTo(320f, 417f, 320f, 400f)
        reflectiveQuadToRelative(-11.5f, -28.5f)
        quadTo(297f, 360f, 280f, 360f)
        reflectiveQuadToRelative(-28.5f, 11.5f)
        quadTo(240f, 383f, 240f, 400f)
        reflectiveQuadToRelative(11.5f, 28.5f)
        quadTo(263f, 440f, 280f, 440f)
        reflectiveQuadToRelative(28.5f, -11.5f)
        close()
        moveTo(308.5f, 308.5f)
        quadTo(320f, 297f, 320f, 280f)
        reflectiveQuadToRelative(-11.5f, -28.5f)
        quadTo(297f, 240f, 280f, 240f)
        reflectiveQuadToRelative(-28.5f, 11.5f)
        quadTo(240f, 263f, 240f, 280f)
        reflectiveQuadToRelative(11.5f, 28.5f)
        quadTo(263f, 320f, 280f, 320f)
        reflectiveQuadToRelative(28.5f, -11.5f)
        close()
        moveTo(400f, 560f)
        horizontalLineToRelative(200f)
        verticalLineToRelative(-80f)
        lineTo(400f, 480f)
        verticalLineToRelative(80f)
        close()
        moveTo(400f, 440f)
        horizontalLineToRelative(320f)
        verticalLineToRelative(-80f)
        lineTo(400f, 360f)
        verticalLineToRelative(80f)
        close()
        moveTo(400f, 320f)
        horizontalLineToRelative(320f)
        verticalLineToRelative(-80f)
        lineTo(400f, 240f)
        verticalLineToRelative(80f)
        close()
        moveTo(80f, 880f)
        verticalLineToRelative(-720f)
        quadToRelative(0f, -33f, 23.5f, -56.5f)
        reflectiveQuadTo(160f, 80f)
        horizontalLineToRelative(640f)
        quadToRelative(33f, 0f, 56.5f, 23.5f)
        reflectiveQuadTo(880f, 160f)
        verticalLineToRelative(480f)
        quadToRelative(0f, 33f, -23.5f, 56.5f)
        reflectiveQuadTo(800f, 720f)
        lineTo(240f, 720f)
        lineTo(80f, 880f)
        close()
        moveTo(206f, 640f)
        horizontalLineToRelative(594f)
        verticalLineToRelative(-480f)
        lineTo(160f, 160f)
        verticalLineToRelative(525f)
        lineToRelative(46f, -45f)
        close()
        moveTo(160f, 640f)
        verticalLineToRelative(-480f)
        verticalLineToRelative(480f)
        close()
      }
    }.build()

    return _SpeakerNotes!!
  }

@Suppress("ObjectPropertyName")
private var _SpeakerNotes: ImageVector? = null
