plugins {
    `kotlin-dsl`
}

group = "com.hexium.nodes.buildlogic"

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.ksp.gradlePlugin)
    implementation(libs.spotless.gradlePlugin)
    implementation(libs.compose.compiler.gradlePlugin)
    implementation(libs.hilt.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "hexium.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "hexium.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "hexium.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("androidHilt") {
            id = "hexium.android.hilt"
            implementationClass = "HiltConventionPlugin"
        }
        register("spotless") {
            id = "hexium.spotless"
            implementationClass = "SpotlessConventionPlugin"
        }
    }
}
