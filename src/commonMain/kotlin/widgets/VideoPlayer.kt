package widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Plays a video from composeResources/files.
 *
 * On desktop this decodes through VLCJ / libVLC (see VideoPlayer.jvm.kt); on web there is no
 * libVLC, so the [fallbackGifPath] GIF is shown instead.
 *
 * Behaves like [GifImage]: autoplays, shows no transport controls, and freezes on the final frame.
 * The video is letterboxed inside [background] so its bars blend with the deck.
 *
 * @param path resource path such as "files/outro.mp4"
 * @param fallbackGifPath optional GIF resource shown if video playback is unavailable, e.g. "files/outro.gif"
 */
@Composable
expect fun VideoPlayer(
  path: String,
  fallbackGifPath: String? = null,
  modifier: Modifier = Modifier,
  background: Color = Color(0xFF0B0E11),
)
