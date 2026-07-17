package livewire

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bugs.DeckBugs
import com.livewire.ui.Plugin
import com.livewire.ui.PluginInfo
import com.livewire.ui.actions.checkedChangeAction
import com.livewire.ui.actions.clickAction
import com.livewire.ui.actions.floatValueChangeAction
import com.livewire.ui.composition.LivewireComposable
import com.livewire.ui.graphics.RoundedCornerShape
import com.livewire.ui.layout.Alignment
import com.livewire.ui.layout.Arrangement
import com.livewire.ui.layout.Column
import com.livewire.ui.layout.ColumnScope
import com.livewire.ui.layout.Row
import com.livewire.ui.modifier.LivewireModifier
import com.livewire.ui.modifier.fillMaxSize
import com.livewire.ui.modifier.fillMaxWidth
import com.livewire.ui.modifier.height
import com.livewire.ui.modifier.padding
import com.livewire.ui.modifier.verticalScroll
import com.livewire.ui.modifier.width
import com.livewire.ui.theme.LivewireTheme
import com.livewire.ui.widget.Chip
import com.livewire.ui.widget.Slider
import com.livewire.ui.widget.Spacer
import com.livewire.ui.widget.Surface
import com.livewire.ui.widget.Switch
import com.livewire.ui.widget.Text
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import livewire.icons.BugReport
import livewire.icons.Icons
import widgets.DeckControls
import widgets.PaceClock

/**
 * Live controls for the running deck, labeled like a settings screen nobody should
 * touch. The jargon is the disguise — every control is real:
 *
 * - "HID event coalescing" → [DeckBugs.swallowKeys], the staged dead-clicker bug
 * - "Photon output limiter" → [DeckControls.blackout], blanks the audience window
 * - "Telemetry beacon" → [DeckControls.audienceHud], the pace battery HUD
 * - "Substrate lattice gain" → [DeckControls.gridGain], background dot brightness
 * - "Chronometer skew" → [PaceClock.skew], nudges the talk clock ±1 min
 *
 * Everything is bound to snapshot state the deck reads, so a flip on the phone
 * changes the projector immediately: control → action over the wire → snapshot
 * state → recomposition/redraw. "Restore defaults" (or K on the laptop) resets it all.
 */
class DeckDoctorPlugin : Plugin {

  override val info: PluginInfo = PluginInfo(
    pluginId = "com.livewire.deck-doctor",
    title = "Deck Doctor",
    icon = Icons.BugReport,
  )

  @LivewireComposable
  @Composable
  override fun Content() {
    Column(
      modifier = LivewireModifier.fillMaxSize().verticalScroll().padding(16.dp),
      verticalArrangement = Arrangement.SpacedBy(12.dp),
    ) {
      Card(title = "RUNTIME TUNABLES") {
        Text(
          text = "Advanced knobs. Do not touch during a live talk.",
          style = LivewireTheme.typography.bodySmall,
          color = LivewireTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(LivewireModifier.height(10.dp))
        TunableSwitch(
          key = "tune-hid-coalescing",
          label = "HID event coalescing",
          description = "Aggressively debounce presenter-clicker interrupts",
          checked = DeckBugs.swallowKeys,
        ) {
          DeckBugs.swallowKeys = it
        }
        TunableSwitch(
          key = "tune-photon-limiter",
          label = "Photon output limiter",
          description = "Cut emissions on the primary display surface",
          checked = DeckControls.blackout,
        ) {
          DeckControls.blackout = it
        }
        TunableSwitch(
          key = "tune-telemetry-beacon",
          label = "Telemetry beacon",
          description = "Broadcast pace vitals to the audience surface",
          checked = DeckControls.audienceHud,
        ) {
          DeckControls.audienceHud = it
        }
        Spacer(LivewireModifier.height(10.dp))
        Text(
          text = "Substrate lattice gain — ×${(DeckControls.gridGain * 10).roundToInt() / 10f}",
          style = LivewireTheme.typography.bodyLarge,
        )
        Text(
          text = "Excitation bias for the background dot matrix",
          style = LivewireTheme.typography.bodySmall,
          color = LivewireTheme.colorScheme.onSurfaceVariant,
        )
        Slider(
          value = DeckControls.gridGain,
          onValueChange = floatValueChangeAction { DeckControls.gridGain = it },
          modifier = LivewireModifier.fillMaxWidth(),
          valueRangeStart = 0f,
          valueRangeEnd = 4f,
        )
        Spacer(LivewireModifier.height(10.dp))
        Text(
          text = "Chronometer skew — ${PaceClock.skew.asSignedClock()}",
          style = LivewireTheme.typography.bodyLarge,
        )
        Text(
          text = "Compensate for wetware-induced schedule drift",
          style = LivewireTheme.typography.bodySmall,
          color = LivewireTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(LivewireModifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.SpacedBy(8.dp)) {
          Chip(
            label = "−1 min",
            action = clickAction("skew-minus") { PaceClock.skew -= 1.minutes },
          )
          Chip(
            label = "+1 min",
            action = clickAction("skew-plus") { PaceClock.skew += 1.minutes },
          )
        }
        Spacer(LivewireModifier.height(12.dp))
        Chip(
          label = "Restore defaults",
          action = clickAction("restore-defaults") {
            DeckBugs.clearAll()
            DeckControls.reset()
            PaceClock.skew = Duration.ZERO
          },
        )
      }
    }
  }

  @LivewireComposable
  @Composable
  private fun TunableSwitch(
    key: String,
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
  ) {
    Row(
      modifier = LivewireModifier.fillMaxWidth().padding(vertical = 3.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Column(modifier = LivewireModifier.weight(1f)) {
        Text(label, style = LivewireTheme.typography.bodyLarge)
        Text(
          text = description,
          style = LivewireTheme.typography.bodySmall,
          color = LivewireTheme.colorScheme.onSurfaceVariant,
        )
      }
      Spacer(LivewireModifier.width(12.dp))
      Switch(
        checked = checked,
        onCheckedChange = checkedChangeAction(key, onCheckedChange),
      )
    }
  }

  private fun Duration.asSignedClock(): String {
    val total = inWholeSeconds
    val sign = if (total < 0) "−" else "+"
    val abs = total.absoluteValue
    return "$sign${abs / 60}:${(abs % 60).toString().padStart(2, '0')}"
  }

  @LivewireComposable
  @Composable
  private fun Card(
    title: String,
    content: @Composable @LivewireComposable ColumnScope.() -> Unit,
  ) {
    Surface(
      modifier = LivewireModifier.fillMaxWidth(),
      shape = RoundedCornerShape(12.dp),
      color = LivewireTheme.colorScheme.surfaceVariant,
    ) {
      Column(modifier = LivewireModifier.fillMaxWidth().padding(14.dp)) {
        Text(
          text = title,
          style = LivewireTheme.typography.labelMedium.copy(letterSpacing = 1.sp),
          color = LivewireTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(LivewireModifier.height(8.dp))
        content()
      }
    }
  }
}
