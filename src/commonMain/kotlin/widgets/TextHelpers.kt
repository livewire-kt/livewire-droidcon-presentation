package widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

/**
 * Small DSL for the deck's inline emphasis styles: plain cream text, amber-bold emphasis, italics,
 * and mono inline code.
 */
class LineBuilder(private val fonts: LivewireFonts, private val builder: AnnotatedString.Builder) {
  fun t(s: String) = builder.append(s)

  /** Amber bold — the deck's emphasis style. */
  fun em(s: String, color: Color = Livewire.Amber) {
    builder.withStyle(SpanStyle(color = color, fontWeight = FontWeight.Bold)) { append(s) }
  }

  fun b(s: String) {
    builder.withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(s) }
  }

  fun i(s: String) {
    builder.withStyle(SpanStyle(fontStyle = FontStyle.Italic)) { append(s) }
  }

  /** Inline code — JetBrains Mono, cream. */
  fun code(s: String, color: Color = Livewire.Cream) {
    builder.withStyle(
      SpanStyle(
        fontFamily = fonts.mono,
        fontSize = 0.9.em,
        color = color,
      )
    ) { append(s) }
  }

  /** Red accent (deprecated/dead things). */
  fun red(s: String) {
    builder.withStyle(SpanStyle(color = Livewire.Red, fontWeight = FontWeight.Bold)) { append(s) }
  }

  private val Double.em
    get() =
      androidx.compose.ui.unit.TextUnit(this.toFloat(), androidx.compose.ui.unit.TextUnitType.Em)
}

@Composable
fun line(block: LineBuilder.() -> Unit): AnnotatedString {
  val fonts = LocalLivewireFonts.current
  return buildAnnotatedString { LineBuilder(fonts, this).block() }
}
