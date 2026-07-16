@file:OptIn(PluginCupAPI::class)

package net.kodein.cup.speaker

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import speakernotes.icons.SpeakerNotes
import speakernotes.icons.SpeakerNotesOff
import net.kodein.cup.CupKeyEvent
import net.kodein.cup.PluginCupAPI
import net.kodein.cup.PresentationState
import net.kodein.cup.config.CupAdditionalOverlay
import net.kodein.cup.config.CupConfigurationBuilder
import net.kodein.cup.config.CupPlugin
import net.kodein.cup.key
import net.kodein.cup.laser.Laser
import net.kodein.cup.laser.LaserDisplay
import net.kodein.cup.type
import net.kodein.cup.utils.SlideContext
import net.kodein.cup.utils.SlideContextElement
import speakernotes.icons.Icons

@Stable
public class SpeakerNotes(notes: List<Pair<IntRange, String>>) : SlideContextElement<SpeakerNotes>(Key) {
  internal companion object Key : SlideContext.Key<SpeakerNotes> {
    private val allSteps = 0..Int.MAX_VALUE
  }

  public val notes: List<Pair<IntRange, Pair<String, Color>>> = notes.map { (range, text) ->
    val text = text.trim()
    range to when {
      text.startsWith("laurie", ignoreCase = true) -> text.lines().drop(1).joinToString("\n").trim() to Color(0xFF005500)
      text.startsWith("eric", ignoreCase = true) -> text.lines().drop(1).joinToString("\n").trim() to Color(0xFF880000)
      else -> text to Color.Black
    }
  }

  public constructor(text: String) : this(listOf(allSteps to text))
}

internal class SpeakerNotesPlugin(val key: Pair<Key, String>?) : CupPlugin {
  var isOpen by mutableStateOf(false)

  @Composable
  override fun BoxScope.Content() {
    var laser: Laser? by remember { mutableStateOf(null) }
    if (isOpen) {
      SpeakerWindow(
        laser = laser,
        setLaser = { laser = it },
        onCloseRequest = {
          isOpen = false
          laser = null
        },
      )
    }
    if (laser != null) {
      LaserDisplay(laser!!)
    }
  }

  override fun overlay(state: PresentationState): List<CupAdditionalOverlay> = listOf(
    CupAdditionalOverlay(
      text = "Speaker notes",
      keys = key?.second,
      onClick = { isOpen = !isOpen },
      icon = if (isOpen) Icons.SpeakerNotesOff else Icons.SpeakerNotes,
    ),
  )

  override fun onKeyEvent(event: CupKeyEvent): Boolean {
    if (event.type != KeyEventType.KeyDown) return false
    if (event.key == key?.first) {
      isOpen = !isOpen
      return true
    }
    return false
  }
}

public fun CupConfigurationBuilder.speakerWindow(
  key: Pair<Key, String>? = Key.S to "S",
) {
  plugin(SpeakerNotesPlugin(key))
}

internal val LocalIsInSpeakerWindow = compositionLocalOf { false }

@Composable
public fun isInSpeakerWindow(): Boolean = LocalIsInSpeakerWindow.current
