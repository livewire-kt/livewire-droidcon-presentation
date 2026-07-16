package livewire

import androidx.compose.runtime.Composable
import com.livewire.ui.Plugin
import com.livewire.ui.PluginInfo
import livewire.icons.CoPresent
import livewire.icons.Icons

class SlideDeckPlugin : Plugin {
  override val info: PluginInfo = PluginInfo(
    pluginId = "com.livewire.slide-deck",
    title = "Slide Deck",
    icon = Icons.CoPresent,
  )

  @Composable
  override fun Content() {
    
  }
}
