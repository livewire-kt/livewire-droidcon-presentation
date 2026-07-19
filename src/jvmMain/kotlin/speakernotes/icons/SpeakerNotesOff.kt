package speakernotes.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.SpeakerNotesOff: ImageVector
  get() {
    if (_SpeakerNotesOff != null) {
      return _SpeakerNotesOff!!
    }
    _SpeakerNotesOff = ImageVector.Builder(
      name = "SpeakerNotesOff",
      defaultWidth = 24.dp,
      defaultHeight = 24.dp,
      viewportWidth = 960f,
      viewportHeight = 960f,
    ).apply {
      path(fill = SolidColor(Color.Black)) {
        moveTo(251.5f, 548.5f)
        quadTo(240f, 537f, 240f, 520f)
        reflectiveQuadToRelative(11.5f, -28.5f)
        quadTo(263f, 480f, 280f, 480f)
        reflectiveQuadToRelative(28.5f, 11.5f)
        quadTo(320f, 503f, 320f, 520f)
        reflectiveQuadToRelative(-11.5f, 28.5f)
        quadTo(297f, 560f, 280f, 560f)
        reflectiveQuadToRelative(-28.5f, -11.5f)
        close()
        moveTo(828f, 714f)
        lineToRelative(-74f, -74f)
        horizontalLineToRelative(46f)
        verticalLineToRelative(-480f)
        lineTo(274f, 160f)
        lineToRelative(-80f, -80f)
        horizontalLineToRelative(606f)
        quadToRelative(33f, 0f, 56.5f, 23.5f)
        reflectiveQuadTo(880f, 160f)
        verticalLineToRelative(480f)
        quadToRelative(0f, 26f, -14.5f, 45.5f)
        reflectiveQuadTo(828f, 714f)
        close()
        moveTo(554f, 440f)
        lineToRelative(-80f, -80f)
        horizontalLineToRelative(246f)
        verticalLineToRelative(80f)
        lineTo(554f, 440f)
        close()
        moveTo(820f, 932f)
        lineTo(606f, 720f)
        lineTo(240f, 720f)
        lineTo(80f, 880f)
        verticalLineToRelative(-688f)
        lineToRelative(-52f, -52f)
        lineToRelative(56f, -56f)
        lineTo(876f, 876f)
        lineToRelative(-56f, 56f)
        close()
        moveTo(344f, 456f)
        close()
        moveTo(514f, 400f)
        close()
        moveTo(251.5f, 428.5f)
        quadTo(240f, 417f, 240f, 400f)
        reflectiveQuadToRelative(11.5f, -28.5f)
        quadTo(263f, 360f, 280f, 360f)
        reflectiveQuadToRelative(28.5f, 11.5f)
        quadTo(320f, 383f, 320f, 400f)
        reflectiveQuadToRelative(-11.5f, 28.5f)
        quadTo(297f, 440f, 280f, 440f)
        reflectiveQuadToRelative(-28.5f, -11.5f)
        close()
        moveTo(434f, 320f)
        lineToRelative(-34f, -34f)
        verticalLineToRelative(-46f)
        horizontalLineToRelative(320f)
        verticalLineToRelative(80f)
        lineTo(434f, 320f)
        close()
        moveTo(160f, 272f)
        verticalLineToRelative(413f)
        lineToRelative(46f, -45f)
        horizontalLineToRelative(322f)
        lineTo(160f, 272f)
        close()
      }
    }.build()

    return _SpeakerNotesOff!!
  }

@Suppress("ObjectPropertyName")
private var _SpeakerNotesOff: ImageVector? = null
