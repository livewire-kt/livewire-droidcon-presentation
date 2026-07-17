package widgets

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrBrush
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrLogoPadding
import io.github.alexzhirkevich.qrose.options.QrLogoShape
import io.github.alexzhirkevich.qrose.options.QrOptions
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.brush
import io.github.alexzhirkevich.qrose.options.circle
import io.github.alexzhirkevich.qrose.options.roundCorners
import io.github.alexzhirkevich.qrose.options.solid
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import livewire_presentation.generated.resources.Res
import livewire_presentation.generated.resources.jc
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun QrCode(
  data: String,
  modifier: Modifier = Modifier,
  contentDescription: String? = null,
  logo: Painter = painterResource(Res.drawable.jc)
) {
  Image(
    painter = rememberQrCodePainter(data, options = QrOptions{
      logo {
        painter = logo
        padding = QrLogoPadding.Natural(.1f)
        shape = QrLogoShape.circle()
        size = 0.2f
      }

      shapes() {
        ball = QrBallShape.circle()
        darkPixel = QrPixelShape.roundCorners()
        frame = QrFrameShape.roundCorners(.25f)
      }
      colors {
        dark = QrBrush.brush {
          Brush.linearGradient(
            0f to Livewire.Red,
            1f to Livewire.Amber,
            end = Offset(it, it)
          )
        }
        frame = QrBrush.solid(Color.White)
      }
    }),
    contentDescription = contentDescription,
    modifier = modifier,
    contentScale = ContentScale.Fit,
  )
}
