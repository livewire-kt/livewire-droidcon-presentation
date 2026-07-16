@file:OptIn(PluginCupAPI::class)

package net.kodein.cup.speaker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import net.kodein.cup.LocalPresentationState
import net.kodein.cup.PluginCupAPI
import net.kodein.cup.PresentationState
import net.kodein.cup.currentSlide
import net.kodein.cup.laser.Laser
import net.kodein.cup.utils.cupToolsColorScheme
import widgets.PaceMeter

@Composable
internal fun SpeakerMainView(
  ratio: Float,
  laser: Laser?,
  setLaser: (Laser?) -> Unit,
  modifier: Modifier = Modifier,
) {
  val presentationState = LocalPresentationState.current

  Column(modifier = modifier) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.3f),
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      SpeakerCurrentSlideView(
        ratio = ratio,
        laser = laser,
        setLaser = setLaser,
        modifier = Modifier.weight(1f),
      )

      Box(
        modifier = Modifier.align(Alignment.CenterVertically)
      ) {
        PaceMeter() // full detail, presenter-only
      }

      SpeakerNextSlideView(
        presentationState = presentationState,
        ratio = ratio,
        modifier = Modifier.weight(1f),
      )
    }

    Notes(
      presentationState = presentationState,
      modifier = Modifier.fillMaxWidth(),
    )
  }
}

@Composable
private fun Notes(
  presentationState: PresentationState,
  modifier: Modifier = Modifier,
) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp),
  ) {
    Column(modifier = Modifier.weight(1f)) {
      MaterialTheme(colorScheme = cupToolsColorScheme(darkTheme = true)) {
        val speakerNotes = presentationState.currentSlide.context[SpeakerNotes]
        if (speakerNotes != null) {
          val (_, notes) = speakerNotes.notes.first { (range, _) -> presentationState.currentPosition.step in range }
          Text(
            text = notes.first.trimIndent(),
            color = notes.second,
            autoSize = TextAutoSize.StepBased(maxFontSize = 38.sp, minFontSize = 16.sp),
            lineHeight = 1.4.em,
          )
        }
      }
    }
  }
}
