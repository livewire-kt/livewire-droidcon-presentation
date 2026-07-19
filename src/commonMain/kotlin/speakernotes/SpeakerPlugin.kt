package net.kodein.cup.speaker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import net.kodein.cup.config.CupConfigurationBuilder
import net.kodein.cup.utils.SlideContext
import net.kodein.cup.utils.SlideContextElement

@Stable
public class SpeakerNotes(notes: List<Pair<IntRange, String>>) : SlideContextElement<SpeakerNotes>(Key) {
  internal companion object Key : SlideContext.Key<SpeakerNotes> {
    private val allSteps = 0..Int.MAX_VALUE
  }

  public val notes: List<Pair<IntRange, Pair<String, Color>>> = notes.map { (range, text) ->
    val text = text.trim()
    range to when {
      text.startsWith("drew", ignoreCase = true) -> text.lines().drop(1).joinToString("\n").trim() to Color(0xFF880000)
      text.startsWith("eric", ignoreCase = true) -> text.lines().drop(1).joinToString("\n").trim() to Color(0xFF005500)
      else -> text to Color.Black
    }
  }

  public constructor(text: String) : this(listOf(allSteps to text))
}

/**
 * Registers the speaker-notes window plugin. The speaker window is a second OS window, so this is
 * desktop-only; on web it registers nothing.
 */
public expect fun CupConfigurationBuilder.speakerWindow(key: Pair<Key, String>? = Key.S to "S")

internal val LocalIsInSpeakerWindow = compositionLocalOf { false }

@Composable
public fun isInSpeakerWindow(): Boolean = LocalIsInSpeakerWindow.current
