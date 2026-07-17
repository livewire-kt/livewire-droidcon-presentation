package widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.delay
import livewire_presentation.generated.resources.Res
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Codec
import org.jetbrains.skia.Data
import org.jetbrains.skia.Image as SkiaImage

/**
 * Plays an animated GIF from composeResources/files (desktop/JVM via skiko).
 *
 * @param path resource path such as "files/compose_fun.gif"
 */
@Composable
fun GifImage(
  path: String,
  contentDescription: String? = null,
  modifier: Modifier = Modifier,
  contentScale: ContentScale = ContentScale.Fit,
) {
  var frame by remember(path) { mutableStateOf<ImageBitmap?>(null) }

  LaunchedEffect(path) {
    val bytes = Res.readBytes(path)
    val codec = Codec.makeFromData(Data.makeFromBytes(bytes))
    val bitmap = Bitmap().apply { allocPixels(codec.imageInfo) }
    if (codec.frameCount <= 1) {
      codec.readPixels(bitmap, 0)
      frame = SkiaImage.makeFromBitmap(bitmap).toComposeImageBitmap()
      return@LaunchedEffect
    }
    
    var twoAgo: SkiaImage? = null
    var oneAgo: SkiaImage? = null
    try {
      while (true) {
        for (i in 0 until codec.frameCount) {
          codec.readPixels(bitmap, i)
          val image = SkiaImage.makeFromBitmap(bitmap)
          frame = image.toComposeImageBitmap()
          twoAgo?.close()
          twoAgo = oneAgo
          oneAgo = image
          val duration = codec.getFrameInfo(i).duration
          delay(if (duration <= 0) 100L else duration.toLong())
        }
      }
    } finally {
      twoAgo?.close()
      oneAgo?.close()
    }
  }

  val current = frame
  if (current != null) {
    Image(
      bitmap = current,
      contentDescription = contentDescription,
      modifier = modifier,
      contentScale = contentScale,
    )
  } else {
    Box(modifier)
  }
}
