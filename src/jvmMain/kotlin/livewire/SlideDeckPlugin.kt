package livewire

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.livewire.ui.Plugin
import com.livewire.ui.PluginInfo
import com.livewire.ui.actions.clickAction
import com.livewire.ui.composition.LivewireComposable
import com.livewire.ui.graphics.RoundedCornerShape
import com.livewire.ui.layout.Alignment
import com.livewire.ui.layout.Arrangement
import com.livewire.ui.layout.Column
import com.livewire.ui.layout.ColumnScope
import com.livewire.ui.layout.Row
import com.livewire.ui.modifier.LivewireModifier
import com.livewire.ui.modifier.background
import com.livewire.ui.modifier.clickable
import com.livewire.ui.modifier.clip
import com.livewire.ui.modifier.fillMaxSize
import com.livewire.ui.modifier.fillMaxWidth
import com.livewire.ui.modifier.height
import com.livewire.ui.modifier.padding
import com.livewire.ui.modifier.thenIf
import com.livewire.ui.modifier.verticalScroll
import com.livewire.ui.modifier.width
import com.livewire.ui.text.LivewireFontFamily
import com.livewire.ui.theme.LivewireTheme
import com.livewire.ui.widget.Button
import com.livewire.ui.widget.ButtonSize
import com.livewire.ui.widget.ButtonStyle
import com.livewire.ui.widget.Chip
import com.livewire.ui.widget.Icon
import com.livewire.ui.widget.IconButton
import com.livewire.ui.widget.IconButtonStyle
import com.livewire.ui.widget.ProgressIndicator
import com.livewire.ui.widget.Spacer
import com.livewire.ui.widget.Surface
import com.livewire.ui.widget.Text
import kotlin.time.Duration
import livewire.icons.CoPresent
import livewire.icons.Icons
import livewire.icons.NavigateBefore
import livewire.icons.NavigateNext
import livewire.icons.SkipNext
import livewire.icons.SkipPrevious
import net.kodein.cup.PresentationState
import net.kodein.cup.currentSlide
import net.kodein.cup.goTo
import net.kodein.cup.goToNextSlide
import net.kodein.cup.goToNextStep
import net.kodein.cup.goToPreviousSlide
import net.kodein.cup.goToPreviousStep
import net.kodein.cup.speaker.SpeakerNotes
import net.kodein.cup.totalStepCurrent
import net.kodein.cup.totalStepLast
import widgets.PaceClock
import widgets.asClock
import widgets.rememberPaceState

/**
 * A Livewire plugin that turns any device running the Livewire host into a remote
 * control for a CuP presentation.
 *
 * CuP's [PresentationState] is backed by snapshot state, so reading it here — inside
 * Livewire's own composition — recomposes the plugin UI (and re-streams it to the host)
 * whenever the deck moves, and [PresentationState.goTo] works from any thread the
 * action observer delivers on. Nothing CuP-specific crosses the wire: the host only
 * ever sees Livewire widgets.
 *
 * @param presentation the live state of the running presentation, grabbed from
 *   [net.kodein.cup.LocalPresentationState] wherever the [com.livewire.client.LivewireClient]
 *   is created.
 * @param talkDuration when set, shows a talk clock paced against the deck's
 *   [widgets.PaceKeyframe] plan.
 */
class SlideDeckPlugin(
  private val presentation: PresentationState,
  private val talkDuration: Duration? = null,
) : Plugin {

  override val info: PluginInfo = PluginInfo(
    pluginId = "com.livewire.slide-deck",
    title = "Slide Deck",
    icon = Icons.CoPresent,
  )

  @LivewireComposable
  @Composable
  override fun Content() {
    // The client may compose before the Presentation connects its slides.
    if (presentation.slides.isEmpty()) {
      Text("Waiting for the presentation to start…")
      return
    }

    Column(
      modifier = LivewireModifier.fillMaxSize().verticalScroll().padding(16.dp),
      verticalArrangement = Arrangement.SpacedBy(12.dp),
    ) {
      NowPresenting()
      Transport()
      talkDuration?.let { TalkClock(it) }
      SpeakerNotes()
      SlideList()
    }
  }

  @LivewireComposable
  @Composable
  private fun NowPresenting() {
    val slide = presentation.currentSlide
    val position = presentation.currentPosition
    Card(title = "NOW PRESENTING") {
      Text(slide.name.toDisplayTitle(), style = LivewireTheme.typography.titleLarge)
      Spacer(LivewireModifier.height(4.dp))
      Text(
        text = buildString {
          append("Slide ${position.slideIndex + 1} of ${presentation.slides.size}")
          if (slide.stepCount > 1) append(" · Step ${position.step + 1} of ${slide.stepCount}")
        },
        style = LivewireTheme.typography.bodySmall,
        color = LivewireTheme.colorScheme.onSurfaceVariant,
      )
      Spacer(LivewireModifier.height(10.dp))
      ProgressIndicator(
        modifier = LivewireModifier.fillMaxWidth(),
        progress =
          presentation.totalStepCurrent.toFloat() / presentation.totalStepLast.coerceAtLeast(1),
      )
    }
  }

  @LivewireComposable
  @Composable
  private fun Transport() {
    Row(
      modifier = LivewireModifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      IconButton(
        action = clickAction("prev-slide") { presentation.goToPreviousSlide() },
        size = ButtonSize.Medium,
        style = IconButtonStyle.Tonal,
      ) {
        Icon(Icons.SkipPrevious)
      }
      Button(
        action = clickAction("prev-step") { presentation.goToPreviousStep() },
        modifier = LivewireModifier.weight(1f),
        size = ButtonSize.Medium,
        style = ButtonStyle.Filled,
      ) {
        Icon(Icons.NavigateBefore)
      }
      Button(
        action = clickAction("next-step") { presentation.goToNextStep() },
        modifier = LivewireModifier.weight(1f),
        size = ButtonSize.Medium,
        style = ButtonStyle.Filled,
      ) {
        Icon(Icons.NavigateNext)
      }
      IconButton(
        action = clickAction("next-slide") { presentation.goToNextSlide() },
        size = ButtonSize.Medium,
        style = IconButtonStyle.Tonal,
      ) {
        Icon(Icons.SkipNext)
      }
    }
  }

  @LivewireComposable
  @Composable
  private fun TalkClock(talkDuration: Duration) {
    val pace = rememberPaceState(talkDuration, presentation) ?: return
    val colors = LivewireTheme.colorScheme
    Card(title = "TALK CLOCK") {
      Row(
        modifier = LivewireModifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = if (pace.started) pace.elapsed.asClock() else pace.talkDuration.asClock(),
          style =
            LivewireTheme.typography.headlineMedium.copy(
              fontFamily = LivewireFontFamily.Monospace
            ),
          color = if (pace.started) colors.onSurface else colors.onSurfaceVariant,
        )
        Text(
          text =
            when {
              !pace.started -> "ready"
              pace.onPace -> "on pace"
              pace.delta.isPositive() -> "▲ ${pace.delta.asClock()} ahead"
              else -> "▼ ${(-pace.delta).asClock()} behind"
            },
          style = LivewireTheme.typography.titleMedium,
          color =
            when {
              !pace.started || pace.onPace -> colors.onSurfaceVariant
              pace.delta.isPositive() -> colors.primary
              else -> colors.error
            },
        )
      }
      Spacer(LivewireModifier.height(10.dp))
      ProgressIndicator(
        modifier = LivewireModifier.fillMaxWidth(),
        progress =
          if (pace.started) (pace.elapsed / pace.talkDuration).toFloat().coerceIn(0f, 1f) else 0f,
      )
      Spacer(LivewireModifier.height(10.dp))
      Chip(
        label = "Restart clock",
        action = clickAction("restart-clock") { PaceClock.restart() },
      )
    }
  }

  @LivewireComposable
  @Composable
  private fun SpeakerNotes() {
    val slide = presentation.currentSlide
    val step = presentation.currentPosition.step
    val notes =
      slide.context[SpeakerNotes]
        ?.notes
        ?.filter { (steps, _) -> step in steps }
        ?.joinToString("\n\n") { (_, note) -> note.first }
    if (notes.isNullOrBlank()) return
    Card(title = "SPEAKER NOTES") {
      Text(notes, style = LivewireTheme.typography.bodyMedium)
    }
  }

  @LivewireComposable
  @Composable
  private fun SlideList() {
    val colors = LivewireTheme.colorScheme
    val currentIndex = presentation.currentPosition.slideIndex
    Card(title = "JUMP TO") {
      presentation.slides.forEachIndexed { index, slide ->
        val isCurrent = index == currentIndex
        Row(
          modifier =
            LivewireModifier.fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .thenIf(isCurrent) { background(colors.primaryContainer) }
              .clickable(clickAction("go-to-$index") { presentation.goTo(index) })
              .padding(horizontal = 10.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = (index + 1).toString().padStart(2, '0'),
            style =
              LivewireTheme.typography.labelMedium.copy(
                fontFamily = LivewireFontFamily.Monospace
              ),
            color = if (isCurrent) colors.onPrimaryContainer else colors.onSurfaceVariant,
          )
          Spacer(LivewireModifier.width(10.dp))
          Text(
            text = slide.name.toDisplayTitle(),
            modifier = LivewireModifier.weight(1f),
            style = LivewireTheme.typography.bodyMedium,
            color = if (isCurrent) colors.onPrimaryContainer else colors.onSurface,
          )
          if (slide.stepCount > 1) {
            Text(
              text = "${slide.stepCount} steps",
              style = LivewireTheme.typography.labelSmall,
              color = if (isCurrent) colors.onPrimaryContainer else colors.onSurfaceVariant,
            )
          }
        }
      }
    }
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

/** Turns a slide's property name into a readable title: "updatingTheTree" → "Updating The Tree". */
private fun String.toDisplayTitle(): String =
  replace(Regex("(?<=[a-z0-9])(?=[A-Z])|(?<=[A-Za-z])(?=[0-9])"), " ")
    .replaceFirstChar { it.uppercaseChar() }
