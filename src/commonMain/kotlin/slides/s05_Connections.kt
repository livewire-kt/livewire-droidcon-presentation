package slides

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
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
import livewire_presentation.generated.resources.diagram_android_adb
import livewire_presentation.generated.resources.diagram_desktop
import livewire_presentation.generated.resources.diagram_discovery
import livewire_presentation.generated.resources.diagram_e2e_1
import livewire_presentation.generated.resources.diagram_e2e_2
import livewire_presentation.generated.resources.diagram_host_server
import livewire_presentation.generated.resources.diagram_ios_sim
import livewire_presentation.generated.resources.diagram_ios_tunnel
import livewire_presentation.generated.resources.diagram_physical_ios
import livewire_presentation.generated.resources.diagram_two_phases
import livewire_presentation.generated.resources.diagram_usbmuxd
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
import widgets.line

val sectionConnections by Slide {
  SectionSlide(
    number = "03",
    title = "Connections",
    subtitle = "How we learned to talk to each other",
  )
}

val hostIsServer by
  Slide(
    context =
      SpeakerNotes(
        "Your instinct here is probably to make the app the server. It has the UI and the data, " +
          "so the desktop app should connect in to fetch it, right? Turns out we have to do the " +
          "reverse: the desktop is the server and every app dials out to it.\n\n" +
          "Here's why: A USB cable is not an IP network. The phone has no route to your " +
          "computer. It literally can't address it. The only thing adb or usbmuxd can do is " +
          "bridge a port: 'this port over here shows up as loopback over there.' So no matter " +
          "which way you point it, somebody has to be dialing 127.0.0.1. Loopback is the one " +
          "address that exists on both ends.\n\n" +
          "Given that, the real choice here is WHERE the complexity lives. Put the server on " +
          "the desktop and every client (Android, iOS, simulator, another desktop) does the same " +
          "trivial thing: open a socket to ws://127.0.0.1:38301. The client has no networking " +
          "code, no platform branches, and no idea whether it's on a cable or the same machine.\n\n" +
          "All the horrible platform work (adb reverse forwards, usbmuxd protocol handling, " +
          "socket forwarders) is quarantined on the desktop, behind the illusion that 'the app's " +
          "loopback == the host's server.' One app codebase gives us a different invisible " +
          "bridge under each platform."
      )
  ) {
    TitledSlide(title = "The host is the server", kicker = "// CONNECTIONS") {
      Column(Modifier.fillMaxSize()) {
          Bullet(line { t("Desktop hosts app is the server, every client dials out.") })
          Bullet(line { t("A USB cable isn't a network: the device can't address your Mac.") })
          Bullet(
            line {
              t(
                "So loopback is the one address both ends share — every client does the same trivial thing: dial "
              )
              code("ws://127.0.0.1:38301")
              t(".")
            }
          )
          Bullet(line { t("All the platform mess hides on the host") })
        Image(
          painter = painterResource(Res.drawable.diagram_host_server),
          contentDescription = "Every client dials the host's loopback server",
          modifier = Modifier.weight(1f).fillMaxWidth(),
          contentScale = ContentScale.Fit,
        )
      }
    }
  }

val twoPhases by
  Slide(
    context =
      SpeakerNotes(
        "Two problems people often smush together. Pulling them apart is what makes the picker " +
          "feel instant.\n\n" +
          "Discovery answers 'what could I connect to?'. Cheap enough to run constantly, so a " +
          "device picker can update live as you plug in devices or launch apps. It's " +
          "deliberately lossy and stateless.\n\n" +
          "Connection answers 'wire me up to this one'. Key exchange, the tunnel, the " +
          "websocket. That's the stateful, expensive part, so it only happens once the user " +
          "commits."
      )
  ) {
    TitledSlide(title = "Two phases: discover, then connect", kicker = "// CONNECTIONS") {
      Column(Modifier.fillMaxSize()) {
        Bullet(
          line {
            em("Discovery")
            t(": “what's alive right now?”")
          }
        )
        Bullet(line { t("Continuous, cheap, always running. No tunnels held open.") }, indent = 1)
        Bullet(
          line {
            em("Connection")
            t(": “the user picked one”")
          }
        )
        Bullet(line { t("Builds the persistent encrypted tunnel.") }, indent = 1)
        Image(
          painter = painterResource(Res.drawable.diagram_two_phases),
          contentDescription = "Discovery then connection",
          modifier = Modifier.weight(1f).fillMaxSize(),
          contentScale = ContentScale.Fit,
        )
      }
    }
  }

val discovery by
  Slide(
    context =
      SpeakerNotes(
        "The beacon is deliberately tiny and self-contained. One packet gives the host " +
          "everything it needs to draw a row in the picker: instanceId, app name, app icon, and " +
          "a whole bunch of other metadata.\n\n" +
          "For the curious: this used to be JSON. It's now an artisanal, hand-rolled, " +
          "length-prefixed binary format with a protocol version at the very front. Why? " +
          "Because base64'd app icons are WAY BIGGER than byte arrays."
      )
  ) {
    TitledSlide(title = "Discovery", kicker = "// CONNECTIONS") {
      Bullet(
        line {
          t("Every client embeds a beacon — a binary ")
          code("DiscoveryPacket")
        }
      )
      Bullet(
        line {
          t("Delivery splits on one question: does the client ")
          em("share the host's loopback?")
        }
      )
      Bullet(line { t("Port range lets instances coexist") })
    }
  }

val discoveryDiagram by
  Slide(
    context =
      SpeakerNotes(
        "You can see in the diagram that we have two-transport split. The deciding question is: " +
          "does this client share the host's loopback? Desktop and the simulator do, so they " +
          "just fire a UDP datagram at a known localhost port every couple seconds and the host " +
          "listens. Android and a real iPhone sit behind a USB tunnel, and UDP broadcast can't " +
          "cross that, so those clients run a tiny TCP server and the host opens it, reads one " +
          "packet, and closes.\n\n" +
          "We use a port range instead of one port so you can run several instances of an app " +
          "on a device."
      )
  ) {
    TitledSlide(title = "Discovery", kicker = "// CONNECTIONS") {
      Image(
        painter = painterResource(Res.drawable.diagram_discovery),
        contentDescription = "UDP datagrams for local clients, TCP beacon pull over USB tunnels",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Fit,
      )
    }
  }

val desktopEasy by
  Slide(
    context =
      SpeakerNotes(
        "Here's the baseline - the version of the problem with none of the platform pain. This " +
          "is useful because it isolates the core idea before the tunnels arrive.\n\n" +
          "Client and host are on the same machine, both on localhost, so discovery is a plain " +
          "UDP datagram and 'connecting' is just opening a socket.\n\n" +
          "Why can't life always be this easy?"
      )
  ) {
    TitledSlide(title = "Desktop: the easy one", kicker = "// CONNECTIONS") {
      Bullet(
        line {
          t("It's a KMP tool and desktop is where iteration is fastest — so we started here.")
        }
      )
      Bullet(line { t("Same machine, same loopback: yay, direct connection!") })
      Spacer(Modifier.height(8.dp))
      Image(
        painter = painterResource(Res.drawable.diagram_desktop),
        contentDescription = "Desktop client connecting directly over loopback",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Fit,
      )
    }
  }

val iosSimulator by
  Slide(
    context =
      SpeakerNotes(
        "The simulator looks like iOS but for our purposes it behaves like desktop, because it " +
          "runs as a normal process on the Mac and shares the host's loopback.\n\n" +
          "It's a process on your Mac wearing an iPhone costume. UDP discovery works, the " +
          "connection is direct, same easy path as desktop.\n\n" +
          "Now let's plug in some actual hardware."
      )
  ) {
    TitledSlide(title = "iOS Simulator: also easy!", kicker = "// CONNECTIONS") {
      Bullet(line { t("The simulator shares the Mac's loopback, exactly like a desktop app.") })
      Bullet(
        line {
          t("So discovery is plain UDP and the connection is direct: ")
          em("no tunneling!")
        }
      )
      Bullet(line { t("Then you plug in a real iPhone, and it all falls apart.") })
      Spacer(Modifier.height(8.dp))
      Image(
        painter = painterResource(Res.drawable.diagram_ios_sim),
        contentDescription = "iOS simulator sharing the Mac's loopback",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Fit,
      )
    }
  }

val androidAdb by
  Slide(
    context =
      SpeakerNotes(
        "Discovery: we speak the adb wire protocol directly through the excellent dadb library, " +
          "open a stream to the device's tcp discovery port, and read that one beacon.\n\n" +
          "Connection is the satisfying one. Plain adb 'forward' takes a port on your computer " +
          "and forwards it INTO the device: host to device. But our whole model is 'the app " +
          "dials loopback and reaches the host,' which is backwards. So we use adb REVERSE " +
          "forward, which maps a port on the DEVICE back to a port on the host. Now when the " +
          "app connects to a localhost port on the phone, it surfaces on the host at the same " +
          "port, where our server is listening.\n\n" +
          "That single reverse is the entire Android bridge. Still not bad, right? JUST WAIT " +
          "UNTIL YOU SEE HOW IOS WORKS."
      )
  ) {
    TitledSlide(title = "Android: adb in reverse", kicker = "// CONNECTIONS") {
      Bullet(
        line {
          t("Discovery rides the adb transport (via ")
          code("dadb")
          t("): open a ")
          code("tcp")
          t(" stream, read the beacon, close.")
        }
      )
      Bullet(
        line {
          t("The connection flips from what you'd expect - a ")
          em("reverse forward")
          t(" maps the device's loopback back to the host's.")
        }
      )
      Spacer(Modifier.height(8.dp))
      Image(
        painter = painterResource(Res.drawable.diagram_android_adb),
        contentDescription = "adb reverse forward mapping device loopback to the host",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Fit,
      )
    }
  }

val physicalIos1 by
  Slide(
    context = SpeakerNotes("Things haven't been too bad so far.\n\nHere's where we start the pain")
  ) {
    TitledSlide(title = "Physical iOS", kicker = "// CONNECTIONS") {
      Image(
        painter = painterResource(Res.drawable.diagram_physical_ios),
        contentDescription = "Physical iOS device connection",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Fit,
      )
    }
  }

val physicalIos2 by
  Slide(
    context =
      SpeakerNotes(
        "There is no supported way to open an arbitrary port to an app on a physical iPhone. " +
          "The mechanism Xcode and Finder use is a daemon called usbmuxd, and its protocol is " +
          "completely undocumented.\n\n" +
          "Luckily there are a few open source projects in this space already so we didn't " +
          "have to do much reverse engineering. PeerTalk is an old objective-c library that " +
          "hasn't changed much in 10 or so years. There's also a Linux version of usbmuxd " +
          "called libimobiledevice.\n\n" +
          "You talk to usbmuxd over a Unix domain socket; each message is a 16-byte " +
          "little-endian header wrapping an XML plist.\n\n" +
          "But the detail that shapes everything is the last one: usbmuxd is one-directional. " +
          "The host can reach into the device, but the device can't dial out to the host. " +
          "There's no adb-reverse equivalent. That breaks our 'app dials loopback' rule, so " +
          "let's look into how we get it back."
      )
  ) {
    TitledSlide(title = "Physical iOS", kicker = "// CONNECTIONS") {
      Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1.2f)) {
          Bullet(
            line {
              t("No public API exists, so we use ")
              code("usbmuxd")
              t(
                ", Apple's undocumented daemon that powers connections for Finder, Apple Music, Xcode, iTunes (RIP), etc"
              )
            }
          )
          Bullet(
            line {
              code("usbmuxd listen")
              t(" streams device attach/detach events, each with a ")
              code("deviceId")
              t(" and a ")
              code("UDID")
              t(". Enough to route bytes, but not to show humans.")
            }
          )
          Bullet(
            line {
              t("For the name we shell out: ")
              code("ideviceinfo")
              t(" (Linux), ")
              code("xcrun devicectl")
              t(" (macOS).")
            }
          )
        }
        Spacer(Modifier.width(14.dp))
        Image(
          painter = painterResource(Res.drawable.diagram_usbmuxd),
          contentDescription = "usbmuxd protocol",
          modifier = Modifier.weight(0.8f).fillMaxSize(),
          contentScale = ContentScale.Fit,
        )
      }
    }
  }

val physicalIos3 by
  Slide(
    context =
      SpeakerNotes(
        "Discovery (top) is the same pull model as Android: the host opens a tcp stream over " +
          "usbmuxd and reads the beacon.\n\n" +
          "Connection (bottom) is the part we had to build by hand. Because usbmux is one-way, " +
          "the iOS client library runs its own socket forwarder ON the device, and the host " +
          "runs one too. Between them, over the usbmux stream, we splice together a fake " +
          "loopback-to-loopback tunnel.\n\n" +
          "From left to right: the app connects to its own loopback port, the device-side " +
          "forwarder shuttles that to a different port; usbmux carries that port across the " +
          "cable; the host-side IosForwarder pumps it into the host's loopback at the original " +
          "port; the WebSocket server answers. Five hops, and the app thinks it's talking to " +
          "itself."
      )
  ) {
    TitledSlide(title = "Physical iOS", kicker = "// CONNECTIONS") {
      Bullet(
        line {
          code("usbmuxd")
          t(" only reaches ")
          em("host → device")
          t(
            ". To keep “the app dials loopback,” both ends run a socket forwarder and we stitch a loopback tunnel across the cable ourselves."
          )
        }
      )
      Spacer(Modifier.height(8.dp))
      Image(
        painter = painterResource(Res.drawable.diagram_ios_tunnel),
        contentDescription = "Loopback tunnel stitched across the USB cable via usbmuxd",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Fit,
      )
    }
  }

val e2eEncryption1 by
  Slide(
    context =
      SpeakerNotes(
        "The obvious question here: 'it's localhost, why bother?'\n\n" +
          "Because localhost isn't private: any other process or user on the machine can " +
          "connect to an open port. We're streaming your app's live UI and letting the host " +
          "drive taps back; that deserves protection, and it costs almost nothing."
      )
  ) {
    TitledSlide(title = "E2E Encryption", kicker = "// CONNECTIONS") {
      Image(
        painter = painterResource(Res.drawable.diagram_e2e_1),
        contentDescription = "End to end encryption over the tunnel",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Fit,
      )
    }
  }

val e2eEncryption2 by
  Slide(
    context =
      SpeakerNotes(
        "The handshake is ephemeral ECDH over P-256, done as the very first frames: swap public " +
          "keys, derive a shared secret, run it through HKDF-SHA256 to get symmetric keys. And " +
          "there's no designated client key vs server key — both ends compare public keys and " +
          "deterministically agree who owns which direction, so no role negotiation.\n\n" +
          "Every frame after that is AES-GCM: a one-byte tag (text vs binary), a 12-byte " +
          "nonce, then ciphertext.\n\n" +
          "The nonce is a per-direction prefix plus a monotonically increasing counter, and " +
          "the receiver enforces that the counter only ever goes up. So a replayed or " +
          "reordered frame is rejected outright, not just noticed after the fact."
      )
  ) {
    TitledSlide(title = "E2E Encryption", kicker = "// CONNECTIONS") {
      Bullet(
        line {
          t(
            "The first frames are the handshake. No fixed client/server role, key ownership is decided by comparing public keys."
          )
        }
      )
      Bullet(line { t("Then every frame is tagged, nonce-counted, and encrypted:") })
      Spacer(Modifier.height(8.dp))
      Image(
        painter = painterResource(Res.drawable.diagram_e2e_2),
        contentDescription = "Frame layout: tag, nonce, ciphertext",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Fit,
      )
    }
  }

val stayingConnected by
  PreparedSlide(
    context =
      SpeakerNotes(
        "The problem with connecting to apps like this is that they tend to be fairly ephemeral, " +
          "but luckily we've set ourselves up for fairly easily handling of reconnections.\n\n" +
          "The client is a single loop, the code on screen is barely simplified from the real " +
          "thing. Dial loopback, handshake, stream frames. If anything drops (maybe the app was " +
          "backgrounded or killed, the socket died, the host restarted), it just waits three " +
          "seconds and dials again. No rediscovery, no renegotiating the tunnel.\n\n" +
          "The host side is even lazier: the server never tears down. When a client vanishes " +
          "it just goes Connected → Listening, throws away the UI tree, and waits for someone " +
          "to knock again.\n\n" +
          "If a second socket shows up during a reconnect race, the old one is cancelled and " +
          "replaced, so you never get two live sessions. A connection_id is used to guarantee " +
          "a reconnect is the same app you originally picked, not some other process grabbing " +
          "the port.\n\n" +
          "And the reason app restarts 'just work': the bridge (adb reverse, or the usbmux " +
          "forwarder) lives on the host and outlives the app process. Kill and relaunch the " +
          "app and it reconnects itself, with a brand-new handshake and a full tree resync, so " +
          "you never render stale UI.\n\n" +
          "HAMMER HOME WHY THE DESKTOP BEING THE SERVER IS BETTER"
      )
  ) {
    val sourceCode =
      rememberSourceCode(language = "kotlin") {
        // language=kotlin
        """
        while (true) {
          try {
            httpClient.webSocket(host = "127.0.0.1", port = 38301) {
              secureSession = handshake()
              for (frame in incoming) handle(frame)
            }
          } catch (_: Exception) {}
          delay(3.seconds) // dropped? just dial loopback again
        }
        """
          .trimIndent()
      }

    slideContent {
      TitledSlide(title = "Staying connected", kicker = "// CONNECTIONS") {
        Bullet(
          line {
            t("Because the host is the server, reconnection is almost free. ")
            em("The client just keeps dialing loopback.")
          }
        )
        Bullet(line { t("The entire client reconnection handler is one loop:") })
        Spacer(Modifier.height(10.dp))
        CodeBox {
          LivewireCode(sourceCode, modifier = Modifier.fillMaxWidth())
        }
      }
    }
  }
