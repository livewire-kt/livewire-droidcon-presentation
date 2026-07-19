import androidx.compose.runtime.Composable

/**
 * Registers glyph-fallback fonts on platforms without system font fallback (web). Black Han Sans
 * (titles) lacks even basic typographic glyphs (– — † … ™ →) and Google Sans lacks ⇒ ▼; on desktop
 * Skia falls back to system fonts, but the wasm build only has what we bundle and preload.
 */
@Composable
expect fun PreloadFallbackFonts()
