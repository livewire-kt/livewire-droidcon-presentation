plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.compose)
  alias(libs.plugins.kotlin.plugin.compose)
  alias(libs.plugins.cup)
}

cup { targetDesktop() }

kotlin {
  jvmToolchain(21)

  sourceSets.commonMain {
    dependencies {
      implementation(libs.bundles.compose)

      implementation(libs.bundles.cup)

      implementation(libs.emoji.compose)
      implementation(libs.livewire.client)
      implementation(libs.livewire.plugin.recomposition)
      implementation(libs.qrose)
      implementation(libs.particle.emitter)
    }
  }
}
