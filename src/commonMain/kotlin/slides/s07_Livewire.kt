package slides

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bugs.StagedBugs
import livewire_presentation.generated.resources.Res
import livewire_presentation.generated.resources.jc
import livewire_presentation.generated.resources.screenshot_demo_1
import livewire_presentation.generated.resources.screenshot_demo_2
import livewire_presentation.generated.resources.screenshot_demo_3
import livewire_presentation.generated.resources.screenshot_demo_4
import livewire_presentation.generated.resources.screenshot_demo_5
import livewire_presentation.generated.resources.screenshot_recomposition
import net.kodein.cup.PreparedSlide
import net.kodein.cup.Slide
import net.kodein.cup.sa.rememberSourceCode
import net.kodein.cup.speaker.SpeakerNotes
import net.kodein.cup.utils.plus
import org.jetbrains.compose.resources.painterResource
import widgets.Bullet
import widgets.CodeBox
import widgets.LivewireCode
import widgets.LivewireWire
import widgets.PaceKeyframe
import widgets.SectionSlide
import widgets.TitledSlide
import widgets.VideoPlayer
import widgets.line
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

val sectionLivewire by
  Slide(
    context =
      SpeakerNotes(
        "ERIC:\nWe've talked a lot about what we've built, but we haven't actually shown you a single " +
          "thing yet."
      ) + PaceKeyframe(31.minutes + 45.seconds)
  ) {
    SectionSlide(number = "05", title = "Livewire", subtitle = "Live Demos (also not live demos)")
  }

val demoScreens by
  Slide(
    stepCount = 5,
    context =
      SpeakerNotes(
        listOf(
          0..0 to "ERIC:\nSo here's the main screen. You can see here we've launched it with 3 open devices.\n\nAn Android emulator on top.\n\nAn iPhone in the middle\n\nAnd a desktop app running on the bottom.\n\nThese all have the same logo cause they're all instances our demo app.",
          1..1 to
            "DREW:\nHere's our database plugin. Run custom queries. View schemas. View tables. Database queries through the same SQL driver your app uses.",
          2..2 to
            "DREW:\nNetwork: one line in your OkHttp/Ktor setup; requests, timings, headers, " +
              "formatted bodies, even images render. It reads the app's traffic because it IS " +
              "the app.",
          3..3 to
            "ERIC:\nRecomposition plugin, which is what I'm personally most proud of.\n\n" +
              "It shows your composition tree, with recomposition, skip, and child " +
              "recomposition counts for each node. We collapse boring stuff into breadcrumbs, " +
              "like the Box > NavigationBar > Surface group above.",
          4..4 to
            "ERIC:\nWhen you select a node, it tells you WHY it invalidated (if it can) - which " +
              "state changed, and its value. Also shows current parameters.\n\n" +
              "Note that while the plugin does work on every platform, it's not able to give " +
              "quite as much detail on iOS due to kotlin/native's lack of reflection.",
        )
      ),
  ) { step ->
    Crossfade(targetState = step, modifier = Modifier.fillMaxSize().padding(8.dp)) { s ->
      Image(
        painter =
          painterResource(
            when (s) {
              0 -> Res.drawable.screenshot_demo_1
              1 -> Res.drawable.screenshot_demo_2
              2 -> Res.drawable.screenshot_demo_3
              3 -> Res.drawable.screenshot_demo_4
              else -> Res.drawable.screenshot_demo_5
            }
          ),
        contentDescription = "Livewire host app screenshots",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Fit,
      )
    }
  }

val introspection by
  Slide(context = SpeakerNotes(
    """
      ERIC:
      One funny thing we discovered while developing this is that it's REALLY easy to get ourselves into an infinite recomposition loop.

      The recomposition inspector is Compose…
      …streamed over a composition…
      …watching your compositions.

      We got around this by ignorelisting the tracker's own composition, and thus contained our turtles.
    """.trimIndent()
  )) {
    TitledSlide(title = "Introspection isn't always a good thing", kicker = "// PLUGINS") {
      Box(Modifier.fillMaxSize()) {
        Column {
          Text("""
            The recomposition inspector is Compose…
            …streamed over a composition…
            …watching your compositions.
          """.trimIndent())
          Spacer(Modifier.height(8.dp))
          Bullet(line { t("Livewire's own composition\nis tagged with a context\nmarker") })
          Bullet(line { t("The tracker ignorelists itself\nso it doesn't observe its own\nemissions, forever") })
          Bullet(line { t("Turtles: contained 🐢") })
        }
        Image(
          painter = painterResource(Res.drawable.screenshot_recomposition),
          contentDescription = "Recomposition inspector inspecting itself",
          modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 20.dp).height(165.dp),
          contentScale = ContentScale.FillHeight,
        )
      }
    }
  }

val clientSetup by PreparedSlide {
  val sourceCode =
    rememberSourceCode(language = "kotlin") {
      // language=kotlin
      """
        val livewire = LivewireClient {
          // Customize the host theme
          theme(CustomLivewireTheme)

          // Customize the plugins
          install(DatabasePlugin(context))
          install(NetworkPlugin())
          install(RecompositionPlugin())
        }

        // Listen and connect to the host
        livewire.start()
        """
        .trimIndent()
    }

  slideContent {
    TitledSlide(title = "Setting up the client", kicker = "// USING LIVEWIRE") {
      CodeBox(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
      ) {
        LivewireCode(
          sourceCode = sourceCode,
          fontSize = 12.sp,
        )
      }
    }
  }
}

val pluginApi by PreparedSlide {
  val sourceCode =
    rememberSourceCode(language = "kotlin") {
      // language=kotlin
      """
        interface Plugin {

          // id, title, icon for the host's drawer
          val info: PluginInfo

          // Your debug UI
          @LivewireComposable
          @Composable
          fun Content()
        }
        """
        .trimIndent()
    }

  slideContent {
    TitledSlide(title = "Plugin API", kicker = "// CUSTOMIZING LIVEWIRE") {
      CodeBox(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
      ) {
        LivewireCode(
          sourceCode = sourceCode,
          fontSize = 12.sp,
        )
      }
    }
  }
}

val pluginInfo by PreparedSlide {
  val sourceCode =
    rememberSourceCode(language = "kotlin") {
      // language=kotlin
      """
        class DemoPlugin : Plugin {

          override val info: PluginInfo = PluginInfo(
            pluginId = "com.demo.livewire",  // Unique Id
            title = "Livewire Demo",         // Display name
            icon = Icons.Rounded.Demo,       // ImageVector
          )

          //…
        }
        """
        .trimIndent()
    }

  slideContent {
    TitledSlide(title = "Plugin info", kicker = "// CUSTOMIZING LIVEWIRE") {
      CodeBox(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
      ) {
        LivewireCode(
          sourceCode = sourceCode,
          fontSize = 12.sp,
        )
      }
    }
  }
}

val pluginContent by PreparedSlide {
  val sourceCode =
    rememberSourceCode(language = "kotlin") {
      // language=kotlin
      """
        class DemoPlugin(val demoRepository: DemoRepository) : Plugin {

          // val info: PluginInfo = …

          @Composable
          override fun Content() {
            Column {
              Text("Hello, Droidcon!")
              Button(
                action = clickAction { demoRepository.doWave() },
              ) {
                Text("Wave")
              }
            }
          }
        }
        """
        .trimIndent()
    }

  slideContent {
    TitledSlide(title = "Plugin content", kicker = "// CUSTOMIZING LIVEWIRE") {
      CodeBox(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
      ) {
        LivewireCode(
          sourceCode = sourceCode,
          fontSize = 12.sp,
        )
      }
    }
  }
}

val demoTime by
  Slide(
    context =
      SpeakerNotes(
        "DREW:\nBEAT 1 — the clicker is \"dead\" on this slide: navigation keys are swallowed while " +
          "we're here. Click a few times, look puzzled, then pull out the phone: " +
          "Livewire → Slide Deck → next step. The remote path never touches the keyboard. " +
          "Optional flourish before advancing: Deck Doctor → turn OFF \"HID event " +
          "coalescing\" and show the clicker coming back to life.\n\n" +
          "Panic button: K on the laptop clears every staged bug."
      ) + StagedBugs(swallowKeys = true)
  ) {
    TitledSlide(title = "One more thing — a live demo", kicker = "// DEMO") {
      Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Bullet(line { t("This deck is a Compose app") })
        Bullet(line { t("Livewire is compiled into it") })
        Bullet(line { em("What could possibly go wrong?") })
      }
    }
  }

val stateSurgery by
  Slide(
    context =
      SpeakerNotes(
        "DREW:\nBEAT 2 — deck surgery, live from the phone. Deck Doctor is a ~40-line plugin " +
          "whose controls are bound straight to the deck's snapshot state.\n\n" +
          "Suggested run, all from the phone: drag \"Substrate lattice gain\" up and down " +
          "— the background dots breathe on every window. Flip \"Telemetry beacon\" — the " +
          "pace battery vanishes and returns. Then WIRE PHYSICS on the wire below: crank " +
          "\"Corona discharge rate\" to 400, \"Arc energy\" to ×3 (fireworks), drag " +
          "\"Carrier drift velocity\" to 0 (charge freezes mid-wire) then to ×4, and slam " +
          "\"Gravitational coupling\" so the sparks rain. Finale: \"Photon output " +
          "limiter\" ON — the projector goes black while the speaker window keeps running " +
          "— then OFF.\n\n" +
          "Land the point: no screenshots, no special demo build. The phone dispatches " +
          "actions over the wire into the same snapshot state this deck renders from. " +
          "Turtle one-liner on the way out: the tool inspecting the deck is itself " +
          "streamed Compose. Panic: K restores every visual default."
      )
  ) {
    TitledSlide(title = "The deck is just state", kicker = "// DEMO") {
      Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
          Row {
            Column {
              Bullet(
                line {
                  t("Those dots behind me? ")
                  em("Snapshot state")
                }
              )
              Bullet(
                line {
                  t("The little battery in the corner? ")
                  em("Snapshot state")
                }
              )
              Bullet(
                line {
                  t("The live…wire below? ")
                  em("Snapshot state")
                }
              )
            }
            Spacer(Modifier.weight(1f))
            LivewireQrCode(
              title = "ParticleEmitter",
              url = "https://github.com/PiotrPrus/ParticleEmitter",
              logo = painterResource(Res.drawable.jc),
              initialVisibility = true,
            )
          }
          Spacer(Modifier.weight(1f))
          LivewireWire(
            modifier =
              Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(horizontal = 6.dp)
          )
          Text(
            line {
              b("ParticleEmitter by Piotr Prus")
            },
            color = Color.White,
            fontSize = 10.sp,
          )
        }

      }
    }
  }

val outroGif by Slide {
  VideoPlayer(
    path = "files/outro.mp4",
    fallbackGifPath = "files/outro.gif",
    modifier = Modifier.fillMaxSize().padding(8.dp),
  )
}
