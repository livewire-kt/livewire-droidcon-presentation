package widgets

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import livewire_presentation.generated.resources.Res
import livewire_presentation.generated.resources.blackhansans_regular
import livewire_presentation.generated.resources.googlesans_bold
import livewire_presentation.generated.resources.googlesans_bold_italic
import livewire_presentation.generated.resources.googlesans_italic
import livewire_presentation.generated.resources.googlesans_medium
import livewire_presentation.generated.resources.googlesans_medium_italic
import livewire_presentation.generated.resources.googlesans_regular
import livewire_presentation.generated.resources.googlesans_semibold
import livewire_presentation.generated.resources.googlesans_semibold_italic
import livewire_presentation.generated.resources.jetbrainsmono_bold
import livewire_presentation.generated.resources.jetbrainsmono_bold_italic
import livewire_presentation.generated.resources.jetbrainsmono_italic
import livewire_presentation.generated.resources.jetbrainsmono_regular
import net.kodein.cup.sa.SourceCodeTheme
import net.kodein.cup.widgets.material3.cupScaleDown
import org.jetbrains.compose.resources.Font

/** Deck palette, extracted from the original pptx theme. */
object Livewire {
  val Background = Color(0xFF0B0E11)
  val Cream = Color(0xFFF5F1EC)
  val Amber = Color(0xFFFAAC43)
  val Red = Color(0xFFEA2526)
  val Gray = Color(0xFF939B9A)
  val Slate = Color(0xFF33403F)

  // Source code colors (from the deck's code textboxes)
  val CodeKeyword = Color(0xFFF40B0B)
  val CodeType = Color(0xFFFFA500)
  val CodePlain = Color(0xFFF2EDEA)
  val CodeComment = Color(0xFF8A7A72)
  val CodeNumber = Color(0xFFFFD666)
  val CodeString = Color(0xFF9CCC65)
}

data class LivewireFonts(val title: FontFamily, val body: FontFamily, val mono: FontFamily)

val LocalLivewireFonts =
  staticCompositionLocalOf<LivewireFonts> {
    error("LivewireFonts not provided — wrap content in LivewireTheme")
  }

val livewireCodeTheme: SourceCodeTheme = { cls ->
  when (cls) {
    "default",
    "subst",
    "punctuation",
    "operator",
    "tag" -> SpanStyle(color = Livewire.CodePlain)

    "keyword",
    "literal" -> SpanStyle(color = Livewire.CodeKeyword)

    // NOTE: "function" and "params" are deliberately unmapped — in hljs's Kotlin
    // grammar they are wrapper scopes spanning the whole signature; styling them
    // would repaint receivers and parameter lists instead of just names.
    "title",
    "class",
    "type",
    "name",
    "built_in",
    "symbol",
    "variable",
    "property",
    "attr",
    "meta" -> SpanStyle(color = Livewire.CodeType)

    "comment",
    "quote",
    "doctag" -> SpanStyle(color = Livewire.CodeComment)

    "number" -> SpanStyle(color = Livewire.CodeNumber)

    "string",
    "regexp" -> SpanStyle(color = Livewire.CodeString)

    "strong" -> SpanStyle(fontWeight = FontWeight.Bold)
    "emphasis" -> SpanStyle(fontStyle = FontStyle.Italic)

    else -> null
  }
}

@Composable
fun LivewireTheme(content: @Composable () -> Unit) {
  val fonts =
    LivewireFonts(
      title = FontFamily(Font(Res.font.blackhansans_regular)),
      body =
        FontFamily(
          Font(Res.font.googlesans_regular),
          Font(Res.font.googlesans_italic, style = FontStyle.Italic),
          Font(Res.font.googlesans_medium, weight = FontWeight.Medium),
          Font(
            Res.font.googlesans_medium_italic,
            weight = FontWeight.Medium,
            style = FontStyle.Italic,
          ),
          Font(Res.font.googlesans_semibold, weight = FontWeight.SemiBold),
          Font(
            Res.font.googlesans_semibold_italic,
            weight = FontWeight.SemiBold,
            style = FontStyle.Italic,
          ),
          Font(Res.font.googlesans_bold, weight = FontWeight.Bold),
          Font(Res.font.googlesans_bold_italic, weight = FontWeight.Bold, style = FontStyle.Italic),
        ),
      mono =
        FontFamily(
          Font(Res.font.jetbrainsmono_regular),
          Font(Res.font.jetbrainsmono_italic, style = FontStyle.Italic),
          Font(Res.font.jetbrainsmono_bold, weight = FontWeight.Bold),
          Font(
            Res.font.jetbrainsmono_bold_italic,
            weight = FontWeight.Bold,
            style = FontStyle.Italic,
          ),
        ),
    )

  MaterialTheme(
    colorScheme =
      darkColorScheme(
        background = Livewire.Background,
        surface = Livewire.Background,
        onBackground = Livewire.Cream,
        onSurface = Livewire.Cream,
        primary = Livewire.Amber,
        secondary = Livewire.Red,
      ),
    typography = MaterialTheme.typography.cupScaleDown(),
  ) {
    CompositionLocalProvider(
      LocalLivewireFonts provides fonts,
      androidx.compose.material3.LocalContentColor provides Livewire.Cream,
      androidx.compose.material3.LocalTextStyle provides
        TextStyle(fontFamily = fonts.body, color = Livewire.Cream, fontSize = 12.sp),
    ) {
      content()
    }
  }
}
