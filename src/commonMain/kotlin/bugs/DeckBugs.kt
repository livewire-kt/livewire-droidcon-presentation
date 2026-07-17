// CupPlugin + PresentationStateWrapper are CuP plugin API, same opt-in as the speaker plugin.
@file:OptIn(PluginCupAPI::class)

package bugs

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import net.kodein.cup.CupKeyEvent
import net.kodein.cup.LocalPresentationState
import net.kodein.cup.PluginCupAPI
import net.kodein.cup.PresentationStateWrapper
import net.kodein.cup.config.CupConfigurationBuilder
import net.kodein.cup.config.CupPlugin
import net.kodein.cup.currentSlide
import net.kodein.cup.key
import net.kodein.cup.type
import net.kodein.cup.utils.SlideContext
import net.kodein.cup.utils.SlideContextElement
import widgets.DeckControls

/**
 * Staged, switchable "bugs" for the live-debugging beats near the end of the talk.
 *
 * Each flag is snapshot state, so all three parties stay in sync automatically:
 * slides read a flag to (mis)behave, [StagedBugs] slide context arms flags as the
 * deck enters a demo slide (and disarms them on exit), and the Livewire Deck Doctor
 * plugin flips them live from the phone — the fix the audience watches happen.
 *
 * Panic button: pressing `K` in the presentation window clears everything.
 */
object DeckBugs {
  /** Beat 1: the "clicker is dead" — navigation keys are swallowed. Fix: advance from the phone. */
  var swallowKeys by mutableStateOf(false)

  fun clearAll() {
    swallowKeys = false
  }
}

/** Slide context that arms the given bugs while its slide is current, and disarms them on exit. */
class StagedBugs(
  val swallowKeys: Boolean = false,
) : SlideContextElement<StagedBugs>(Key) {
  companion object Key : SlideContext.Key<StagedBugs>
}

private val navigationKeys =
  setOf(
    Key.DirectionRight,
    Key.DirectionLeft,
    Key.DirectionUp,
    Key.DirectionDown,
    Key.Spacebar,
    Key.Enter,
    Key.Backspace,
    Key.Back,
    Key.NavigateNext,
    Key.NavigatePrevious,
    Key.PageDown,
    Key.PageUp,
  )

internal class DeckBugsPlugin : CupPlugin {

  @Composable
  override fun BoxScope.Content() {
    // The speaker window previews render with a wrapped, position-shifted state —
    // only the real presentation window gets to arm/disarm bugs.
    val state = LocalPresentationState.current
    if (state is PresentationStateWrapper) return

    val staged = state.currentSlide.context[StagedBugs]
    LaunchedEffect(staged) {
      DeckBugs.swallowKeys = staged?.swallowKeys == true
    }
  }

  override fun onKeyEvent(event: CupKeyEvent): Boolean {
    if (event.type != KeyEventType.KeyDown) return false
    if (event.key == Key.K) {
      // Panic: clear staged bugs AND restore the Deck Doctor's visual tunables.
      DeckBugs.clearAll()
      DeckControls.reset()
      return true
    }
    // The staged "dead clicker": eat navigation keys, let everything else through.
    return DeckBugs.swallowKeys && event.key in navigationKeys
  }
}

fun CupConfigurationBuilder.deckBugs() {
  plugin(DeckBugsPlugin())
}
