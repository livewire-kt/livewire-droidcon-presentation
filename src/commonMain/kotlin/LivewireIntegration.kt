import androidx.compose.runtime.Composable

/**
 * Attaches the Livewire client (recomposition plugin, slide-deck remote, Deck Doctor) to the
 * running deck. Livewire only ships jvm/android/ios artifacts, so this is a no-op on web.
 *
 * Must be called inside cupApplication so [net.kodein.cup.LocalPresentationState] is available.
 */
@Composable
expect fun LivewireIntegration()
