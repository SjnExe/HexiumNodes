plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.spotless)
}

kotlin {
    jvmToolchain(25)
}

android {
    namespace = "com.hexium.nodes.core.ui"
    compileSdk = 36
    defaultConfig {
        minSdk = 24
    }
    buildFeatures {
        compose = true
    }
}

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
        target("**/*.kt")
        ktlint().editorConfigOverride(mapOf("ktlint_standard_function-naming" to "disabled", "ktlint_standard_no-wildcard-imports" to "disabled"))
    }
}

dependencies {
    api(libs.androidx.material.icons.extended)

    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.material)
}
