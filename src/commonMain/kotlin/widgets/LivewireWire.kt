package widgets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import dev.piotrprus.particleemitter.CanvasEmitterConfig
import dev.piotrprus.particleemitter.CanvasParticleEmitter
import dev.piotrprus.particleemitter.ParticleShape
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * The talk's namesake, literally: an amber cable with zig-zag discharge clusters that
 * sizzles along its whole length, while a red-hot charge travels the path trailing
 * sparks. Particles via PiotrPrus/ParticleEmitter's canvas emitter — the sizzle is one
 * emitter whose center hops to a random point on the path every frame, the charge is a
 * second emitter whose center rides the path animation.
 */
@Composable
fun LivewireWire(
  modifier: Modifier = Modifier,
  amplitude: Dp = 14.dp,
) {
  BoxWithConstraints(modifier = modifier) {
    val width = maxWidth
    val midY = maxHeight / 2

    val points = remember(width, midY, amplitude) {
      WIRE_PATTERN.map { (fx, fy) -> DpOffset(width * fx, midY + amplitude * fy) }
    }
    val segmentLengths = remember(points) {
      points.zipWithNext().map { (a, b) ->
        val dx = (b.x - a.x).value
        val dy = (b.y - a.y).value
        sqrt(dx * dx + dy * dy)
      }
    }
    val totalLength = remember(segmentLengths) { segmentLengths.sum() }

    // Frame-time integrator rather than an infinite transition, so the Deck Doctor's
    // "carrier drift velocity" can change speed mid-flight without restarting the loop.
    var chargeFraction by remember { mutableStateOf(0f) }
    var frameTick by remember { mutableStateOf(0L) }
    LaunchedEffect(Unit) {
      var lastNanos = 0L
      while (true) {
        withFrameNanos { now ->
          if (lastNanos != 0L) {
            val dt = ((now - lastNanos) / 1e9f).coerceAtMost(0.1f)
            val advance = dt * DeckControls.wireDriftVelocity / BASE_TRAVEL_SECONDS
            chargeFraction = (chargeFraction + advance).mod(1f)
          }
          lastNanos = now
          frameTick = now
        }
      }
    }
    val chargePos = pointAt(points, segmentLengths, totalLength, chargeFraction)
    // frameTick changes every frame, so this picks a fresh sizzle origin per frame
    // (even when the drift velocity is zeroed and the charge stands still).
    val sizzlePos =
      remember(frameTick) { pointAt(points, segmentLengths, totalLength, Random.nextFloat()) }

    // The cable: wide soft glow under a thin bright core.
    Canvas(Modifier.matchParentSize()) {
      val path = Path().apply {
        points.forEachIndexed { i, p ->
          if (i == 0) moveTo(p.x.toPx(), p.y.toPx()) else lineTo(p.x.toPx(), p.y.toPx())
        }
      }
      drawPath(
        path = path,
        color = Livewire.Amber.copy(alpha = 0.22f),
        style = Stroke(6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
      )
      drawPath(
        path = path,
        color = Livewire.Amber,
        style = Stroke(1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
      )
    }

    // Whole-wire sizzle: tiny short-lived fizz all along the cable.
    CanvasParticleEmitter(
      modifier = Modifier.matchParentSize(),
      config = CanvasEmitterConfig(
        particlePerSecond = DeckControls.wireSizzleRate.roundToInt(),
        emitterCenter = sizzlePos,
        startRegionShape = CanvasEmitterConfig.Shape.POINT,
        startRegionSize = DpSize.Zero,
        particleShapes = listOf(ParticleShape.Circle),
        lifespanRange = 150..450,
        fadeOutTime = 120..300,
        scaleTime = 200..450,
        colors = listOf(Livewire.Amber, Livewire.Amber, Livewire.Cream, Livewire.CodeNumber),
        particleSizes =
          listOf(
            DpSize(1.5.dp, 1.5.dp),
            DpSize(2.dp, 2.dp),
            DpSize(2.5.dp, 2.5.dp),
          ),
        spread = -180..180,
        initialForce = scaledForce(8, 45, DeckControls.wireArcEnergy),
        startScaleRange = 1..1,
        targetScaleRange = 0..0,
      ),
    )

    // The traveling charge's spark shower: hotter, bigger, with a little gravity.
    CanvasParticleEmitter(
      modifier = Modifier.matchParentSize(),
      config = CanvasEmitterConfig(
        particlePerSecond = 80,
        emitterCenter = chargePos,
        startRegionShape = CanvasEmitterConfig.Shape.POINT,
        startRegionSize = DpSize.Zero,
        particleShapes = listOf(ParticleShape.Circle),
        lifespanRange = 250..650,
        fadeOutTime = 200..450,
        scaleTime = 300..650,
        colors = listOf(Livewire.Red, Livewire.Red, Livewire.Amber, Livewire.Cream),
        particleSizes =
          listOf(
            DpSize(2.dp, 2.dp),
            DpSize(2.5.dp, 2.5.dp),
            DpSize(3.dp, 3.dp),
          ),
        spread = -180..180,
        initialForce = scaledForce(25, 80, DeckControls.wireArcEnergy),
        gravityStrength = DeckControls.wireSparkGravity,
        gravityAngle = 0,
        startScaleRange = 1..1,
        targetScaleRange = 0..0,
      ),
    )

    // The charge itself: red-hot core over layered glow, above its own sparks.
    Canvas(Modifier.matchParentSize()) {
      val center = Offset(chargePos.x.toPx(), chargePos.y.toPx())
      drawCircle(Livewire.Red.copy(alpha = 0.20f), 9.dp.toPx(), center)
      drawCircle(Livewire.Red.copy(alpha = 0.45f), 5.5.dp.toPx(), center)
      drawCircle(Livewire.Red, 3.dp.toPx(), center)
      drawCircle(Livewire.Cream, 1.2.dp.toPx(), center)
    }
  }
}

/** Seconds for the charge to travel the full path at drift velocity ×1. */
private const val BASE_TRAVEL_SECONDS = 5.5f

/** Scales a force range by an energy multiplier, keeping it a valid IntRange. */
private fun scaledForce(min: Int, max: Int, energy: Float): IntRange {
  val lo = (min * energy).roundToInt()
  val hi = (max * energy).roundToInt().coerceAtLeast(lo + 1)
  return lo..hi
}

private fun pointAt(
  points: List<DpOffset>,
  segmentLengths: List<Float>,
  totalLength: Float,
  fraction: Float,
): DpOffset {
  var remaining = fraction.coerceIn(0f, 1f) * totalLength
  segmentLengths.forEachIndexed { i, length ->
    if (remaining <= length || i == segmentLengths.lastIndex) {
      val t = if (length == 0f) 0f else (remaining / length).coerceIn(0f, 1f)
      val a = points[i]
      val b = points[i + 1]
      return DpOffset(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t)
    }
    remaining -= length
  }
  return points.last()
}

/**
 * Zig-zag skeleton as (x fraction, y in amplitude units): a mostly-straight cable with
 * four lightning-style discharge clusters.
 */
private val WIRE_PATTERN: List<Pair<Float, Float>> = listOf(
  0.00f to 0f,
  0.07f to 0f,
  0.10f to -0.9f,
  0.13f to 0.85f,
  0.155f to -0.4f,
  0.175f to 0f,
  0.30f to 0f,
  0.33f to 0.9f,
  0.36f to -0.85f,
  0.385f to 0.45f,
  0.405f to 0f,
  0.53f to 0f,
  0.56f to -1f,
  0.59f to 0.75f,
  0.615f to -0.5f,
  0.635f to 0f,
  0.76f to 0f,
  0.79f to 0.85f,
  0.82f to -0.9f,
  0.845f to 0.35f,
  0.865f to 0f,
  1.00f to 0f,
)
