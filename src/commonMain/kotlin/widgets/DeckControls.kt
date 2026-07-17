// PresentationStateWrapper detection, same plugin-API opt-in as PaceMeter.
@file:OptIn(PluginCupAPI::class)

package widgets

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import net.kodein.cup.LocalPresentationState
import net.kodein.cup.PluginCupAPI
import net.kodein.cup.PresentationStateWrapper
import net.kodein.cup.speaker.isInSpeakerWindow

/**
 * Live-tunable deck state, mutated from the phone via the Deck Doctor Livewire plugin.
 *
 * Unlike [bugs.DeckBugs] (staged failures for the demo), every one of these is a real
 * control: plain snapshot state that deck widgets read, so a change from the wire
 * recomposes/redraws the presentation immediately.
 */
object DeckControls {
  /** Blanks the audience window (the "photon output limiter"). The speaker window stays lit. */
  var blackout by mutableStateOf(false)

  /** Brightness multiplier for the background dot lattice. 1 = the designed subtlety. */
  var gridGain by mutableStateOf(1f)

  /** Whether the audience window shows the pace battery HUD (the "telemetry beacon"). */
  var audienceHud by mutableStateOf(true)

  fun reset() {
    blackout = false
    gridGain = 1f
    audienceHud = true
  }
}

/**
 * Fade-to-black curtain over the audience window, driven by [DeckControls.blackout].
 * Draw it last in the presentation decoration so it covers slides and overlays alike.
 */
@Composable
fun BoxScope.BlackoutCurtain() {
  // Never blind the speaker: skip the speaker window and its shifted previews.
  if (LocalPresentationState.current is PresentationStateWrapper) return
  if (isInSpeakerWindow()) return

  val alpha by animateFloatAsState(
    targetValue = if (DeckControls.blackout) 1f else 0f,
    animationSpec = tween(durationMillis = 400),
  )
  if (alpha > 0f) {
    Box(Modifier.matchParentSize().background(Color.Black.copy(alpha = alpha)))
  }
}
