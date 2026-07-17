// PresentationStateWrapper lets us detect the speaker window's shifted "next slide"
// preview — the same thing CuP plugins do, hence the plugin API opt-in.
@file:OptIn(PluginCupAPI::class)

package widgets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource
import kotlinx.coroutines.delay
import livewire_presentation.generated.resources.Res
import livewire_presentation.generated.resources.pace_bolt
import net.kodein.cup.LocalPresentationState
import net.kodein.cup.PluginCupAPI
import net.kodein.cup.PresentationState
import net.kodein.cup.PresentationStateWrapper
import net.kodein.cup.utils.SlideContext
import net.kodein.cup.utils.SlideContextElement
import org.jetbrains.compose.resources.painterResource

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

  /**
   * Manual adjustment added to the monotonic clock — the Deck Doctor's "chronometer
   * skew", for when the clock was started late (or a Q&A ate the buffer).
   */
  var skew: Duration by mutableStateOf(Duration.ZERO)

  fun restart() {
    startMark = TimeSource.Monotonic.markNow()
    elapsed = Duration.ZERO
    skew = Duration.ZERO
  }

  fun clear() {
    startMark = null
    elapsed = Duration.ZERO
    skew = Duration.ZERO
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
fun rememberPaceState(
  talkDuration: Duration,
  state: PresentationState = LocalPresentationState.current,
): PaceState? {
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
      PaceClock.elapsed = (mark.elapsedNow() + PaceClock.skew).coerceAtLeast(Duration.ZERO)
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
      Modifier.align(Alignment.BottomEnd)
        .padding(end = 16.dp, bottom = 16.dp)
        .background(Livewire.Background.copy(alpha = 0.85f), RoundedCornerShape(10.dp))
        .border(1.dp, Livewire.Slate, RoundedCornerShape(10.dp))
        .clickable { PaceClock.restart() }
        .padding(horizontal = 14.dp, vertical = 10.dp),
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
        text = if (pace.started) pace.elapsed.asClock() else pace.talkDuration.asClock(),
        fontFamily = fonts.mono,
        fontSize = 32.sp,
        color = Livewire.Cream.copy(alpha = if (pace.started) 1f else 0.5f),
      )
      Spacer(Modifier.width(12.dp))
      Text(
        text =
          when {
            !pace.started -> "ready"
            pace.onPace -> "on pace"
            pace.delta.isPositive() -> "▲ ${pace.delta.asClock()}"
            else -> "▼ ${(-pace.delta).asClock()}"
          },
        fontFamily = fonts.mono,
        fontSize = 32.sp,
        color = paceColor,
      )
    }
    Spacer(Modifier.height(8.dp))
    // Timeline: amber fill = time spent, notches = keyframe targets,
    // cream tick = target clock time for the current deck position.
    Canvas(Modifier.width(300.dp).height(12.dp)) {
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
 * Discreet audience-facing pace status styled as a little battery in the bottom-right
 * corner, with the Livewire bolt over it. Charge = time budget: 3 of 5 bars in amber is
 * on pace, more bars in green means ahead, fewer in red means behind (delta clamped to
 * ±[fullScale]). Invisible until the talk clock starts.
 */
@Composable
fun BoxScope.PacePowerBar(
  talkDuration: Duration = 40.minutes,
  fullScale: Duration = 2.minutes,
) {
  if (LocalPresentationState.current is PresentationStateWrapper) return
  val pace = rememberPaceState(talkDuration) ?: return
  if (!pace.started) return

  // Map delta onto charge: 1 bar = badly behind, 3 = on pace, 5 = comfortably ahead.
  val fraction = (pace.delta / fullScale).toFloat().coerceIn(-1f, 1f)
  val segments = (3 + fraction * 2).roundToInt().coerceIn(1, 5)
  val fillColor =
    when {
      pace.onPace -> Livewire.Amber
      pace.delta.isPositive() -> Livewire.CodeString // ahead — green
      else -> Livewire.Red // behind
    }

  Box(
    modifier = Modifier.align(Alignment.BottomEnd).padding(end = 12.dp, bottom = 12.dp),
    contentAlignment = Alignment.Center,
  ) {
    // Vertical battery: nub on top, charge fills bottom-up.
    Canvas(Modifier.width(18.dp).height(32.dp)) {
      val stroke = 1.5.dp.toPx()
      val nubHeight = 2.5.dp.toPx()
      val bodyHeight = size.height - nubHeight
      val outline = Livewire.Gray.copy(alpha = 0.8f)
      drawRoundRect(
        color = outline,
        topLeft = Offset(stroke / 2, nubHeight + stroke / 2),
        size = Size(size.width - stroke, bodyHeight - stroke),
        cornerRadius = CornerRadius(4.dp.toPx()),
        style = Stroke(stroke),
      )
      drawRoundRect(
        color = outline,
        topLeft = Offset(size.width * 0.3f, 0f),
        size = Size(size.width * 0.4f, nubHeight - 0.5.dp.toPx()),
        cornerRadius = CornerRadius(1.dp.toPx()),
      )
      val inset = stroke + 2.dp.toPx()
      val gap = 1.5.dp.toPx()
      val slotHeight = (bodyHeight - inset * 2 - gap * 4) / 5
      repeat(5) { i ->
        // i = 0 is the BOTTOM segment; charge stacks upward.
        drawRoundRect(
          color =
            if (i < segments) fillColor.copy(alpha = 0.9f)
            else Livewire.Slate.copy(alpha = 0.4f),
          topLeft =
            Offset(inset, size.height - inset - (i + 1) * slotHeight - i * gap),
          size = Size(size.width - inset * 2, slotHeight),
          cornerRadius = CornerRadius(1.dp.toPx()),
        )
      }
    }
    Image(
      painter = painterResource(Res.drawable.pace_bolt),
      contentDescription = null,
      modifier = Modifier.height(16.dp).padding(top = 2.5.dp),
      contentScale = ContentScale.Fit,
    )
  }
}

internal fun Duration.asClock(): String {
  val totalSeconds = inWholeSeconds
  return "${totalSeconds / 60}:${(totalSeconds % 60).toString().padStart(2, '0')}"
}
