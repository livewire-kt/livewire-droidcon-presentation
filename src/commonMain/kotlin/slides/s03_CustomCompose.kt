package slides

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import livewire_presentation.generated.resources.Res
import livewire_presentation.generated.resources.diagram_growing_tree
import net.kodein.cup.PreparedSlide
import net.kodein.cup.Slide
import net.kodein.cup.sa.rememberSourceCode
import net.kodein.cup.speaker.SpeakerNotes
import org.jetbrains.compose.resources.painterResource
import widgets.Bullet
import widgets.CodeBox
import widgets.LivewireCode
import widgets.TitledSlide
import widgets.dimmed
import widgets.line

val customComposition by
  Slide(
    context =
      SpeakerNotes(
        "DREW:\n" +
        "What does it take to actually create a custom composition?\n\n" +
          "You need to define your own custom tree data structure. This will hold all the " +
          "metadata and information we need to describe UIs and intentions.\n\n" +
          "You need an implementation of Compose's `Applier` interface. This basically tells " +
          "Compose how to manipulate your custom tree data structure.\n\n" +
          "You need to create a Composition, i.e. the compose engine, with your applier and the " +
          "root of your tree. This is responsible for creating and updating your tree data " +
          "structure.\n\n" +
          "Lastly, you need \"Node emitters/updaters\". This is basically your `@Composable` " +
          "apis. For Livewire these are re-implementations of the functions you know and love.\n\n" +
          "Okay, we know what we need to build so lets start with our tree."
      )
  ) {
    TitledSlide(title = "Create a custom composition", kicker = "// CUSTOM COMPOSE") {
      Bullet(line { t("A custom tree structure") })
      Bullet(line { t("A custom Applier for your tree") })
      Bullet(line { t("A custom Composition") })
      Bullet(
        line {
          t("Custom tree node emitters (")
          i("i.e. Your own @Composable functions")
          t(")")
        }
      )
    }
  }

val theTree by
  PreparedSlide(
    stepCount = 4,
    context =
      SpeakerNotes(
        listOf(
          0..0 to
            "DREW:\n" +
            "If we hearken back to our data structures class. A tree is essentially just " +
              "a class with a list of itself and some metadata to describe each node.\n\n" +
              "The core of Livewire is just this:",
          1..1 to "DREW:\nWe have an abstract `LayoutNode` to describe each layout element.",
          2..2 to "DREW:\nIt has a list of children, i.e. more `LayoutNodes`",
          3..3 to
            "DREW:\nThen we define its first concrete implementation, the `RootNode`. Which is " +
              "the \"**ROOT**\" of every livewire tree in our composition.",
        )
      ),
  ) {
    val sourceCode =
      rememberSourceCode(language = "kotlin") {
        val rest by marker(dimmed(1..3))
        val decl by marker(dimmed(2..3))
        val children by marker(dimmed(1, 3))
        val root by marker(dimmed(1..2))

        // language=kotlin
        """
        ${rest}// Our base tree structure${X}
        ${decl}abstract class LayoutNode {${X}
        ${children}  val children: MutableList<LayoutNode> = mutableListOf()${X}
        ${rest}  var compositeKeyHash: Long = 0
        }

        // The root of every Livewire tree${X}
        ${root}class RootNode : LayoutNode()${X}
        """
          .trimIndent()
      }

    slideContent { step ->
      TitledSlide(title = "The tree", kicker = "// CUSTOM COMPOSE") {
        CodeBox(
          modifier = Modifier.fillMaxSize(),
        ) {
          LivewireCode(sourceCode, step = step)
        }
      }
    }
  }

val applier by
  PreparedSlide(
    stepCount = 3,
    context =
      SpeakerNotes(
        listOf(
          0..0 to
            "DREW:\nNext! We need an applier, or a way to tell Compose how to manipulate our " +
              "tree.\n\n" +
              "In its simplest form you just implement `AbstractApplier`, giving it your tree's " +
              "Type, and seeding it with the root or an empty tree.",
          1..1 to
            "DREW:\nThe first methods in an applier are how it inserts nodes into your tree. " +
              "There are two different approaches, but **ONLY ONE** should be used.\n\n" +
              "`insertTopDown` is called before the children of instance have been created and " +
              "inserted into it. `insertBottomUp` is called after all children have been created " +
              "and inserted.\n\n" +
              "Which one you use is usually dictated by our needs / performance. For **top-down** " +
              "if you have a tree where all parents up to the root are notified of every child " +
              "added, then the notifications grow exponentially. For this **bottom-up** would be " +
              "linear and better. For **bottom-up** if you have a tree where all children are " +
              "notified when a parent is added the inverse is true.\n\n" +
              "For livewire we followed the approach that the `UiApplier` takes and use " +
              "`insertBottomUp` to prevent duplicate notifications.",
          2..2 to
            "DREW:\nLastly, are just the remaining tree manipulation methods for removing, " +
              "moving, and clearing nodes in your tree.\n\n" +
              "There are more functions to override here for more complicated behavior, but you " +
              "get the gist.",
        )
      ),
  ) {
    val sourceCode =
      rememberSourceCode(language = "kotlin") {
        val shell by marker(dimmed(1..2))
        val inserts by marker(dimmed(2))
        val rest by marker(dimmed(1))

        // language=kotlin
        """
        ${shell}class LivewireApplier(
          root: LayoutNode,
        ) : AbstractApplier<LayoutNode>(root) {${X}
        ${inserts}  override fun insertTopDown(index: Int, instance: LayoutNode) {}
          override fun insertBottomUp(index: Int, instance: LayoutNode) {}${X}
        ${rest}  override fun remove(index: Int, count: Int) {}
          override fun move(from: Int, to: Int, count: Int) {}
          override fun onClear() {}${X}
        ${shell}}${X}
        """
          .trimIndent()
      }

    slideContent { step ->
      TitledSlide(title = "Applier", kicker = "// CUSTOM COMPOSE") {
        CodeBox(
          modifier = Modifier.fillMaxSize(),
        ) {
          LivewireCode(sourceCode, step = step)
        }
      }
    }
  }

val composition by
  PreparedSlide(
    stepCount = 5,
    context =
      SpeakerNotes(
        listOf(
          0..0 to
            "DREW:\nNext, is building the Composition. This example is simplified for brevity of " +
              "our slides, but we pretty much cribbed the setup from **Molecule**, so go checkout " +
              "that library for another example of this setup.",
          1..1 to "DREW:\nFirst, we pass in a `RootNode` to seed our composition with",
          2..2 to "DREW:\nThen, we create an instance of our `Applier` with our `RootNode`",
          3..3 to
            "DREW:\nNext, we create the Recomposer object so we can power recompositions in our " +
              "custom composition. (*go check out those other talks for more on this*)",
          4..4 to
            "DREW:\nLastly, we create our Composition, run the recomposer, and give it the " +
              "content that will be used to build our tree and define our UI.\n\n" +
              "This, again, is overly simplified and our real implementation has some more " +
              "boilerplate that essentially emits our `LayoutNode` tree when its updated so that " +
              "we can send it across the wire.",
        )
      ),
  ) {
    val sourceCode =
      rememberSourceCode(language = "kotlin") {
        val shell by marker(dimmed(1..4))
        val root by marker(dimmed(2..4))
        val applier by marker(dimmed(1, 3, 4))
        val recomposer by marker(dimmed(1, 2, 4))
        val finish by marker(dimmed(1..3))

        // language=kotlin
        """
        ${shell}fun CoroutineScope.launchLivewire(${X}
        ${root}  rootNode = RootNode(),${X}
        ${shell}  body: @Composable () -> Unit,
        ) {${X}
        ${applier}  val livewireApplier = LivewireApplier(rootNode)${X}
        ${recomposer}  val recomposer = Recomposer(coroutineContext)${X}
        ${finish}  val composition = Composition(livewireApplier, recomposer)${X}

        ${shell}  // … recomposer.runRecomposeAndApplyChanges() …${X}

        ${finish}  composition.setContent(body)${X}
        ${shell}}${X}
        """
          .trimIndent()
      }

    slideContent { step ->
      TitledSlide(title = "Composition", kicker = "// CUSTOM COMPOSE") {
        CodeBox(
          modifier = Modifier.fillMaxSize(),
        ) {
          LivewireCode(sourceCode, step = step)
        }
      }
    }
  }

val growingTheTree by
  Slide(
    context =
      SpeakerNotes(
        "DREW:\nLet's take a look at the sapling that is our tree so far. Visually, it would look " +
          "something like this.\n\n" +
          "However, that doesn't really **describe** a UI. We have a tree, but it doesn't really " +
          "tell us anything."
      )
  ) {
    TitledSlide(title = "Growing the tree", kicker = "// CUSTOM COMPOSE") {
      Image(
        painter = painterResource(Res.drawable.diagram_growing_tree),
        contentDescription = "The tree so far: a RootNode with LayoutNode children",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Fit,
      )
    }
  }
