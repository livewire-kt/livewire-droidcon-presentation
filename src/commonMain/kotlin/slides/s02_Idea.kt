package slides

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import livewire_presentation.generated.resources.Res
import livewire_presentation.generated.resources.compose_remotely_art
import livewire_presentation.generated.resources.diagram_flip_it
import livewire_presentation.generated.resources.diagram_ui_vs_runtime
import livewire_presentation.generated.resources.platform_all
import livewire_presentation.generated.resources.remote_compose_art
import net.kodein.cup.PreparedSlide
import net.kodein.cup.Slide
import net.kodein.cup.sa.rememberSourceCode
import net.kodein.cup.speaker.SpeakerNotes
import org.jetbrains.compose.resources.painterResource
import widgets.Bullet
import widgets.CodeBox
import widgets.GifImage
import widgets.Livewire
import widgets.LivewireCode
import widgets.LocalLivewireFonts
import widgets.PaceKeyframe
import widgets.QrCode
import widgets.SectionSlide
import widgets.TitledSlide
import widgets.line
import kotlin.time.Duration.Companion.minutes

val sectionIdea by Slide(context = PaceKeyframe(6.minutes)) {
  SectionSlide(number = "02", title = "The Idea", subtitle = "How to Compose once over the wire")
}

val flipIt by
  Slide(
    context =
      SpeakerNotes(
        """DREW:
That idea is to flip the script!
In Flipper, meaning is reconstructed and re-rendered on the desktop.
But … what if meaning never left the app?
The app already knows how its debug data should look, so it should just send the looks.

A plugin is just Kotlin in your codebase; A database plugin can call your DAOs directly.
What if you wanted a plugin to access more complex state that would be hard to send across the wire?
What if the host never needs updates when you write or update a plugin."""
      )
  ) {
    TitledSlide(title = "Flip it: the app describes, the host renders", kicker = "// THE IDEA") {
      Image(
        painter = painterResource(Res.drawable.diagram_flip_it),
        contentDescription = "The app describes its UI, the host renders it",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Fit,
      )
    }
  }

val composeIsFun by
  PreparedSlide(
    context =
      SpeakerNotes(
        """DREW:
By now most of us know how powerful and delightful writing Compose can be.

Writing Compose is Fun!
"""
      )
  ) {
    val sourceCode =
      rememberSourceCode(language = "kotlin") {
        // language=kotlin
        """
        @Composable
        fun JetpackCompose {
          Card {
            var expanded by remember { mutableStateOf(false) }
            Column(Modifier.clickable { expanded = expanded }) {
              Image(painterResource(R.drawable.jetpack_compose))
              AnimatedVisibility(expanded) {
                Text(
                  text = "Jetpack Compose",
                  style = MaterialTheme.typography.bodyLarge,
                )
              }
            }
          }
        }
        """
          .trimIndent()
      }

    slideContent {
      TitledSlide(title = "Compose is Fun", kicker = "// COMPOSE") {
        Row(verticalAlignment = Alignment.CenterVertically) {
          CodeBox(Modifier.weight(1.2f)) {
            LivewireCode(sourceCode, modifier = Modifier)
          }

          Spacer(Modifier.width(16.dp))
          Box(
            modifier = Modifier.weight(0.8f),
            contentAlignment = Alignment.Center,
          ) {
            GifImage(
              path = "files/compose_fun.gif",
              contentDescription = "Expanding card demo",
              contentScale = ContentScale.Crop,
              modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .fillMaxHeight(),
            )
          }
        }
      }
    }
  }

val composeRemotely by
  Slide(
    context =
      SpeakerNotes(
        "DREW:\n" +
        "Wouldn't it be nice if we could write compose and have it render on our desktops? " +
          "It would be!\n\nThat sounds like some sort of **Remote Compose**!"
      )
  ) {
    val fonts = LocalLivewireFonts.current
    Column(
      modifier = Modifier.fillMaxSize().padding(horizontal = 36.dp, vertical = 22.dp)
    ) {
      Text(
        text = "// COMPOSE",
        fontFamily = fonts.mono,
        color = Livewire.Amber,
        fontSize = 7.sp,
        letterSpacing = 1.5.sp,
      )
      Spacer(Modifier.height(2.dp))
      Row(verticalAlignment = Alignment.Bottom) {
        Text(
          "What if we could remotely… ",
          fontFamily = fonts.title,
          color = Livewire.Cream,
          fontSize = 22.sp,
        )
        Text(
          "Compose",
          fontFamily = fonts.title,
          color = Livewire.Red,
          fontSize = 22.sp,
          fontStyle = FontStyle.Italic,
        )
      }
      Spacer(Modifier.height(28.dp))
      Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
        Image(
          painter = painterResource(Res.drawable.platform_all),
          contentDescription = null,
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Fit,
        )
      }
    }
  }

val remoteComposeYes by
  PreparedSlide(
    context =
      SpeakerNotes(
        "DREW:\n" +
        "Oh fantastic! This exists!\n\n" +
          "You can write Compose UI code in one app …\n\n" +
          "Then have it render **remotely** in another app!\n\n" +
          "**This will work, right?!?!**"
      )
  ) {
    val sourceCode =
      rememberSourceCode(language = "kotlin") {
        // language=kotlin
        """
        dependencies {
            implementation("androidx.compose.remote:remote-core:<version>")
            implementation("androidx.compose.remote:remote-creation-compose:<version>")
            implementation("androidx.compose.remote:remote-player-view:<version>")
        }
        """
          .trimIndent()
      }

    slideContent {
      TitledSlide(title = "Remote Compose™ ✅", kicker = "// COMPOSE") {
        Bullet(line { t("Write Compose in one app") })
        Bullet(line { t("Render Compose in another!") })
        Bullet(line { em("Great! This will work right?") })
        Spacer(Modifier.height(14.dp))
        CodeBox {
          LivewireCode(sourceCode, modifier = Modifier.fillMaxWidth())
        }
      }
    }
  }

val remoteComposeNo by
  Slide(
    context =
      SpeakerNotes(
        """DREW:
Errrr, not quite!

"At the time of writing"…

The creator, or producing, side of this library is only possible on the JVM.
So this doesn't work with our goal of supporting all multiplatform targets.

The player side is Android only currently.
So this doesn't work with our goal of rendering in our desktop app.

We looked at porting this to kotlin / kmp but the lift turned out to be more than we wanted to take on:
 - the core of both creation/player libraries are written in Java,
 - and we didn't want the burden of maintaining heavy changes to a fork of a young and frequently changing library."""
      )
  ) {
    TitledSlide(title = "Remote Compose™ ❌", kicker = "// COMPOSE") {
      Bullet(line { em("Creating is JVM-only") })
      Bullet(line { t("Not fully KMP compatible") }, indent = 1)
      Bullet(line { em("Player is Android-only") })
      Bullet(
        line {
          t("We want the companion ")
          b("desktop")
          t(" app to render the remote compose")
        },
        indent = 1,
      )
      Bullet(line { em("Too much of a lift to port") })
      Bullet(line { t("Core SDK is written in Java?!?") }, indent = 1)
      Bullet(line { t("Not wanting to create / maintain a fork of the library") }, indent = 1)
    }
  }

val composeRemotelyQ by
  Slide(
    context =
      SpeakerNotes(
        """DREW:
But what if we could **Compose…Remotely?**

Could we build our own Compose UI?

Could we build a Compose API that developers are already familiar with?
Functions like **Box, Column, Button, Icon, Text**?

Could we render this custom composition over the wire to accomplish our idea?"""
      )
  ) {
    TitledSlide(title = "Compose … Remotely?", kicker = "// COMPOSE") {
      Row {
        Column(Modifier.weight(1.3f)) {
          Bullet(
            line {
              t("What if we created our own custom ")
              i("Compose UI")
              t("?")
            }
          )
          Bullet(
            line {
              t("Could we use ")
              i("similar")
              t(" Composables for an API?")
            }
          )
          Bullet(line { i("i.e Box, Column, Button, Icon, etc") }, indent = 1)
          Bullet(
            line {
              t("How could we compose over the ")
              i("wire")
              t("?")
            }
          )
        }
        Spacer(Modifier.width(16.dp))
        Image(
          painter = painterResource(Res.drawable.compose_remotely_art),
          contentDescription = null,
          modifier = Modifier
            .weight(0.7f)
            .scale(scaleX = -1f, scaleY = 1f)
            .fillMaxSize()
            .offset(y = 40.dp),
          contentScale = ContentScale.Fit,
        )
      }
    }
  }

val uiVsRuntime by
  Slide(
    context =
      SpeakerNotes(
        "DREW:\n" +
        "So if we want to talk about writing custom compositions, we need to talk about what " +
          "\"Compose\" really is.\n\n" +
          "There is Compose UI, or Jetpack Compose based on who does your marketing, which is " +
          "probably what everyone thinks of when you mention \"Compose\". It's your columns, rows, " +
          "textfields and other widgets that we use to build our wonderful declarative UIs.\n\n" +
          "Then there is the compose compiler and runtime. It is an amazing tool for building " +
          "trees of data, tracking state dependencies, and efficiently updating your trees when " +
          "this state changes. It is platform agnostic and can run anywhere that Kotlin can run.\n\n" +
          "At the end of the day aren't most things just trees of data."
      )
  ) {
    val fonts = LocalLivewireFonts.current
    TitledSlide(title = "", kicker = "// COMPOSE BY ANY OTHER NAME") {
      Row(verticalAlignment = Alignment.Bottom) {
        Text("Compose ", fontFamily = fonts.title, color = Livewire.Cream, fontSize = 22.sp)
        Text(
          "UI",
          fontFamily = fonts.title,
          color = Livewire.Amber,
          fontSize = 24.sp,
          fontStyle = FontStyle.Italic,
        )
        Text("  vs.  ", fontFamily = fonts.title, color = Livewire.Gray, fontSize = 22.sp)
        Text("Compose ", fontFamily = fonts.title, color = Livewire.Cream, fontSize = 22.sp)
        Text(
          "Runtime",
          fontFamily = fonts.title,
          color = Livewire.Red,
          fontSize = 22.sp,
          fontStyle = FontStyle.Italic,
        )
      }
      Spacer(Modifier.height(14.dp))
      Row(Modifier.fillMaxSize()) {
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            line {
              b("UI ")
              i("(i.e. Jetpack Compose)")
            },
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
          )
          Spacer(Modifier.height(24.dp))

          GifImage(
            path = "files/ui_vs_runtime.gif",
            contentDescription = "Compose UI demo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
              .width(180.dp)
              .fillMaxHeight()
              .clip(
                RoundedCornerShape(
                  topStart = 30.dp,
                  topEnd = 30.dp,
                )
              ),
          )
        }
        Spacer(Modifier.width(20.dp))
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
          Text(line { b("Compiler / Runtime") }, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
          Spacer(Modifier.height(8.dp))
          Image(
            painter = painterResource(Res.drawable.diagram_ui_vs_runtime),
            contentDescription = "Compose compiler and runtime tree",
            modifier = Modifier
              .weight(1f)
              .fillMaxWidth()
              .padding(16.dp),
            contentScale = ContentScale.Fit,
          )
        }
      }
    }
  }

val otherTalks by
  Slide(
    context =
      SpeakerNotes(
        "DREW:\n" +
        "The Compose compiler, runtime, and internals are an amazing piece of tech and we won't " +
          "spend any time under the hood as this topic alone could fill several talks.\n\n" +
          "So instead check out these great talks that dive deeper into this topic."
      )
  ) {
    val fonts = LocalLivewireFonts.current
    TitledSlide(title = "Check out these other talks", kicker = "// COMMUNITY") {
      Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        listOf(
            Triple(
              "https://www.youtube.com/watch?v=6BRlI5zfCCk",
              "The Compose Runtime, Demystified",
              "Leland Richardson",
            ),
            Triple(
              "https://www.youtube.com/watch?v=EN45NNImwYs",
              "Demystifying the Compose Runtime & Compiler",
              "Andrew Bailey",
            ),
          )
          .forEach { (thumb, talkTitle, speaker) ->
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
              QrCode(
                data = thumb,
                contentDescription = talkTitle,
                modifier = Modifier.weight(1f)
                  .fillMaxWidth()
                  .padding(16.dp),
              )
              Spacer(Modifier.height(8.dp))
              Column(
                Modifier.height(56.dp)
              ) {
                Text(
                  talkTitle,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  lineHeight = 12.sp,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                  speaker,
                  color = Livewire.Gray,
                  fontSize = 10.sp,
                  lineHeight = 12.sp,
                  fontFamily = fonts.body
                )
              }
            }
          }
      }
    }
  }

val buildOurOwnTree by
  Slide(
    context =
      SpeakerNotes(
        "DREW:\n" +
        "So let's build our own tree\n\n" +
          "First, lets lay out some requirements to do this\n\n" +
          "We want developers to interact with an API they are already familiar with. We don't " +
          "want to re-invent how you write declarative UI. **Naming stuff like HStack and VStack " +
          "would just be insane.**\n\n" +
          "We want to support everywhere Kotlin can run. \nAs we mentioned with the Compose " +
          "compiler/runtime this is already free. Then using Compose Multiplatform we can meet our " +
          "host rendering requirement.\n\n" +
          "Lastly, We need the ability to send our custom composition over the wire.\n" +
          "So we need it to be serializable."
      )
  ) {
    TitledSlide(title = "So let's build our own tree…", kicker = "// CUSTOM COMPOSE") {
      Row(verticalAlignment = Alignment.Top) {
        Column(Modifier.weight(1.2f).padding(top = 16.dp)) {
          Text(line { b("What do we need?") }, fontSize = 16.sp)
          Spacer(Modifier.height(12.dp))
          Bullet(line { t("Compose-like APIs & widgets") })
          Bullet(line { t("Support for all KMP targets") })
          Bullet(line { i("Android/iOS/Desktop/Web") }, indent = 1)
          Bullet(line { t("Ability to render our composition over the wire") })
        }
        Spacer(Modifier.width(16.dp))
        Box(
          modifier = Modifier
            .weight(0.8f)
            .fillMaxHeight(),
          contentAlignment = Alignment.Center
        ) {
          GifImage(
            path = "files/build_own_compose.gif",
            contentDescription = "Remote Compose",
            modifier = Modifier
              .height(165.dp),
            contentScale = ContentScale.FillHeight,
          )
        }
      }
    }
  }
