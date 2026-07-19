import org.gradle.language.jvm.tasks.ProcessResources

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.compose)
  alias(libs.plugins.kotlin.plugin.compose)
  alias(libs.plugins.cup)
}

cup {
  targetDesktop()
  targetWeb(withJsCompat = false)
}

// CuP ships its own index.html (titled "gstudio", lang="fr") — retitle it for this deck.
tasks.named<ProcessResources>("wasmJsProcessResources") {
  filesMatching("index.html") {
    filter { line ->
      line
        .replace("<title>gstudio</title>", "<title>Livewire — Droidcon '26</title>")
        .replace("""<html lang="fr">""", """<html lang="en">""")
    }
  }
}

kotlin {
  jvmToolchain(21)

  sourceSets.commonMain {
    dependencies {
      implementation(libs.bundles.compose)

      implementation(libs.bundles.cup)

      implementation(libs.emoji.compose)
      implementation(libs.qrose)
      implementation(libs.particle.emitter)
    }
  }

  // Livewire publishes no js/wasm artifacts, and vlcj is a JVM-only libVLC binding —
  // both stay desktop-side behind expect/actual (LivewireIntegration, VideoPlayer).
  sourceSets.jvmMain {
    dependencies {
      implementation(libs.livewire.client)
      implementation(libs.livewire.plugin.recomposition)
      implementation(libs.vlcj)
    }
  }
}
