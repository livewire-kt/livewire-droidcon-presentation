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
import androidx.compose.ui.unit.sp
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
import widgets.Livewire

val sectionReassembly by
  Slide(
    context =
      SpeakerNotes(
        "DREW:\nNow we've established how we create our UIs… How we established a connection between a " +
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
        "DREW:\nIn the host app, we can access a Flow of our `LayoutNode` trees from the connection and " +
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

val renderingTree by
  PreparedSlide(
    context =
      SpeakerNotes(
        "DREW:\nStructurally our tree looks something like this, and then the router on the right here " +
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
          modifier = Modifier.fillMaxSize().weight(1.2f),
          contentAlignment = Alignment.Center,
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
          0..0 to "DREW:\nFor layout and other nodes can can handle children iterate through them…",
          1..1 to
            "DREW:\n… then keying each children to the composition key hash generated from the " +
              "client so that the client stays the source of truth in this \"dual-composition\" " +
              "kind of setup",
          2..2 to
            "DREW:\nThen fold their LivewireModifier into a Compose UI modifier, and then feed " +
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
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
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
        """
          ERIC:
          So by now you've probably thought to yourself: there's no way we can send the entire tree over the wire several times per second on a USB 2 cord.

          The good news is that we can actually do diffs. The compose runtime hands us structural diffs. Its insert/remove/move calls ARE the diff.

          Our first frame ends up being a few kb, per-frame patches are generally tens of bytes.

          The host can send a RequestFullTree message to the client in the event it ever loses context (maybe it missed a frame?)
        """.trimIndent()
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
  Slide(context = SpeakerNotes(
    """
      ERIC:
      Even with diffs, backpressure can be an issue! So how do we handle the case that the composition tries to produce frames faster than we can shuttle them to the host?

      We do this by stopping our MonotonicFrameClock after each emission. Once the wire is drained, we restart the clock.

      This means we simply get fewer frames rather than more queueing.

      Luckily the host is independent enough that the throttling doesn't actually hurt us - we just show the latest data available at all times for near-zero lag.
    """.trimIndent()
  )) {
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
          em("composes less", color = Livewire.Red)
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
        """
          DREW:
          We've now covered creating our UIs, establishing a connection over the wire, and rendering our UIs on the other side.

          Buuuuuut, how do we now convey the user's actual intention (i.e. actions). We can't really send a lambda across a socket.

          What we can do is have the client define the intention to the host and have the host deliver the user's action back to the client.
        """.trimIndent()
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
        "DREW:\n" +
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
          0..0 to "DREW:\nNow we need an API to let developers declare this intentions",
          1..1 to
            "DREW:\nFirst, we capture the unique composition key for this action/node so that " +
              "we can uniquely relate the input from the user when sent back from the host",
          2..2 to
            "DREW:\nThen, in a `LaunchedEffect` we observe all actions of our type, filtered to " +
              "our unique composition id and then execute the saved lambda-action when we " +
              "observe this event.",
          3..3 to
            "DREW:\nLastly, we remember and return this action, i.e. intention, to the caller " +
              "to be serialized and set across the wire as part of our tree",
        )
      ),
  ) {
    val sourceCode =
      rememberSourceCode(language = "kotlin") {
        val head by marker(dimmed(1..3))
        val tail by marker(dimmed(1..3))
        val click by marker(dimmed(1, 3))
        val identifier by marker(dimmed(2, 3))
        val observe by marker(dimmed(1, 3))
        val ret by marker(dimmed(1, 2))

      // language=kotlin
      """
        ${head}@Composable
        fun clickAction(${X}
          ${click}onClick: () -> Unit,${X}
        ${tail}): ClickAction {${X}
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
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
      ) {
        LivewireCode(
          sourceCode = sourceCode,
          step = step,
          fontSize = 12.sp,
        )
      }
    }
  }
}

val clickingBackwards by
  PreparedSlide(
    context =
      SpeakerNotes(
        "DREW:\nHere is an example of how this would look in the wild for a developer using Livewire.\n\n" +
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

val deliveringAction by
  PreparedSlide(
    context =
      SpeakerNotes(
        "DREW:\nNow, on the host side when rendering our node into actual ComposeUI, we do an inverse " +
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
