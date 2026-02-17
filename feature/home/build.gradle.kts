plugins {
    id("hexium.android.library")
    id("hexium.android.compose")
    id("hexium.android.hilt")
    id("hexium.spotless")
}

android {
    namespace = "com.hexium.nodes.feature.home"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":data"))
    implementation(project(":core:model")) // Explicitly depend on model

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)

    implementation(libs.androidx.hilt.navigation.compose)

    // Ads
    implementation(libs.play.services.ads)

    implementation(libs.gson)
}
