package slides

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import livewire_presentation.generated.resources.Res
import livewire_presentation.generated.resources.jc
import livewire_presentation.generated.resources.jetbrains
import livewire_presentation.generated.resources.livewire_logo
import net.kodein.cup.Slide
import org.jetbrains.compose.resources.painterResource
import widgets.Livewire
import widgets.LocalLivewireFonts
import kotlin.time.Duration.Companion.minutes
import widgets.PaceKeyframe
import widgets.QrCode
import kotlin.time.Duration.Companion.seconds

val thankYou by Slide(context = PaceKeyframe(39.minutes)) {
  val fonts = LocalLivewireFonts.current
  Row(
    modifier = Modifier
      .fillMaxSize()
  ) {
    Column(
      modifier = Modifier.weight(1f),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      LivewireQrCode(
        title = "Livewire",
        url = "https://github.com/livewire-kt/livewire",
        logo = painterResource(Res.drawable.livewire_logo),
      )
      Spacer(Modifier.weight(1f))
      LivewireQrCode(
        title = "Livewire IntelliJ\nTheme",
        url = "https://github.com/livewire-kt/livewire-intellij-theme",
        logo = painterResource(Res.drawable.jetbrains),
      )
    }
    Column(
      modifier = Modifier
        .weight(2f)
        .fillMaxHeight()
        .padding(36.dp),
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
    Column(
      modifier = Modifier.weight(1f),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      LivewireQrCode(
        title = "This slide deck",
        url = "https://github.com/livewire-kt/livewire-droidcon-presentation",
        logo = painterResource(Res.drawable.jc)
      )
    }
  }
}

@Composable
private fun LivewireQrCode(
  title: String,
  url: String,
  logo: Painter,
  modifier: Modifier = Modifier,
) {
  var visible by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    delay(1.seconds)
    visible = true
  }

  val fonts = LocalLivewireFonts.current
  AnimatedVisibility(
    visible = visible,
    modifier = modifier,
    enter = fadeIn() + expandIn(
      animationSpec = tween(
        durationMillis = 700,
      ),
      expandFrom = Alignment.Center,
    )
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      QrCode(
        data = url,
        modifier = Modifier.size(100.dp),
        logo = logo,
      )
      Spacer(Modifier.height(4.dp))
      Text(
        text = title,
        textAlign = TextAlign.Center,
        fontSize = 12.sp,
        lineHeight = 14.sp,
        fontFamily = fonts.mono,
        fontWeight = FontWeight.SemiBold,
      )
    }
  }
}
