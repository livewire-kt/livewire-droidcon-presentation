@file:OptIn(PluginCupAPI::class)

package net.kodein.cup.speaker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import net.kodein.cup.LocalPresentationSize
import net.kodein.cup.LocalPresentationState
import net.kodein.cup.PluginCupAPI
import net.kodein.cup.PresentationKeyHandler
import net.kodein.cup.asComposeKeyHandler
import net.kodein.cup.laser.Laser

@Composable
internal fun SpeakerWindow(
  laser: Laser?,
  setLaser: (Laser?) -> Unit,
  onCloseRequest: () -> Unit,
) {
  val presentationState = LocalPresentationState.current

  remember(presentationState.currentPosition.slideIndex) {
    setLaser(null)
  }

  val presentationSize = LocalPresentationSize.current
  val ratio = presentationSize.width / presentationSize.height

  val updatedLaser by rememberUpdatedState(laser)

  CompositionLocalProvider(LocalPresentationState provides presentationState) {
    Window(
      state = rememberWindowState(width = 960.dp, height = 720.dp),
      title = SpeakerWindowTitle,
      onCloseRequest = onCloseRequest,
      onKeyEvent = keyHandler(
        laser = { updatedLaser },
        setLaser = setLaser,
      ),
    ) {
      Row(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.White),
      ) {
        var boxSize: IntSize? by remember { mutableStateOf(null) }
        SpeakerMainView(
          ratio = ratio,
          laser = laser,
          setLaser = setLaser,
          modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { boxSize = it },
        )
      }
    }
  }
}

@Composable
private fun keyHandler(
  laser: () -> Laser?,
  setLaser: (Laser?) -> Unit,
): (KeyEvent) -> Boolean {
  val state by rememberUpdatedState(LocalPresentationState.current)
  val fallback = PresentationKeyHandler { state }.asComposeKeyHandler()
  return handler@{ event ->
    if (event.type != KeyEventType.KeyDown) {
      return@handler fallback(event)
    }
    when (event.key) {
      Key.S -> {
        true
      }

      Key.P -> {
        when (laser()) {
          null -> {
            setLaser(Laser.Pointer())
          }

          is Laser.Pointer -> {
            setLaser(null)
          }

          else -> {}
        }
        true
      }

      Key.H -> {
        when (laser()) {
          null -> {
            setLaser(Laser.Highlight())
          }

          is Laser.Highlight -> {
            setLaser(null)
          }

          else -> {}
        }
        true
      }

      Key.Escape -> {
        if (laser() != null) {
          setLaser(null)
          true
        } else {
          fallback(event)
        }
      }

      else -> {
        fallback(event)
      }
    }
  }
}

const val SpeakerWindowTitle = "Speaker Notes"
