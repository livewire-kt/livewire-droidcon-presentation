// PresentationStateWrapper lets us detect the speaker window's shifted "next slide"
// preview — the same thing CuP plugins do, hence the plugin API opt-in.
@file:OptIn(PluginCupAPI::class)

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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource
import kotlinx.coroutines.delay
import net.kodein.cup.LocalPresentationState
import net.kodein.cup.PluginCupAPI
import net.kodein.cup.PresentationStateWrapper
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
 * The pace widgets interpolate linearly between consecutive keyframes (with implicit
 * anchors at the very start = 0:00 and the very end = the full talk duration), so
 * dense-but-fast sections and slow demo sections can budget different time per slide.
 */
class PaceKeyframe(val target: Duration) : SlideContextElement<PaceKeyframe>(Key) {
  companion object Key : SlideContext.Key<PaceKeyframe>
}

/**
 * The talk clock, shared by every window (main presentation + speaker window previews)
 * so they all agree on elapsed time and a restart from either side applies everywhere.
 */
object PaceClock {
  var startMark: TimeMark? by mutableStateOf(null)
    private set

  var elapsed: Duration by mutableStateOf(Duration.ZERO)

  fun restart() {
    startMark = TimeSource.Monotonic.markNow()
    elapsed = Duration.ZERO
  }

  fun clear() {
    startMark = null
    elapsed = Duration.ZERO
  }
}

data class PaceState(
  val started: Boolean,
  val elapsed: Duration,
  val expected: Duration,
  val talkDuration: Duration,
  val checkpoints: List<Pair<Int, Duration>>,
  val lastPosition: Int,
) {
  /** Positive = ahead of schedule, negative = behind. */
  val delta: Duration get() = expected - elapsed
  val onPace: Boolean get() = delta.absoluteValue <= 20.seconds
}

/**
 * Computes pacing against the [PaceKeyframe] plan and drives the shared [PaceClock]:
 * starts it on the first advance away from the opening position, resets it when
 * navigating back there. The effects are idempotent, so it is safe for several
 * windows to run this simultaneously.
 */
@Composable
fun rememberPaceState(talkDuration: Duration): PaceState? {
  val state = LocalPresentationState.current
  val slides = state.slides
  if (slides.isEmpty()) return null

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

  LaunchedEffect(position) {
    if (position == 0) {
      PaceClock.clear()
    } else if (PaceClock.startMark == null) {
      PaceClock.restart()
    }
  }
  LaunchedEffect(PaceClock.startMark) {
    val mark = PaceClock.startMark ?: return@LaunchedEffect
    while (true) {
      PaceClock.elapsed = mark.elapsedNow()
      delay(250)
    }
  }

  val expected =
    run {
      val next = checkpoints.firstOrNull { it.first >= position } ?: checkpoints.last()
      val prev = checkpoints.last { it.first <= position }
      when {
        next.first == prev.first -> prev.second
        else -> {
          val fraction = (position - prev.first).toDouble() / (next.first - prev.first)
          prev.second + (next.second - prev.second) * fraction
        }
      }
    }

  return PaceState(
    started = PaceClock.startMark != null,
    elapsed = PaceClock.elapsed,
    expected = expected,
    talkDuration = talkDuration,
    checkpoints = checkpoints,
    lastPosition = lastPosition,
  )
}

/**
 * Full pacing HUD — clock, ahead/behind delta, and a keyframed timeline. Meant for the
 * SPEAKER WINDOW; show [PacePowerBar] to the audience instead. Clicking restarts the
 * shared clock.
 */
@Composable
fun BoxScope.PaceMeter(talkDuration: Duration = 40.minutes) {
  // The speaker window's "next slide" preview renders the presentation with a wrapped,
  // position-shifted state — don't show a (mispositioned, duplicate) meter there.
  if (LocalPresentationState.current is PresentationStateWrapper) return
  val pace = rememberPaceState(talkDuration) ?: return

  val paceColor =
    when {
      !pace.started || pace.onPace -> Livewire.Gray
      pace.delta.isPositive() -> Livewire.CodeString // ahead — green
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
        .clickable { PaceClock.restart() }
        .padding(horizontal = 10.dp, vertical = 6.dp),
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
        text = if (pace.started) pace.elapsed.asClock() else pace.talkDuration.asClock(),
        fontFamily = fonts.mono,
        fontSize = 11.sp,
        color = Livewire.Cream.copy(alpha = if (pace.started) 1f else 0.5f),
      )
      Spacer(Modifier.width(8.dp))
      Text(
        text =
          when {
            !pace.started -> "ready"
            pace.onPace -> "on pace"
            pace.delta.isPositive() -> "▲ ${pace.delta.asClock()}"
            else -> "▼ ${(-pace.delta).asClock()}"
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
      if (pace.started) {
        val timeFraction = (pace.elapsed / pace.talkDuration).toFloat().coerceIn(0f, 1f)
        drawRoundRect(
          color = Livewire.Amber,
          size = size.copy(width = size.width * timeFraction),
          cornerRadius = r,
        )
      }
      pace.checkpoints.forEach { (pos, target) ->
        if (pos == 0 || pos >= pace.lastPosition) return@forEach
        val nx = size.width * (target / pace.talkDuration).toFloat().coerceIn(0f, 1f)
        drawLine(
          color = Livewire.Gray,
          start = Offset(nx, 0f),
          end = Offset(nx, size.height),
          strokeWidth = 1.dp.toPx(),
        )
      }
      val x = size.width * (pace.expected / pace.talkDuration).toFloat().coerceIn(0f, 1f)
      drawLine(
        color = Livewire.Cream,
        start = Offset(x, -2.dp.toPx()),
        end = Offset(x, size.height + 2.dp.toPx()),
        strokeWidth = 1.5.dp.toPx(),
      )
    }
  }
}

/**
 * Discreet audience-facing pace status: a small center-anchored bar in the bottom-right
 * corner. Ahead → green grows right, behind → red grows left (magnitude clamped to
 * ±[fullScale]), on pace → a faint gray dot. Invisible until the talk clock starts.
 */
@Composable
fun BoxScope.PacePowerBar(
  talkDuration: Duration = 40.minutes,
  fullScale: Duration = 2.minutes,
) {
  if (LocalPresentationState.current is PresentationStateWrapper) return
  val pace = rememberPaceState(talkDuration) ?: return
  if (!pace.started) return

  Canvas(
    Modifier.align(Alignment.BottomEnd)
      .padding(end = 12.dp, bottom = 12.dp)
      .width(44.dp)
      .height(4.dp)
  ) {
    val r = CornerRadius(size.height / 2)
    val center = size.width / 2
    drawRoundRect(color = Livewire.Slate.copy(alpha = 0.55f), cornerRadius = r)
    when {
      pace.onPace ->
        drawCircle(
          color = Livewire.Gray.copy(alpha = 0.7f),
          radius = size.height * 0.75f,
          center = Offset(center, size.height / 2),
        )
      else -> {
        val fraction = (pace.delta / fullScale).toFloat().coerceIn(-1f, 1f)
        val ahead = fraction > 0f
        val extent = center * kotlin.math.abs(fraction)
        drawRoundRect(
          color = (if (ahead) Livewire.CodeString else Livewire.Red).copy(alpha = 0.8f),
          topLeft = Offset(if (ahead) center else center - extent, 0f),
          size = size.copy(width = extent),
          cornerRadius = r,
        )
      }
    }
    // center notch
    drawLine(
      color = Livewire.Cream.copy(alpha = 0.5f),
      start = Offset(center, -1.dp.toPx()),
      end = Offset(center, size.height + 1.dp.toPx()),
      strokeWidth = 1.dp.toPx(),
    )
  }
}

private fun Duration.asClock(): String {
  val totalSeconds = inWholeSeconds
  return "${totalSeconds / 60}:${(totalSeconds % 60).toString().padStart(2, '0')}"
}
