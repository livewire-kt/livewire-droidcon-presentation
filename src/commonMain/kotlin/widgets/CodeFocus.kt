package widgets

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.DrawScope
import net.kodein.cup.sa.SAStyle
import net.kodein.cup.sa.SourceCodeBuilder

/**
 * Focus effect for stepped code slides, replacing CuP's built-in `highlighted()`.
 *
 * `highlighted()` scales focused blocks by a hardcoded 1.4x, which clips wide lines off
 * the slide. Span-style dimming (recoloring text via `styled` + SpanStyle) is smooth-less:
 * CuP lerps the SpanStyle every animation frame, rebuilding and re-laying-out the text.
 *
 * Instead we dim by drawing a background-colored scrim OVER the de-emphasized blocks —
 * pure draw-phase animation, the text itself never changes. Because a parent's scrim
 * would cover nested children, mark the COMPLEMENT: wrap each region that should fade
 * in `marker(dimmed(steps))` for the steps where it is NOT the focus, and leave the
 * focused region unmarked. A marker may own several `${m}...${X}` sections, so regions
 * sharing the same dim-steps can reuse one marker.
 */
private val dimStyle: SAStyle =
  object : SAStyle {
    override fun DrawScope.drawOver(rect: Rect, fraction: Float) {
      drawRect(
        color = Livewire.Background.copy(alpha = 0.72f * fraction),
        topLeft = rect.topLeft,
        size = rect.size,
      )
    }
  }

fun SourceCodeBuilder.dimmed(vararg steps: Int): SourceCodeBuilder.State = styled(dimStyle, *steps)

fun SourceCodeBuilder.dimmed(vararg steps: IntRange): SourceCodeBuilder.State =
  styled(dimStyle, *steps)
