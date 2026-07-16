package slides

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import livewire_presentation.generated.resources.Res
import livewire_presentation.generated.resources.diagram_actions
import livewire_presentation.generated.resources.diagram_rendering_tree
import net.kodein.cup.PreparedSlide
import net.kodein.cup.Slide
import net.kodein.cup.sa.rememberSourceCode
import net.kodein.cup.speaker.SpeakerNotes
import org.jetbrains.compose.resources.painterResource
import widgets.Bullet
import widgets.CodeBox
import widgets.LivewireCode
import widgets.SectionSlide
import widgets.TitledSlide
import widgets.dimmed
import widgets.line
import kotlin.time.Duration.Companion.minutes
import widgets.PaceKeyframe
import net.kodein.cup.utils.plus

val sectionReassembly by
  Slide(
    context =
      SpeakerNotes(
        "Now we've established how we create our UIs… How we established a connection between a " +
          "device and our desktop companion app… How do we now bring it all together."
      ) + PaceKeyframe(28.minutes)
  ) {
    SectionSlide(number = "04", title = "Re-assembly", subtitle = "Not all parts included.")
  }

val renderingOnHost by
  PreparedSlide(
    stepCount = 1,
    context =
      SpeakerNotes(
        "In the host app, we can access a Flow of our `LayoutNode` trees from the connection and " +
          "collect them in our desktop compose.\n\n" +
          "From there we feed it into a \"Router\" composable that can recursively direct and " +
          "render the node and its children based on type."
      ),
  ) {
    val sourceCode =
      rememberSourceCode(language = "kotlin") {
        val shell by marker(dimmed(0))

        // language=kotlin
        """
        ${shell}HostScaffold(…) {${X}
          val layoutNode by host.connection
            .incomingLayoutNodes
            .collectAsState()

          LayoutNodeContent(
            node = layoutNode,
            modifier = Modifier.fillMaxSize(),
          )
        ${shell}}${X}
        """
          .trimIndent()
      }

    slideContent { step ->
      TitledSlide(title = "Rendering on the host", kicker = "// HOST RENDERING") {
        CodeBox(
          modifier = Modifier.fillMaxSize()
        ) {
          LivewireCode(sourceCode, step = step)
        }
      }
    }
  }

val renderingTree by
  PreparedSlide(
    context =
      SpeakerNotes(
        "Structurally our tree looks something like this, and then the router on the right here " +
          "handles individual nodes like so…"
      )
  ) {
    val sourceCode =
      rememberSourceCode(language = "kotlin") {
        // language=kotlin
        """
        @Composable
        fun LayoutNodeContent(
          node: LayoutNode,
          modifier: Modifier,
        ) {
          when (node) {
            is BoxNode -> BoxNodeContent(node, modifier)
            is ColumnNode -> ColumnNodeContent(node, modifier)
            is RowNode -> RowNodeContent(node, modifier)
            is TextNode -> TextNodeContent(node, modifier)
            is ButtonNode -> ButtonNodeContent(node, modifier)
            // … many more!
          }
        }
        """
          .trimIndent()
      }

    slideContent {
      TitledSlide(title = "Rendering our tree", kicker = "// HOST RENDERING") {
        Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
          Image(
            painter = painterResource(Res.drawable.diagram_rendering_tree),
            contentDescription = "The LayoutNode tree structure",
            modifier = Modifier.weight(0.8f).fillMaxSize(),
            contentScale = ContentScale.Fit,
          )
          Spacer(Modifier.width(16.dp))
          CodeBox(
            modifier = Modifier.fillMaxSize().weight(1.2f)
          ) {
            LivewireCode(sourceCode)
          }
        }
      }
    }
  }

val renderingNode by
  PreparedSlide(
    stepCount = 3,
    context =
      SpeakerNotes(
        listOf(
          0..0 to "For layout and other nodes can can handle children iterate through them…",
          1..1 to
            "… then keying each children to the composition key hash generated from the " +
              "client so that the client stays the source of truth in this \"dual-composition\" " +
              "kind of setup",
          2..2 to
            "Then fold their LivewireModifier into a Compose UI modifier, and then feed " +
              "each child back through the router.",
        )
      ),
  ) {
    val sourceCode =
      rememberSourceCode(language = "kotlin") {
        val outer by marker(dimmed(1..2))
        val keyed by marker(dimmed(2))
        val folded by marker(dimmed(1))

        // language=kotlin
        """
        ${outer}@Composable
        fun BoxNodeContent(
          node: BoxNode,
          modifier: Modifier = Modifier,
        ) {
          Box(
            modifier = modifier.debugFrame(),
            //…
          ) {
            node.children.forEach { child ->${X}
              ${keyed}key(child.compositeKeyHash) {${X}
        ${folded}        val modifier = with(child.modifier) { this@Box.toComposeUi(Modifier) }
                LayoutNodeContent(child, modifier)${X}
              ${keyed}}${X}
        ${outer}    }
          }
        }${X}
        """
          .trimIndent()
      }

    slideContent { step ->
      TitledSlide(title = "Rendering a node", kicker = "// HOST RENDERING") {
        CodeBox(
          modifier = Modifier.fillMaxSize()
        ) {
        LivewireCode(sourceCode, step = step)
          }
      }
    }
  }

val diffing by
  PreparedSlide(
    context =
      SpeakerNotes(
        "The compose runtime hands us structural diffs. Its insert/remove/move calls ARE the " +
          "diff.\n\n" +
          "Our first frame ends up being a few kb, per-frame patches are generally tens of " +
          "bytes.\n\n" +
          "The host can send a RequestFullTree message to the client in the event it ever " +
          "loses context (maybe it missed a frame?)"
      )
  ) {
    val sourceCode =
      rememberSourceCode(language = "kotlin") {
        // language=kotlin
        """
        sealed class LayoutNodePatch {
          class InsertAt(parentNodeId, index, node)
          class RemoveAt(parentNodeId, index, count)
          class Move(parentNodeId, from, to, count)
          class Clear(nodeId)
          class UpdateNode(nodeId, propertyBytes)
        }
        """
          .trimIndent()
      }

    slideContent {
      TitledSlide(title = "Composition over the wire", kicker = "// DIFFING") {
        Bullet(
          line {
            t(
              "First frame serializes the full tree, after that the Applier records what the runtime "
            )
            i("did")
          }
        )
        Bullet(line { t("Resync the world when in doubt") })
        Spacer(Modifier.height(10.dp))
        CodeBox(
        ) {
          LivewireCode(sourceCode, modifier = Modifier.fillMaxWidth())
        }
      }
    }
  }

val backpressure by
  Slide(context = SpeakerNotes("")) {
    TitledSlide(title = "Handling backpressure", kicker = "// DIFFING") {
      Bullet(
        line { t("Problem: composition can produce frames far faster than the wire drains them") }
      )
      Bullet(
        line {
          t("A custom ")
          code("MonotonicFrameClock")
          t(" that only ticks when the output buffer has room")
        }
      )
      Bullet(
        line {
          t("Emit → ")
          em("stop the clock")
          t(" → wire drains → clock resumes")
        }
      )
      Bullet(
        line {
          t("Slow consumer ⇒ the app ")
          i("composes less")
          t(", not queues more.")
        }
      )
      Bullet(
        line {
          t("Host is independent enough that throttling doesn't hurt us - latest-wins, no lag")
        }
      )
    }
  }

val actions by
  Slide(
    context =
      SpeakerNotes(
        "We've now covered creating our UIs, establishing a connection over the wire, and " +
          "rendering our UIs on the other side.\n\n" +
          "Buuuuuut, how do we now convey the user's actual intention (i.e. actions). We can't " +
          "really send a lambda across a socket.\n\n" +
          "What we can do is have the client define the intention to the host and have the " +
          "host deliver the user's action back to the client."
      )
  ) {
    TitledSlide(title = "Actions", kicker = "// CLICKING OVER THE WIRE") {
      Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.Top) {
        androidx.compose.foundation.layout.Column(Modifier.weight(1f)) {
          Bullet(line { em("A lambda can't cross a socket!") })
          Bullet(line { t("Client defines the intention") })
          Bullet(line { t("Host delivers the action") })
        }
        Spacer(Modifier.width(14.dp))
        Image(
          painter = painterResource(Res.drawable.diagram_actions),
          contentDescription = "Client defines intention, host delivers the action",
          modifier = Modifier.weight(1f).fillMaxSize(),
          contentScale = ContentScale.Fit,
        )
      }
    }
  }

val definingIntention by
  PreparedSlide(
    context =
      SpeakerNotes(
        "Wait! This is suspiciously starting to look like another tree… Well, maybe a bonzai " +
          "tree.\n\n" +
          "We can model a user's action as a sealed set of classes that define the meaning of " +
          "a users actions. Such as a:\n\n" +
          "`ClickAction` for responding to button or surface clicks\n\n" +
          "`CheckedChangeAction` for switches, checkboxes, and radio buttons\n\n" +
          "`ValueChangeAction` for communicating TextFields\n\n" +
          "`FloatValueChangeAction` for Sliders"
      )
  ) {
    val left =
      rememberSourceCode(language = "kotlin", key = "actionsLeft") {
        // language=kotlin
        """
        @Serializable
        sealed interface LivewireAction


        @Serializable
        data class ClickAction(
          val identifier: String,
        ) : LivewireAction


        @Serializable
        data class CheckedChangeAction(
          val identifier: String,
          val checked: Boolean = false,
        ) : LivewireAction
        """
          .trimIndent()
      }
    val right =
      rememberSourceCode(language = "kotlin", key = "actionsRight") {
        // language=kotlin
        """
        @Serializable
        data class ValueChangeAction(
          val identifier: String,
          val value: String = "",
        ) : LivewireAction


        @Serializable
        data class FloatValueChangeAction(
          val identifier: String,
          val value: Float = 0f,
        ) : LivewireAction
        """
          .trimIndent()
      }

    slideContent {
      TitledSlide(title = "Defining user intention", kicker = "// ACTIONS") {
        CodeBox(
          modifier = Modifier.fillMaxSize()
        ) {
          Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.Top) {
            LivewireCode(left, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(20.dp))
            LivewireCode(right, modifier = Modifier.weight(1f))
          }
        }
      }
    }
  }

val declaringIntention by
  PreparedSlide(
    stepCount = 4,
    context =
      SpeakerNotes(
        listOf(
          0..0 to "Now we need an API to let developers declare this intentions",
          1..1 to
            "First, we capture the unique composition key for this action/node so that " +
              "we can uniquely relate the input from the user when sent back from the host",
          2..2 to
            "Then, in a `LaunchedEffect` we observe all actions of our type, filtered to " +
              "our unique composition id and then execute the saved lambda-action when we " +
              "observe this event.",
          3..3 to
            "Lastly, we remember and return this action, i.e. intention, to the caller " +
              "to be serialized and set across the wire as part of our tree",
        )
      ),
  ) {
    val sourceCode =
      rememberSourceCode(language = "kotlin") {
        val head by marker(dimmed(1..3))
        val identifier by marker(dimmed(2, 3))
        val observe by marker(dimmed(1, 3))
        val ret by marker(dimmed(1, 2))

        // language=kotlin
        """
        ${head}@Composable
        fun clickAction(
          onClick: () -> Unit,
        ): ClickAction {${X}
        ${identifier}  val identifier = "click_${'$'}currentCompositeKeyHashCode"${X}
        ${observe}  val actionObserver = LocalLivewireActionObserver.current

          LaunchedEffect(compositionKey) {
            actionObserver.events
              .filterIsInstance<ClickAction>()
              .filter { it.identifier == identifier }
              .collect {
                onClick()
              }
          }${X}

        ${ret}  return remember { ClickAction(identifier) }${X}
        ${head}}${X}
        """
          .trimIndent()
      }

    slideContent { step ->
      TitledSlide(title = "Declaring user intention", kicker = "// CLIENT") {
        CodeBox(
          modifier = Modifier.fillMaxSize()
        ) {
          LivewireCode(sourceCode, step = step)
        }
      }
    }
  }

val clickingBackwards by
  PreparedSlide(
    context =
      SpeakerNotes(
        "Here is an example of how this would look in the wild for a developer using Livewire.\n\n" +
          "Not so different from just using a lambda."
      )
  ) {
    val sourceCode =
      rememberSourceCode(language = "kotlin") {
        // language=kotlin
        """
        @LivewireComposable
        @Composable
        fun MyAppPlugin() {
          var counter by remember { mutableStateOf(0) }
          Button(
            action = clickAction { counter++ },
          ) {
            Text("Increment: ${'$'}{counter}")
          }
        }
        """
          .trimIndent()
      }

    slideContent {
      TitledSlide(title = "Clicking, but backwards", kicker = "// CLIENT") {
        CodeBox(
          modifier = Modifier.fillMaxSize()
        ) {
          LivewireCode(sourceCode)
        }
      }
    }
  }

val deliveringAction by
  PreparedSlide(
    context =
      SpeakerNotes(
        "Now, on the host side when rendering our node into actual ComposeUI, we do an inverse " +
          "and dispatch the stored action on the node back to the client when clicked."
      )
  ) {
    val sourceCode =
      rememberSourceCode(language = "kotlin") {
        // language=kotlin
        """
        @Composable
        fun ButtonNodeContent(node: ButtonNode) {
          val scope = rememberCoroutineScope()
          val eventDispatcher = LocalLivewireActionDispatcher.current

          Button(
            onClick = {
              scope.launch {
                eventDispatcher.dispatch(node.action)
              }
            },
            modifier = modifier,
          ) // {}
        }
        """
          .trimIndent()
      }

    slideContent {
      TitledSlide(title = "Delivering Action", kicker = "// HOST") {
        CodeBox(
          modifier = Modifier.fillMaxSize()
        ) {
          LivewireCode(sourceCode)
        }
      }
    }
  }
