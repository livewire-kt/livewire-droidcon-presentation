package slides

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import livewire_presentation.generated.resources.Res
import livewire_presentation.generated.resources.livewire_logo
import net.kodein.cup.Slide
import org.jetbrains.compose.resources.painterResource
import widgets.Livewire
import widgets.LocalLivewireFonts
import kotlin.time.Duration.Companion.minutes
import widgets.PaceKeyframe

val thankYou by Slide(context = PaceKeyframe(39.minutes)) {
  val fonts = LocalLivewireFonts.current
  Column(
    modifier = Modifier.fillMaxSize().padding(36.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Image(
      painter = painterResource(Res.drawable.livewire_logo),
      contentDescription = "Livewire",
      modifier = Modifier.size(72.dp),
      contentScale = ContentScale.Fit,
    )
    Spacer(Modifier.height(16.dp))
    Text(text = "Thank you", fontFamily = fonts.title, color = Livewire.Cream, fontSize = 36.sp)
    Spacer(Modifier.height(8.dp))
    Text(
      text = "[ QUESTIONS?  LET'S TALK ]",
      fontFamily = fonts.mono,
      color = Livewire.Amber,
      fontSize = 9.sp,
      letterSpacing = 2.sp,
    )
    Spacer(Modifier.height(20.dp))
    Text(
      text = "github.com/livewire-kt/livewire",
      fontFamily = fonts.mono,
      color = Livewire.Gray,
      fontSize = 11.sp,
    )
  }
}
