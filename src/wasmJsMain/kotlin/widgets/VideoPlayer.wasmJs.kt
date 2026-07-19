package widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale

@Composable
actual fun VideoPlayer(
  path: String,
  fallbackGifPath: String?,
  modifier: Modifier,
  background: Color,
) {
  if (fallbackGifPath != null) {
    GifImage(path = fallbackGifPath, contentDescription = null, modifier = modifier, contentScale = ContentScale.Fit)
  } else {
    Box(modifier.background(background))
  }
}
