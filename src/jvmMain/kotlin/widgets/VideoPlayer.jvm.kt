package widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import livewire_presentation.generated.resources.Res
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.ImageInfo
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer
import uk.co.caprica.vlcj.player.embedded.videosurface.CallbackVideoSurface
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurfaceAdapters
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormatCallback
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.RenderCallback
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.format.RV32BufferFormat
import java.io.File
import java.nio.ByteBuffer
import kotlin.concurrent.thread
import org.jetbrains.skia.Image as SkiaImage

/**
 * Plays a video from composeResources/files using VLCJ / libVLC.
 *
 * libVLC decodes natively and hands each frame to a [CallbackVideoSurface]; we copy it into a Skia
 * bitmap and publish it as an [ImageBitmap]. Because the frame is drawn through an ordinary Compose
 * [Image], the player is a real node in the composable tree — it participates in slide transitions,
 * the overview grid, and image export, unlike a native video surface embedded via SwingPanel.
 *
 * Behaves like [GifImage]: autoplays, shows no transport controls, and freezes on the final frame
 * (once playback ends libVLC simply stops delivering frames, so the last one stays on screen). The
 * video is letterboxed inside [background] so its bars blend with the deck.
 *
 * Requires libVLC (VLC) to be installed on the host; see build.gradle.kts. If libVLC can't be
 * initialised and [fallbackGifPath] is provided, the GIF is shown instead so the deck never crashes
 * mid-talk on a machine without VLC.
 *
 * @param path resource path such as "files/outro.mp4"
 * @param fallbackGifPath optional GIF resource shown if libVLC is unavailable, e.g. "files/outro.gif"
 */
@Composable
actual fun VideoPlayer(
  path: String,
  fallbackGifPath: String?,
  modifier: Modifier,
  background: Color,
) {
  var frame by remember(path) { mutableStateOf<ImageBitmap?>(null) }
  var failed by remember(path) { mutableStateOf(false) }

  // libVLC plays from a file/MRL, not raw resource bytes — extract the bundled asset once.
  val mediaPath by produceState<String?>(null, path) {
    val bytes = Res.readBytes(path)
    value = withContext(Dispatchers.IO) {
      File.createTempFile("livewire-video-", "." + path.substringAfterLast('.', "mp4"))
        .apply {
          deleteOnExit()
          writeBytes(bytes)
        }
        .absolutePath
    }
  }

  val currentPath = mediaPath
  DisposableEffect(currentPath) {
    var player: VlcFramePlayer? = null
    if (currentPath != null) {
      try {
        player = VlcFramePlayer(currentPath) { bmp -> frame = bmp }.also(VlcFramePlayer::start)
      } catch (t: Throwable) {
        // Missing/broken libVLC surfaces as an UnsatisfiedLinkError or RuntimeException here.
        failed = true
      }
    }
    onDispose { player?.release() }
  }

  if (failed && fallbackGifPath != null) {
    GifImage(path = fallbackGifPath, contentDescription = null, modifier = modifier, contentScale = ContentScale.Fit)
    return
  }

  Box(modifier.background(background), contentAlignment = Alignment.Center) {
    frame?.let {
      Image(
        bitmap = it,
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Fit,
      )
    }
  }
}

/**
 * Wraps a libVLC embedded player rendering into off-screen buffers. Frame delivery happens on a
 * native VLC thread, which calls [onFrame] with the latest [ImageBitmap].
 */
private class VlcFramePlayer(
  private val mediaPath: String,
  private val onFrame: (ImageBitmap) -> Unit,
) {
  private var factory: MediaPlayerFactory? = null
  private var player: EmbeddedMediaPlayer? = null

  // Per-frame scratch, reused to avoid churn. Each published SkiaImage pins native memory, so we
  // free it two frames after it's shown — never the one the render thread may currently be drawing.
  private var scratch = ByteArray(0)
  private var bitmap: Bitmap? = null
  private var info: ImageInfo? = null
  private var twoAgo: SkiaImage? = null
  private var oneAgo: SkiaImage? = null

  private val bufferFormatCallback = object : BufferFormatCallback {
    override fun getBufferFormat(sourceWidth: Int, sourceHeight: Int): BufferFormat =
      RV32BufferFormat(sourceWidth, sourceHeight)

    override fun newFormatSize(bufferWidth: Int, bufferHeight: Int, displayWidth: Int, displayHeight: Int) {}

    override fun allocatedBuffers(buffers: Array<ByteBuffer>) {}
  }

  private val renderCallback = object : RenderCallback {
    override fun lock(mediaPlayer: MediaPlayer) {}

    override fun display(
      mediaPlayer: MediaPlayer,
      nativeBuffers: Array<ByteBuffer>,
      bufferFormat: BufferFormat,
      displayWidth: Int,
      displayHeight: Int,
    ) {
      val width = bufferFormat.width
      val height = bufferFormat.height
      val pitch = bufferFormat.pitches[0]
      val size = pitch * height
      if (scratch.size != size) scratch = ByteArray(size)
      // RV32 is BGRA; copy the native buffer without disturbing VLC's own position.
      nativeBuffers[0].duplicate().apply {
        rewind()
        get(scratch)
      }

      var imageInfo = info
      var bmp = bitmap
      if (imageInfo == null || bmp == null) {
        imageInfo = ImageInfo(width, height, ColorType.BGRA_8888, ColorAlphaType.OPAQUE)
        bmp = Bitmap().apply { allocPixels(imageInfo) }
        info = imageInfo
        bitmap = bmp
      }
      bmp.installPixels(imageInfo, scratch, pitch)
      val image = SkiaImage.makeFromBitmap(bmp)
      onFrame(image.toComposeImageBitmap())

      twoAgo?.close()
      twoAgo = oneAgo
      oneAgo = image
    }

    override fun unlock(mediaPlayer: MediaPlayer) {}
  }

  fun start() {
    NativeDiscovery().discover()
    val f = MediaPlayerFactory()
    val p = f.mediaPlayers().newEmbeddedMediaPlayer()
    p.videoSurface().set(
      CallbackVideoSurface(
        bufferFormatCallback,
        renderCallback,
        true,
        VideoSurfaceAdapters.getVideoSurfaceAdapter(),
      )
    )
    p.controls().setRepeat(false) // don't loop — hold the last frame when playback ends
    factory = f
    player = p
    p.media().play(mediaPath) // autoplay
  }

  fun release() {
    val p = player
    val f = factory
    player = null
    factory = null
    // stop() blocks until native threads quiesce; do it off the UI thread. Once stopped there are
    // no more render callbacks, so it's safe to free the remaining native images and the bitmap.
    thread(isDaemon = true, name = "vlc-release") {
      runCatching {
        p?.controls()?.stop()
        p?.release()
        f?.release()
      }
      twoAgo?.close()
      twoAgo = null
      bitmap?.close()
      bitmap = null
    }
  }
}
