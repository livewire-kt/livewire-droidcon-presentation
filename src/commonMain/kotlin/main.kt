import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import net.kodein.cup.Presentation
import net.kodein.cup.SLIDE_SIZE_16_9
import net.kodein.cup.SlideGroup
import net.kodein.cup.SlideSpecs
import net.kodein.cup.Slides
import net.kodein.cup.TransitionSet
import net.kodein.cup.cupApplication
import net.kodein.cup.imgexp.imageExport
import net.kodein.cup.insideTransitionSpecs
import net.kodein.cup.laser.laser
import net.kodein.cup.overview.overview
import net.kodein.cup.speaker.speakerWindow
import net.kodein.cup.speaker.windowManagement
import org.kodein.emoji.compose.EmojiService
import slides.actions
import slides.agenda
import slides.allTrees
import slides.androidAdb
import slides.applier
import slides.backpressure
import slides.buildOurOwnTree
import slides.clickingBackwards
import slides.clientSetup
import slides.composeIsFun
import slides.composeRemotely
import slides.composeRemotelyQ
import slides.composeUiLikeApis
import slides.composition
import slides.compositionUpdater
import slides.createComposable
import slides.creatingNewModifier
import slides.customComposition
import slides.declaringIntention
import slides.definingIntention
import slides.deliveringAction
import slides.demoScreens
import slides.desktopEasy
import slides.diffing
import slides.discovery
import slides.discoveryDiagram
import slides.e2eEncryption1
import slides.e2eEncryption2
import slides.emitToComposition
import slides.flipIt
import slides.flipper
import slides.growingTheTree
import slides.hostIsServer
import slides.introspection
import slides.iosSimulator
import slides.livewireModifier
import slides.otherTalks
import slides.outroGif
import slides.physicalIos1
import slides.physicalIos2
import slides.physicalIos3
import slides.pluginApi
import slides.pluginContent
import slides.pluginInfo
import slides.pluginScreens1
import slides.pluginScreens2
import slides.remoteComposeNo
import slides.remoteComposeYes
import slides.renderingNode
import slides.renderingOnHost
import slides.renderingTree
import slides.revisitingTree
import slides.sectionConnections
import slides.sectionIdea
import slides.sectionLivewire
import slides.sectionProblem
import slides.sectionReassembly
import slides.serialization
import slides.sidecarDebugging
import slides.stayingConnected
import slides.stetho
import slides.textNode
import slides.thankYou
import slides.theTree
import slides.title
import slides.twoPhases
import slides.uiVsRuntime
import slides.updatingOurApis
import slides.updatingTheComposition
import slides.updatingTheTree
import slides.wantsAndDesires
import widgets.DotGridBackground
import widgets.LivewireTheme

fun main() =
  cupApplication(title = "Livewire — Droidcon '26") {
    remember { EmojiService.initialize() }

    LivewireTheme {
      Presentation(
        slides = presentationSlides,
        configuration = {
          windowManagement()
          laser()
          speakerWindow()
          imageExport()
          overview()
          defaultSlideSpecs =
            SlideSpecs(
              size = SLIDE_SIZE_16_9,
              startTransitions = TransitionSet.moveHorizontal(LayoutDirection.Ltr),
              endTransitions = TransitionSet.moveHorizontal(LayoutDirection.Ltr),
            )
        },
        backgroundColor = Color(0xFF0B0E11),
      ) { slidesContent ->
        DotGridBackground()
        slidesContent()
      }
    }
  }

private fun section(vararg slides: SlideGroup) =
  Slides(
    *slides,
    specs = {
      it.insideTransitionSpecs(
        startTransitions = TransitionSet.fade,
        endTransitions = TransitionSet.fade,
      )
    },
  )

val presentationSlides =
  Slides(
    section(title, agenda),
    section(sectionProblem, sidecarDebugging, stetho, flipper, wantsAndDesires),
    section(
      sectionIdea,
      flipIt,
      composeIsFun,
      composeRemotely,
      remoteComposeYes,
      remoteComposeNo,
      composeRemotelyQ,
      uiVsRuntime,
      otherTalks,
      buildOurOwnTree,
      customComposition,
      theTree,
      applier,
      composition,
      growingTheTree,
      composeUiLikeApis,
      textNode,
      createComposable,
      emitToComposition,
      compositionUpdater,
      revisitingTree,
      livewireModifier,
      updatingTheTree,
      updatingOurApis,
      updatingTheComposition,
      creatingNewModifier,
      allTrees,
      serialization,
    ),
    section(
      sectionConnections,
      hostIsServer,
      twoPhases,
      discovery,
      discoveryDiagram,
      desktopEasy,
      iosSimulator,
      androidAdb,
      physicalIos1,
      physicalIos2,
      physicalIos3,
      e2eEncryption1,
      e2eEncryption2,
      stayingConnected,
    ),
    section(
      sectionReassembly,
      renderingOnHost,
      renderingTree,
      renderingNode,
      diffing,
      backpressure,
      actions,
      definingIntention,
      declaringIntention,
      clickingBackwards,
      deliveringAction,
    ),
    section(
      sectionLivewire,
      demoScreens,
      introspection,
      clientSetup,
      pluginApi,
      pluginInfo,
      pluginContent,
      pluginScreens1,
      pluginScreens2,
      outroGif,
    ),
    section(thankYou),
  )
