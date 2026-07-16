package slides

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import livewire_presentation.generated.resources.Res
import livewire_presentation.generated.resources.diagram_apis_1
import livewire_presentation.generated.resources.diagram_apis_2
import livewire_presentation.generated.resources.diagram_apis_3
import livewire_presentation.generated.resources.diagram_livewire_modifier
import livewire_presentation.generated.resources.diagram_revisiting_tree
import livewire_presentation.generated.resources.meme_serialization
import livewire_presentation.generated.resources.meme_trees
import net.kodein.cup.PreparedSlide
import net.kodein.cup.Slide
import net.kodein.cup.sa.SourceCode
import net.kodein.cup.sa.rememberSourceCode
import net.kodein.cup.speaker.SpeakerNotes
import org.jetbrains.compose.resources.painterResource
import widgets.GifImage
import widgets.Livewire
import widgets.LivewireCode
import widgets.LocalLivewireFonts
import widgets.TitledSlide
import widgets.dimmed

val composeUiLikeApis by
  Slide(
    stepCount = 3,
    context =
      SpeakerNotes(
        listOf(
          0..0 to
            "To add meaning to our tree, we need to cover our last requirement for " +
              "building a custom composition.\n\n" +
              "Tree node emitter, i.e our `@Composable` function APIs. These are the \"widgets\" " +
              "you already know and love.",
          1..1 to "> Just quickly talk / mention through these. Just here to drive the point.",
          2..2 to "We want writing Livewire UIs to feel JUST like writing regular Compose UIs.",
        )
      ),
  ) { step ->
    TitledSlide(title = "Compose UI-like APIs", kicker = "// BUILDING OUR APIS") {
      Crossfade(targetState = step, modifier = Modifier.fillMaxSize()) { s ->
        Image(
          painter =
            painterResource(
              when (s) {
                0 -> Res.drawable.diagram_apis_1
                1 -> Res.drawable.diagram_apis_2
                else -> Res.drawable.diagram_apis_3
              }
            ),
          contentDescription = "Livewire composable APIs compared to Compose UI",
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Fit,
        )
      }
    }
  }

val textNode by
  PreparedSlide(
    context =
      SpeakerNotes(
        "To do this we need to first extend the meaning of our tree nodes.\n\n" +
          "We create a new node/implementation that adds additional meaning and metadata to " +
          "describe a UI element that we want to render.\n\n" +
          "In this example, a `TextNode`, which has some text string it wants to display, a " +
          "color it wants to render in, and the style of text to render on screen.\n\nEasy!"
      )
  ) {
    val sourceCode =
      rememberSourceCode(language = "kotlin") {
        // language=kotlin
        """
        class TextNode(
          var text: String,
          var color: Color = Color.Unspecified,
          var style: TextStyle? = null,
        ) : LayoutNode()
        """
          .trimIndent()
      }

    slideContent {
      TitledSlide(title = "Create a new node", kicker = "// BUILDING OUR APIS") {
        LivewireCode(sourceCode)
      }
    }
  }

val createComposable by
  PreparedSlide(
    context =
      SpeakerNotes(
        "Next, we need to define a function that developers can call to emit this node in the " +
          "composition.\n\n" +
          "These almost one-to-one follow the composables you use today, minus some limitations " +
          "because of our idea."
      )
  ) {
    val sourceCode =
      rememberSourceCode(language = "kotlin") {
        // language=kotlin
        """
        @LivewireComposable
        @Composable
        fun Text(
          text: String,
          color: Color = Color.Unspecified,
          style: TextStyle? = null,
        ) // {}
        """
          .trimIndent()
      }

    slideContent {
      TitledSlide(title = "Create its Composable (API)", kicker = "// BUILDING OUR APIS") {
        LivewireCode(sourceCode)
      }
    }
  }

val emitToComposition by
  PreparedSlide(
    stepCount = 4,
    context =
      SpeakerNotes(
        listOf(
          0..0 to
            "Lastly, we need to tell the composition how to create and update our new " +
              "tree node.",
          1..1 to
            "Start with calling the `ReusableComposeNode` composable function. Giving it " +
              "the node type that you want to emit, and the `Applier` type that the composition " +
              "needs to handle it.",
          2..2 to
            "Next, define the factory or how the composition creates the concrete class " +
              "of your implementation.",
          3..3 to "Lastly, tell the composition HOW to update your node when its state changes",
        )
      ),
  ) {
    val sourceCode =
      rememberSourceCode(language = "kotlin") {
        val shell by marker(dimmed(0..3))
        val hash by marker(dimmed(1..3))
        val node by marker(dimmed(1..3))
        val generics by marker(dimmed(2, 3))
        val factory by marker(dimmed(1, 3))
        val updateBlock by marker(dimmed(1, 2))

        // language=kotlin
        """
        ${shell}fun Text(…) {${X}
        ${hash}  val compositeKeyHash = currentCompositeKeyHashCode.toLong()${X}
        ${node}  ReusableComposeNode<${X}${generics}TextNode, Applier<LayoutNode>${X}${node}>(${X}
        ${factory}    factory = { TextNode(text) },${X}
        ${updateBlock}    update = {
              init(compositeKeyHash, LayoutNode.SetCompositeKeyHash)
              set(color, TextNode.SetColor)
              update(text, TextNode.SetText)
              set(style, TextNode.SetStyle)
            },${X}
        ${hash}  )${X}
        ${shell}}${X}
        """
          .trimIndent()
      }

    slideContent { step ->
      TitledSlide(title = "Emit to the composition", kicker = "// BUILDING OUR APIS") {
        LivewireCode(sourceCode, step = step)
      }
    }
  }

private val fadedCodeTheme: net.kodein.cup.sa.SourceCodeTheme = { _ -> null }

val compositionUpdater by
  PreparedSlide(
    context =
      SpeakerNotes(
        "For this, there are 4 basic methods you can use in this scope depending on **when** you " +
          "want the composition to update your node:\n\n" +
          "`init` - This is called only once after the node is created, and no more\n\n" +
          "`set` - This is called after the node is created, and when its state (color in this " +
          "case) changes\n\n" +
          "`update` - This is called ONLY after the state changes and not on creation\n\n" +
          "`reconcile` - This is called after EVERY update to your node."
      )
  ) {
    val backgroundCode =
      rememberSourceCode(language = "kotlin", key = "updaterBg") {
        // language=kotlin
        """
        fun Text(…) {
          val compositeKeyHash = currentCompositeKeyHashCode.toLong()
          ReusableComposeNode<TextNode, Applier<LayoutNode>>(
            factory = { TextNode(text) },
            update = {



            },
          )
        }
        """
          .trimIndent()
      }
    val panelCode =
      rememberSourceCode(language = "kotlin", key = "updaterPanel") {
        // language=kotlin
        """
        update = {
          init(compositeKeyHash, {…}) // once, after create
          set(color, {…})             // create + change
          update(text, {…})           // change only
          reconcile {…}               // every pass
        }
        """
          .trimIndent()
      }

    slideContent {
      TitledSlide(title = "Composition Updater", kicker = "// BUILDING OUR APIS") {
        Box(Modifier.fillMaxSize()) {
          SourceCode(
            sourceCode = backgroundCode,
            style =
              TextStyle(
                fontFamily = LocalLivewireFonts.current.mono,
                fontSize = 9.sp,
                color = Color(0xFF434343),
              ),
            theme = fadedCodeTheme,
          )
          Box(
            modifier =
              Modifier.align(Alignment.Center)
                .background(Livewire.Background, RoundedCornerShape(6.dp))
                .padding(14.dp)
          ) {
            LivewireCode(panelCode, fontSize = 10.sp)
          }
        }
      }
    }
  }

val revisitingTree by
  Slide(
    context =
      SpeakerNotes(
        "Let's look back at our tree now that we've added more meaning to our nodes.\n\n" +
          "It's starting to take shape and look more like the description of a UI.\n\n" +
          "However, its still pretty limited. It lacks the metadata / meaning that make our UIs " +
          "truly beautiful and worth looking at. Stuff like padding, backgrounds, borders, and " +
          "more. The real JUICE of an interface.\n\n" +
          "But adding this kind of metadata to EVERY node type would quickly become tedious, " +
          "verbose, and brittle to changes. To solve this for Livewire we cribbed what Compose " +
          "UI does almost verbatim…"
      )
  ) {
    TitledSlide(title = "Revisiting our tree", kicker = "// BUILDING OUR APIS") {
      Image(
        painter = painterResource(Res.drawable.diagram_revisiting_tree),
        contentDescription = "The tree with typed nodes: TextNode, BoxNode, etc",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Fit,
      )
    }
  }

val livewireModifier by
  Slide(
    context =
      SpeakerNotes(
        "… which is to create a `Modifier` pattern!\n\n" +
          "This works wonderfully so why re-invent the world and it keeps us to our original " +
          "requirement of creating APIs that developers already know.\n\n" +
          "Aside from the name, this API in Livewire is *almost* one-for-one.\n\n" +
          "If you've ever worked with Jetpack Glance before, the compose wrapper around " +
          "RemoteView's, it does the same thing with `GlanceModifier`.\n\n" +
          "So how does this work with our custom compose setup?"
      )
  ) {
    TitledSlide(title = "LivewireModifier", kicker = "// BUILDING OUR APIS") {
      Image(
        painter = painterResource(Res.drawable.diagram_livewire_modifier),
        contentDescription = "LivewireModifier chain",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Fit,
      )
    }
  }

val updatingTheTree by
  PreparedSlide(
    stepCount = 1,
    context =
      SpeakerNotes(
        "First, we update our tree data structure. Adding the modifier as another piece of " +
          "metadata to the node."
      ),
  ) {
    val sourceCode =
      rememberSourceCode(language = "kotlin") {
        val rest by marker(dimmed(0))

        // language=kotlin
        """
        ${rest}// Our base tree structure
        abstract class LayoutNode {
          val children: MutableList<LayoutNode> = mutableListOf()
          var compositeKeyHash: Long = 0${X}
          var modifier: LivewireModifier = LivewireModifier
        ${rest}}

        // The root of every Livewire tree
        class RootNode : LayoutNode()${X}
        """
          .trimIndent()
      }

    slideContent { step ->
      TitledSlide(title = "Updating the tree", kicker = "// LIVEWIRE MODIFIER") {
        LivewireCode(sourceCode, step = step)
      }
    }
  }

val updatingOurApis by
  PreparedSlide(
    stepCount = 1,
    context =
      SpeakerNotes(
        "Then we update our `@Composable` APIs by adding it as the first defaulted parameter"
      ),
  ) {
    val sourceCode =
      rememberSourceCode(language = "kotlin") {
        val rest by marker(dimmed(0))

        // language=kotlin
        """
        ${rest}@LivewireComposable
        @Composable
        fun Text(
          text: String,${X}
          modifier: LivewireModifier = LivewireModifier,
        ${rest}  color: Color = Color.Unspecified,
          style: TextStyle? = null,
        ) // {}${X}
        """
          .trimIndent()
      }

    slideContent { step ->
      TitledSlide(title = "Updating our APIs", kicker = "// LIVEWIRE MODIFIER") {
        LivewireCode(sourceCode, step = step)
      }
    }
  }

val updatingTheComposition by
  PreparedSlide(
    stepCount = 1,
    context =
      SpeakerNotes(
        "Lastly, we update our emitter's update function to tell compose how to apply and update " +
          "it when it changes in the composition."
      ),
  ) {
    val sourceCode =
      rememberSourceCode(language = "kotlin") {
        val rest by marker(dimmed(0))

        // language=kotlin
        """
        ${rest}fun Text(…) {
          val compositeKeyHash = currentCompositeKeyHashCode.toLong()
          ReusableComposeNode<TextNode, Applier<LayoutNode>>(
            factory = { TextNode(text) },
            update = {
              init(compositeKeyHash, LayoutNode.SetCompositeKeyHash)${X}
              set(modifier, LayoutNode.SetModifier)
        ${rest}      set(color, TextNode.SetColor)
              update(text, TextNode.SetText)
              set(style, TextNode.SetStyle)
            },
          )
        }${X}
        """
          .trimIndent()
      }

    slideContent { step ->
      TitledSlide(title = "Updating the composition", kicker = "// LIVEWIRE MODIFIER") {
        LivewireCode(sourceCode, step = step)
      }
    }
  }

val creatingNewModifier by
  PreparedSlide(
    context =
      SpeakerNotes(
        "Very similar to how we add new nodes to our UI tree, we do the same for modifiers.\n\n" +
          "Just create a new implementation that defines the modifiers meaning and intent that " +
          "can be applied to all nodes its attached to.\n\n" +
          "Then define the extension function for adding it in a modifier chain when writing " +
          "your UI. The `then(…)` function adds it to the chain much in a similar way…"
      )
  ) {
    val sourceCode =
      rememberSourceCode(language = "kotlin") {
        // language=kotlin
        """
        @Serializable
        class BackgroundModifier(
          val color: Color,
          val shape: Shape? = null,
        ) : LivewireModifier.Element

        fun LivewireModifier.background(
          color: Color,
          shape: Shape,
        ): LivewireModifier = then(BackgroundModifier(color, shape))
        """
          .trimIndent()
      }

    slideContent {
      TitledSlide(title = "Creating a new modifier", kicker = "// LIVEWIRE MODIFIER") {
        LivewireCode(sourceCode)
      }
    }
  }

val allTrees by
  Slide(
    context =
      SpeakerNotes(
        "I told you, everything is a tree.\n\n" + "Modifiers, are just chains of binary trees"
      )
  ) {
    TitledSlide(title = "Wait? It's just all trees?", kicker = "// LIVEWIRE MODIFIER") {
      Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
        Image(
          painter = painterResource(Res.drawable.meme_trees),
          contentDescription = "Modifier chains are binary trees",
          modifier = Modifier.weight(1.1f).fillMaxSize(),
          contentScale = ContentScale.Fit,
        )
        Spacer(Modifier.width(16.dp))
        GifImage(
          path = "files/always_has_been.gif",
          contentDescription = "Always has been",
          modifier = Modifier.weight(0.9f).fillMaxWidth(),
        )
      }
    }
  }

val serialization by
  PreparedSlide(
    context =
      SpeakerNotes(
        "KSP plugin with polymorphic serializer?\n\n" +
          "Kotlinx serialization let us migrate from JSON to protos"
      )
  ) {
    val sourceCode =
      rememberSourceCode(language = "kotlin") {
        // language=kotlin
        """
        @Serializable
        abstract class LayoutNode //{}

        @Serializable
        class BoxNode(…) //{}

        @Serializable
        class ButtonNode(…) //{}

        @Serializable
        class SwitchNode(…) //{}

        // and many more!
        """
          .trimIndent()
      }

    slideContent {
      TitledSlide(title = "Serialization", kicker = "// BUILDING OUR APIS") {
        Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
          LivewireCode(sourceCode, modifier = Modifier.weight(1f))
          Spacer(Modifier.width(16.dp))
          Image(
            painter = painterResource(Res.drawable.meme_serialization),
            contentDescription = null,
            modifier = Modifier.weight(0.8f).fillMaxSize(),
            contentScale = ContentScale.Fit,
          )
        }
      }
    }
  }
