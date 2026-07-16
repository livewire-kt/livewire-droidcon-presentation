package widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Standard content slide: small amber mono kicker (`// THE PROBLEM`) above a
 * big Black Han Sans title, then free-form content.
 */
@Composable
fun TitledSlide(
    title: String,
    kicker: String? = null,
    titleSize: TextUnit = 22.sp,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    val fonts = LocalLivewireFonts.current
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 36.dp, vertical = 22.dp),
        horizontalAlignment = horizontalAlignment,
    ) {
        if (kicker != null) {
            Text(
                text = kicker,
                fontFamily = fonts.mono,
                color = Livewire.Amber,
                fontSize = 7.sp,
                letterSpacing = 1.5.sp,
            )
            Spacer(Modifier.height(2.dp))
        }
        Text(
            text = title,
            fontFamily = fonts.title,
            color = Livewire.Cream,
            fontSize = titleSize,
        )
        Spacer(Modifier.height(12.dp))
        content()
    }
}

/**
 * Section divider: `[ SECTION ]`, big amber number, title, gray subtitle.
 */
@Composable
fun SectionSlide(
    number: String,
    title: String,
    subtitle: String,
) {
    val fonts = LocalLivewireFonts.current
    Column(
        modifier = Modifier.fillMaxSize().padding(36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "[ SECTION ]",
            fontFamily = fonts.mono,
            color = Livewire.Amber,
            fontSize = 8.sp,
            letterSpacing = 3.sp,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = number,
            fontFamily = fonts.title,
            color = Livewire.Amber,
            fontSize = 40.sp,
        )
        Text(
            text = title,
            fontFamily = fonts.title,
            color = Livewire.Cream,
            fontSize = 30.sp,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = subtitle,
            color = Livewire.Gray,
            fontSize = 11.sp,
        )
    }
}

@Composable
fun ColumnScope.Bullet(
    text: AnnotatedString,
    visible: Boolean = true,
    indent: Int = 0,
    style: TextStyle = TextStyle.Default,
) {
    AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
        Row(modifier = Modifier.padding(start = (indent * 14).dp, top = 3.dp, bottom = 3.dp)) {
            Text("▸", color = Livewire.Amber, fontSize = 11.sp)
            Spacer(Modifier.width(6.dp))
            Text(text, style = style, lineHeight = 15.sp)
        }
    }
}

@Composable
fun ColumnScope.Bullet(
    text: String,
    visible: Boolean = true,
    indent: Int = 0,
    style: TextStyle = TextStyle.Default,
) = Bullet(AnnotatedString(text), visible, indent, style)
