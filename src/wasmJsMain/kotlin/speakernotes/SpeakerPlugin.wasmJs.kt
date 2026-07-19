package net.kodein.cup.speaker

import androidx.compose.ui.input.key.Key
import net.kodein.cup.config.CupConfigurationBuilder

public actual fun CupConfigurationBuilder.speakerWindow(key: Pair<Key, String>?) {
  // No speaker window on web — it requires a second OS window.
}
