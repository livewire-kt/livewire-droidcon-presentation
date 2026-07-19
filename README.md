# Livewire — Droidcon '26

> **Debugging Using Remote Compose, Made Easy with Livewire**
> by Drew Heavner & Eric Kuck · 40 minutes

The slide deck for our Droidcon '26 talk about [Livewire](https://github.com/livewire-kt/livewire) —
a Kotlin Multiplatform side-car debugging tool where plugins are written *once, in your app*,
as real Compose code, and rendered remotely on the desktop host.

The deck itself is a Kotlin/Compose desktop app built with
[CuP (Compose ur Pres)](https://github.com/KodeinKoders/CuP), converted from the original
94-slide PowerPoint into ~70 slides of real Compose widgets, stepped code walkthroughs, and
animated diagrams. Naturally, it dogfoods Livewire: the presentation runs a `LivewireClient`
with custom plugins so we can debug the deck live, from the deck.

## The talk

1. **The Problem** — the storied history of side-car debugging (Stetho, Flipper, and what we wanted instead)
2. **The Idea** — Compose once, over the wire: building a custom composition (tree, `Applier`, `Composition`, node emitters)
3. **Connections** — how every KMP target talks to the desktop host (loopback, adb reverse, `usbmuxd`, E2E encryption)
4. **Re-assembly** — rendering the tree on the host, diffing, backpressure, and sending actions backwards
5. **Livewire** — live demos, the plugin API, and how to use it in your own apps today

## Running

```sh
./gradlew run          # present
./gradlew hotRunJvm    # develop with Compose Hot Reload
```

Desktop only. CuP plugins enabled: speaker window (with notes), laser pointer, overview,
image export, and window management.

## Project layout

```
src/commonMain/kotlin/
  main.kt            # cupApplication, slide order, LivewireClient setup
  slides/            # one file per talk section (s00_Intro … s08_Outro)
  widgets/           # theme, slide scaffolds, code display, pace meter, GIF/video players
  speakernotes/      # custom speaker window
  livewire/          # deck's own Livewire plugins (SlideDeck, Deck Doctor)
  bugs/              # "faux bugs" staged for the live-debugging demo
```

Custom bits worth a look:

- **Pace meter** (`widgets/PaceMeter.kt`) — keyframed ahead/behind tracking against the 40-minute
  runtime; full timeline in the speaker window, a discreet battery gauge for the audience.
- **Code focus** (`widgets/CodeFocus.kt`) — a draw-phase dimming effect for stepping through
  code, replacing CuP's default zoom highlight.
- **Deck Doctor** (`livewire/DeckDoctorPlugin.kt`) — the Livewire plugin used on stage to
  "debug" the deck itself.

Design (dot grid background, palette, Black Han Sans titles) is reproduced in pure Compose
from the original deck; diagrams, screenshots, and GIFs ship as image assets.
