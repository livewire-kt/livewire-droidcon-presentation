package slides

import net.kodein.cup.Slide
import net.kodein.cup.speaker.SpeakerNotes
import widgets.Bullet
import widgets.Livewire
import widgets.SectionSlide
import widgets.TitledSlide
import widgets.line

val sectionProblem by Slide {
  SectionSlide(
    number = "01",
    title = "The Problem",
    subtitle = "The storied history of application debugging",
  )
}

val sidecarDebugging by
  Slide(
    context =
      SpeakerNotes(
        "The IDE can show a HashMap with 47 entries; it can't show 'the third cart item has a stale " +
          "price.' Domain-shaped debugging needs app code cooperating with a tool. Stetho and " +
          "Flipper are the side-cars everyone used."
      )
  ) {
    TitledSlide(title = "Side-car debugging", kicker = "// THE PROBLEM") {
      Bullet(
        line {
          t("The debugger shows you your app's ")
          em("memory")
          t(". It can't show you your app's ")
          em("meaning")
          t(".")
        }
      )
      Bullet(
        line {
          t(
            "Logcat, layout inspector, network profiler, etc are generic tools with no domain knowledge"
          )
        }
      )
      Bullet(
        line {
          t("What you actually want to see: ")
          i("your")
          t(" cart, ")
          i("your")
          t(" sync queue, ")
          i("your")
          t(" feature flags")
        }
      )
      Bullet(
        line {
          t("The right idea has always been a ")
          em("side-car")
          t(": a desktop tool the app talks to")
        }
      )
    }
  }

val stetho by
  Slide(
    context =
      SpeakerNotes(
        "Stetho had a neat trick: speak the chrome devtools protocol. Piggybacking on this was " +
          "clever, Facebook borrowed a very polished inspector.\n\n" +
          "But custom domain plugins were second-class (dumpapp = a command line).\n\n" +
          "As a firefox user, the lack of dedicated app always annoyed me too.\n\n" +
          "It quietly stopped being maintained years before the archive."
      )
  ) {
    TitledSlide(title = "Stetho (2015–†)", kicker = "// THE PROBLEM") {
      Bullet(line { t("Facebook, 2015") })
      Bullet(
        line {
          code("chrome://inspect", color = Livewire.Amber)
          t(" → your app's network, database, view tree, etc")
        }
      )
      Bullet(
        line {
          code("dumpapp", color = Livewire.Amber)
          t(" for custom plugins — CLI only, no UI")
        }
      )
      Bullet(
        line {
          t("Unmaintained for years, repo archived. ")
          red("He's dead, Jim")
        }
      )
    }
  }

val flipper by
  Slide(
    context =
      SpeakerNotes(
        "Flipper fixed Stetho's UI problem and introduced another one: every custom plugin is two " +
          "codebases in two languages with a hand-rolled JSON contract nothing checks.\n\n" +
          "Artisanal JSON?\n\n" +
          "Data model changes silently break the desktop half."
      )
  ) {
    TitledSlide(title = "Flipper (2019–†?)", kicker = "// THE PROBLEM") {
      Bullet(
        line {
          t("Facebook's successor: real desktop app with a ")
          em("plugin API!")
        }
      )
      Bullet(line { t("Custom Plugins!") })
      Bullet(line { t("Desktop half installed per-developer from a marketplace") })
      Bullet(line { t("Android & iOS only, no desktop client.") })
      Bullet(
        line {
          t("Now ")
          red("deprecated & archived")
        }
      )
    }
  }

val wantsAndDesires by
  Slide(context = SpeakerNotes("a recomposition plugin for Flipper sounds like a nightmare")) {
    TitledSlide(title = "Wants & desires", kicker = "// THE PROBLEM") {
      Bullet(
        line {
          em("Kotlin Multiplatform")
          t(": Android, iOS, and desktop clients (+ web?)")
        }
      )
      Bullet(
        line {
          t("Plugins ")
          em("written once, in the app")
          t(": nothing to install on the desktop")
        }
      )
      Bullet(
        line {
          em("Easy distribution")
          t(": every developer on the team gets plugins and updates automatically")
        }
      )
      Bullet(
        line {
          em("Type-safe")
          t(" end to end: change a model, the compiler tells you")
        }
      )
      Bullet(
        line {
          em("Compose-native")
          t(": good enough to build a recomposition inspector")
        }
      )
    }
  }
