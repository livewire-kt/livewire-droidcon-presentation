import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.font.FontFamily
import livewire_presentation.generated.resources.Res
import livewire_presentation.generated.resources.googlesans_regular
import livewire_presentation.generated.resources.jetbrainsmono_regular
import org.jetbrains.compose.resources.Font

@Composable
actual fun PreloadFallbackFonts() {
  val resolver = LocalFontFamilyResolver.current
  val sans = Font(Res.font.googlesans_regular)
  val mono = Font(Res.font.jetbrainsmono_regular)
  LaunchedEffect(resolver, sans, mono) {
    // Preloaded fonts become the glyph-fallback chain, tried in preload order.
    resolver.preload(FontFamily(sans))
    resolver.preload(FontFamily(mono))
  }
}
