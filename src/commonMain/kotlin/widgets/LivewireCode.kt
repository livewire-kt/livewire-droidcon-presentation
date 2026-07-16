package widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import net.kodein.cup.sa.SourceCode

/** SourceCode styled like the deck: JetBrains Mono + the deck's syntax palette. */
@Composable
fun LivewireCode(
  sourceCode: SourceCode,
  step: Int = 0,
  modifier: Modifier = Modifier,
  fontSize: TextUnit = 9.sp,
) {
  SourceCode(
    sourceCode = sourceCode,
    step = step,
    modifier = modifier,
    style =
      TextStyle(
        fontFamily = LocalLivewireFonts.current.mono,
        fontSize = fontSize,
//        color = Livewire.CodePlain,
      ),
    theme = livewireCodeTheme,
  )
}
