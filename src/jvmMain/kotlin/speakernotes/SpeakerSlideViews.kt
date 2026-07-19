@file:OptIn(PluginCupAPI::class)

package net.kodein.cup.speaker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import speakernotes.icons.Close
import speakernotes.icons.Draw
import speakernotes.icons.Icons
import speakernotes.icons.Rectangle
import net.kodein.cup.LocalPresentationSize
import net.kodein.cup.LocalPresentationState
import net.kodein.cup.PluginCupAPI
import net.kodein.cup.PresentationMainView
import net.kodein.cup.PresentationState
import net.kodein.cup.currentSlide
import net.kodein.cup.laser.Laser
import net.kodein.cup.laser.LaserDraw
import net.kodein.cup.utils.IconButtonWithTooltip
import net.kodein.cup.utils.OverlayState
import net.kodein.cup.utils.OverlayedBox

@Composable
internal fun SpeakerNextSlideView(
  presentationState: PresentationState,
  ratio: Float,
  modifier: Modifier = Modifier,
) {
  var viewSize: Size? by remember { mutableStateOf(null) }
  Box(
    modifier = modifier
      .padding(8.dp)
      .aspectRatio(ratio, matchHeightConstraintsFirst = true)
      .shadow(8.dp)
      .onSizeChanged { viewSize = it.toSize() }
      .clipToBounds(),
  ) {
    if (viewSize != null) {
      val shiftedPresentationState = remember(presentationState) {
        ShiftedPresentationState(
          presentationState
        )
      }
      CompositionLocalProvider(
        LocalPresentationState provides shiftedPresentationState,
        LocalPresentationSize provides viewSize!!,
        LocalIsInSpeakerWindow provides true,
      ) {
        PresentationMainView()

        val speakerNotes = shiftedPresentationState.currentSlide.context[SpeakerNotes]
        if (speakerNotes != null) {
          val (_, notes) = speakerNotes.notes.first { (range, _) -> shiftedPresentationState.currentPosition.step in range }

          Box(
            modifier = Modifier
              .size(60.dp)
              .align(Alignment.BottomStart)
              .clip(CircleShape)
              .background(notes.second),
            )
        }
      }
    }
  }
}

@Composable
internal fun SpeakerCurrentSlideView(
  ratio: Float,
  laser: Laser?,
  setLaser: (Laser?) -> Unit,
  modifier: Modifier = Modifier,
) {
  val scope = rememberCoroutineScope()
  val overlayState = remember { OverlayState() }

  var viewSize: Size? by remember { mutableStateOf(null) }

  Box(
    modifier = modifier
      .padding(8.dp)
      .aspectRatio(ratio, matchHeightConstraintsFirst = true)
      .shadow(8.dp)
      .onSizeChanged { viewSize = it.toSize() }
      .clipToBounds(),
  ) {
    OverlayedBox(
      state = overlayState,
      overlay = {
        Row(
          Modifier
            .overlayComponent(scope)
            .align(Alignment.BottomStart)
            .padding(16.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface),
        ) {
          if (laser == null) {
            IconButtonWithTooltip(
              onClick = { setLaser(Laser.Pointer()) },
              text = "Pointer & free draw (P)",
              icon = Icons.Draw,
            )
            IconButtonWithTooltip(
              onClick = { setLaser(Laser.Highlight()) },
              text = "Highlight rectangle (H)",
              icon = Icons.Rectangle,
            )
          } else {
            IconButtonWithTooltip(
              onClick = { setLaser(null) },
              text = "End draw",
              keys = "Esc",
              icon = Icons.Close,
            )
          }
        }
      },
      modifier = Modifier.fillMaxSize(),
    ) {
      if (viewSize != null) {
        CompositionLocalProvider(
          LocalPresentationSize provides viewSize!!,
          LocalIsInSpeakerWindow provides true,

        ) {
          PresentationMainView()
        }
      }
      if (laser != null) {
        LaserDraw(
          laser = laser,
          setLaser = setLaser,
          modifier = Modifier.fillMaxSize(),
        )
      }
    }
  }
}
