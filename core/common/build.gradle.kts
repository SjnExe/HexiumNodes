plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.spotless)
}

kotlin {
    jvmToolchain(25)
}

android {
    namespace = "com.hexium.nodes.core.common"
    compileSdk = 36
    defaultConfig {
        minSdk = 24
    }
}

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
        target("**/*.kt")
        ktlint().editorConfigOverride(mapOf("ktlint_standard_function-naming" to "disabled", "ktlint_standard_no-wildcard-imports" to "disabled"))
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
}
