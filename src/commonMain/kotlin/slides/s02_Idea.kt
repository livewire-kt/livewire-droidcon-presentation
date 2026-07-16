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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.drawscope.translate
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
import livewire_presentation.generated.resources.platform_logo_1
import livewire_presentation.generated.resources.platform_logo_2
import livewire_presentation.generated.resources.platform_logo_3
import livewire_presentation.generated.resources.platform_logo_4
import livewire_presentation.generated.resources.platform_logo_5
import livewire_presentation.generated.resources.platform_logo_6
import livewire_presentation.generated.resources.platform_logo_7
import livewire_presentation.generated.resources.remote_compose_art
import livewire_presentation.generated.resources.talk_thumb_1
import livewire_presentation.generated.resources.talk_thumb_2
import net.kodein.cup.PreparedSlide
import net.kodein.cup.Slide
import net.kodein.cup.sa.rememberSourceCode
import net.kodein.cup.speaker.SpeakerNotes
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import widgets.Bullet
import widgets.CodeBox
import widgets.GifImage
import widgets.Livewire
import widgets.LivewireCode
import widgets.LocalLivewireFonts
import widgets.SectionSlide
import widgets.TitledSlide
import widgets.line

val sectionIdea by Slide {
  SectionSlide(number = "02", title = "The Idea", subtitle = "How to Compose once over the wire")
}

val flipIt by
  Slide(
    context =
      SpeakerNotes(
        "In Flipper, meaning is reconstructed on the desktop by a second program.\n\n" +
          "What if meaning never left the app? The app already knows how its debug data should " +
          "look, so it sends the looks.\n\n" +
          "A plugin is just Kotlin in your codebase; your database plugin can call your DAO.\n\n" +
          "The host never updates when you write a new plugin. If you're thinking 'isn't that " +
          "just Remote Compose?' — yes, next slide."
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
        "So! Writing Compose is Fun!\n\n" +
          "By now most of us know how powerful and delightful writing Compose can be."
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
          "What if we could Compose… ",
          fontFamily = fonts.title,
          color = Livewire.Cream,
          fontSize = 22.sp,
        )
        Text(
          "remotely",
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
        "Oh fantastic! This exists!\n\n" +
          "You can write Compose UI code in one app …\n\n" +
          "Then have it render **remotely** in another app!\n\n" +
          "**This will work!!**"
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
        "Errrr, not quite!\n\n" +
          "\"At the time of writing\"…\n\n" +
          "The creator, or producing, side of this library is only possible on the JVM. So this " +
          "doesn't work with our goal of supporting all multiplatform targets.\n\n" +
          "The player side is Android only currently. So this doesn't work with our goal of " +
          "rendering in our desktop app.\n\n" +
          "We looked at porting this to kotlin / kmp but the lift turned out to be more than we " +
          "wanted to take on: the core of both creation/player libraries are written in Java, and " +
          "we didn't want the burden of maintaining heavy changes to a fork of a young and " +
          "frequently changing library."
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
        "But what if we could **Compose…Remotely?**\n\n" +
          "Could we build our own Compose UI?\n\n" +
          "Could we build a Compose API that developers are already familiar with? Functions " +
          "like **Box, Column, Button, Icon, Text**?\n\n" +
          "Could we render this custom composition over the wire to accomplish our idea?"
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
              Res.drawable.talk_thumb_1,
              "The Compose Runtime, Demystified",
              "Leland Richardson",
            ),
            Triple(
              Res.drawable.talk_thumb_2,
              "Demystifying the Compose Runtime & Compiler",
              "Andrew Bailey",
            ),
          )
          .forEach { (thumb, talkTitle, speaker) ->
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
              Image(
                painter = painterResource(thumb),
                contentDescription = talkTitle,
                modifier = Modifier.weight(1f)
                  .fillMaxWidth()
                  .padding(16.dp),
                contentScale = ContentScale.Fit,
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
        "\"I'm going to build my own composables! With serialization! And Multiplatform " +
          "rendering!\"\n\n" +
          "We want developers to interact with an API they are already familiar with. We don't " +
          "want to re-invent how you write declarative UI. **Naming stuff like HStack and VStack " +
          "would just be insane.**\n\n" +
          "We want to support everywhere Kotlin can run. As we mentioned with Compose " +
          "compiler/runtime this is already free! And with Compose Multiplatform we can meet our " +
          "host rendering requirement.\n\n" +
          "We want to be able to send our custom composition over the wire. So we need it to be " +
          "serializable."
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
        Image(
          painter = painterResource(Res.drawable.remote_compose_art),
          contentDescription = "Remote Compose",
          modifier = Modifier.weight(0.8f).fillMaxSize(),
          contentScale = ContentScale.Fit,
        )
      }
    }
  }
