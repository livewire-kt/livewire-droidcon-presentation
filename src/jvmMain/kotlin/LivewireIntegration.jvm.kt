import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import com.livewire.client.LivewireClient
import com.livewire.plugin.recomposition.RecompositionPlugin
import kotlin.time.Duration.Companion.minutes
import livewire.DeckDoctorPlugin
import livewire.SlideDeckPlugin
import net.kodein.cup.LocalPresentationState

@Composable
actual fun LivewireIntegration() {
  val presentationState = LocalPresentationState.current
  val livewireClient = remember {
    LivewireClient {
      install(RecompositionPlugin())
      install(SlideDeckPlugin(presentationState, talkDuration = 40.minutes))
      install(DeckDoctorPlugin())
    }
  }

  DisposableEffect(Unit) {
    livewireClient.start()

    onDispose {
      livewireClient.stop()
    }
  }
}
