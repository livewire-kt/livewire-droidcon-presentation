package slides

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import livewire_presentation.generated.resources.Res
import livewire_presentation.generated.resources.speaker_drew
import livewire_presentation.generated.resources.speaker_eric
import livewire_presentation.generated.resources.title_art
import net.kodein.cup.Slide
import net.kodein.cup.speaker.SpeakerNotes
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import widgets.Livewire
import widgets.LivewireFonts
import widgets.LocalLivewireFonts

val title by Slide {
  val fonts = LocalLivewireFonts.current
  Row(
    modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp, vertical = 20.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Image(
      painter = painterResource(Res.drawable.title_art),
      contentDescription = null,
      modifier = Modifier.width(80.dp).fillMaxSize(),
      contentScale = ContentScale.Fit,
    )
    Spacer(Modifier.width(24.dp))
    Column(verticalArrangement = Arrangement.Center) {
      Text(text = "LIVEWIRE", fontFamily = fonts.title, color = Livewire.Cream, fontSize = 52.sp)
      Text(
        text = "[ Debugging Using Remote Compose, Made Easy with Livewire ]",
        fontFamily = fonts.mono,
        color = Livewire.Amber,
        fontSize = 12.sp,
      )
      Spacer(Modifier.height(28.dp))
      Row {
        Speaker(Res.drawable.speaker_drew, "Drew\nHeavner", fonts)
        Spacer(Modifier.width(28.dp))
        Speaker(Res.drawable.speaker_eric, "Eric\nKuck", fonts)
      }
    }
  }
}

@Composable
private fun Speaker(photo: DrawableResource, name: String, fonts: LivewireFonts) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Image(
      painter = painterResource(photo),
      contentDescription = name,
      contentScale = ContentScale.Crop,
      modifier =
        Modifier.size(40.dp)
          .border(width = 2.dp, color = Livewire.Red, shape = CircleShape)
          .clip(CircleShape),
    )
    Spacer(Modifier.width(8.dp))
    Text(
      text = name,
      fontFamily = fonts.title,
      fontSize = 13.sp,
      lineHeight = 13.sp,
      color = Livewire.Cream,
    )
  }
}

val agenda by
  Slide(context = SpeakerNotes("Quick roadmap — five stops, ending in a live demo.")) {
    val fonts = LocalLivewireFonts.current
    Column(Modifier.fillMaxSize().padding(horizontal = 36.dp, vertical = 22.dp)) {
      Text(
        text = "// AGENDA",
        fontFamily = fonts.mono,
        color = Livewire.Amber,
        fontSize = 7.sp,
        letterSpacing = 1.5.sp,
      )
      Spacer(Modifier.height(2.dp))
      Text(
        text = "What we'll cover",
        fontFamily = fonts.title,
        color = Livewire.Cream,
        fontSize = 22.sp,
      )
      Spacer(Modifier.height(16.dp))
      listOf(
          "01" to "The problem: iterating on Android UI is slow",
          "02" to "The idea: a live wire between host & client",
          "03" to "Architecture: WebSockets over ADB",
          "04" to "Kotlin Multiplatform in practice",
          "05" to "Demo & what's next",
        )
        .forEach { (num, item) ->
          Row(
            modifier = Modifier.padding(vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Box(
              modifier = Modifier.size(32.dp).border(1.dp, Livewire.Amber),
              contentAlignment = Alignment.Center,
            ) {
              Text(num, fontFamily = fonts.mono, color = Livewire.Red, fontSize = 12.sp)
            }
            Spacer(Modifier.width(12.dp))
            Text(item, fontSize = 12.sp)
          }
        }
    }
  }
