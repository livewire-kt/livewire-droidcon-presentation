package widgets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource
import kotlinx.coroutines.delay
import net.kodein.cup.LocalPresentationState
import net.kodein.cup.utils.SlideContext
import net.kodein.cup.utils.SlideContextElement

/**
 * Pacing checkpoint attached to a slide's context: "by the time this slide STARTS,
 * the clock should read no more than [target]".
 *
 * ```
 * val sectionIdea by Slide(context = SpeakerNotes("…") + PaceKeyframe(6.minutes)) { … }
 * ```
 *
 * [PaceMeter] interpolates linearly between consecutive keyframes (with implicit
 * anchors at the very start = 0:00 and the very end = the full talk duration), so
 * dense-but-fast sections and slow demo sections can budget different time per slide.
 */
class PaceKeyframe(val target: Duration) : SlideContextElement<PaceKeyframe>(Key) {
  companion object Key : SlideContext.Key<PaceKeyframe>
}

/**
 * Pacing HUD shown on every slide (rendered in the Presentation decoration, outside
 * slide scaling).
 *
 * The clock starts on the first advance away from the very first position and resets
 * if you navigate back to it; clicking the chip restarts the clock. Deck progress is
 * measured in steps (every advance counts) and mapped to a target clock time through
 * the [PaceKeyframe] checkpoints: green = ahead, red = behind.
 */
@Composable
fun BoxScope.PaceMeter(talkDuration: Duration = 40.minutes) {
  val state = LocalPresentationState.current
  val slides = state.slides
  if (slides.isEmpty()) return

  val stepStarts =
    remember(slides) {
      var acc = 0
      IntArray(slides.size) { i -> acc.also { acc += slides[i].stepCount } }
    }
  val lastPosition = remember(slides) { (slides.sumOf { it.stepCount } - 1).coerceAtLeast(1) }
  val position = stepStarts[state.currentPosition.slideIndex] + state.currentPosition.step

  // Checkpoints: implicit start anchor, every PaceKeyframe slide, implicit end anchor.
  val checkpoints =
    remember(slides, talkDuration) {
      buildList {
        add(0 to Duration.ZERO)
        slides.forEachIndexed { i, slide ->
          slide.context[PaceKeyframe]?.let { add(stepStarts[i] to it.target) }
        }
        if (none { it.first >= lastPosition }) add(lastPosition to talkDuration)
      }
        .sortedBy { it.first }
    }

  fun expectedAt(pos: Int): Duration {
    val next = checkpoints.firstOrNull { it.first >= pos } ?: return checkpoints.last().second
    val prev = checkpoints.last { it.first <= pos }
    if (next.first == prev.first) return prev.second
    val fraction = (pos - prev.first).toDouble() / (next.first - prev.first)
    return prev.second + (next.second - prev.second) * fraction
  }

  var startMark: TimeMark? by remember { mutableStateOf(null) }
  var elapsed by remember { mutableStateOf(Duration.ZERO) }

  LaunchedEffect(position) {
    if (position == 0) {
      startMark = null
      elapsed = Duration.ZERO
    } else if (startMark == null) {
      startMark = TimeSource.Monotonic.markNow()
    }
  }
  LaunchedEffect(startMark) {
    val mark = startMark ?: return@LaunchedEffect
    while (true) {
      elapsed = mark.elapsedNow()
      delay(250)
    }
  }

  val started = startMark != null
  val expected = expectedAt(position)
  val delta = expected - elapsed // positive = ahead of schedule
  val paceColor =
    when {
      !started -> Livewire.Gray
      delta.absoluteValue <= 20.seconds -> Livewire.Gray
      delta.isPositive() -> Livewire.CodeString // ahead — green
      else -> Livewire.Red // behind
    }

  val fonts = LocalLivewireFonts.current
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier =
      Modifier.align(Alignment.BottomCenter)
        .padding(bottom = 10.dp)
        .background(Livewire.Background.copy(alpha = 0.85f), RoundedCornerShape(8.dp))
        .border(1.dp, Livewire.Slate, RoundedCornerShape(8.dp))
        .clickable {
          startMark = TimeSource.Monotonic.markNow()
          elapsed = Duration.ZERO
        }
        .padding(horizontal = 10.dp, vertical = 6.dp),
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
        text = if (started) elapsed.asClock() else talkDuration.asClock(),
        fontFamily = fonts.mono,
        fontSize = 11.sp,
        color = Livewire.Cream.copy(alpha = if (started) 1f else 0.5f),
      )
      Spacer(Modifier.width(8.dp))
      Text(
        text =
          when {
            !started -> "ready"
            delta.absoluteValue <= 20.seconds -> "on pace"
            delta.isPositive() -> "▲ ${delta.asClock()}"
            else -> "▼ ${(-delta).asClock()}"
          },
        fontFamily = fonts.mono,
        fontSize = 11.sp,
        color = paceColor,
      )
    }
    Spacer(Modifier.height(4.dp))
    // Timeline: amber fill = time spent, notches = keyframe targets,
    // cream tick = target clock time for the current deck position.
    Canvas(Modifier.width(140.dp).height(3.dp)) {
      val r = CornerRadius(size.height / 2)
      drawRoundRect(color = Livewire.Slate, cornerRadius = r)
      if (started) {
        val timeFraction = (elapsed / talkDuration).toFloat().coerceIn(0f, 1f)
        drawRoundRect(
          color = Livewire.Amber,
          size = size.copy(width = size.width * timeFraction),
          cornerRadius = r,
        )
      }
      checkpoints.forEach { (pos, target) ->
        if (pos == 0 || pos >= lastPosition) return@forEach
        val nx = size.width * (target / talkDuration).toFloat().coerceIn(0f, 1f)
        drawLine(
          color = Livewire.Gray,
          start = Offset(nx, 0f),
          end = Offset(nx, size.height),
          strokeWidth = 1.dp.toPx(),
        )
      }
      val x = size.width * (expected / talkDuration).toFloat().coerceIn(0f, 1f)
      drawLine(
        color = Livewire.Cream,
        start = Offset(x, -2.dp.toPx()),
        end = Offset(x, size.height + 2.dp.toPx()),
        strokeWidth = 1.5.dp.toPx(),
      )
    }
  }
}

private fun Duration.asClock(): String {
  val totalSeconds = inWholeSeconds
  return "${totalSeconds / 60}:${(totalSeconds % 60).toString().padStart(2, '0')}"
}
