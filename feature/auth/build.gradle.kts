plugins {
    id("hexium.android.library")
    id("hexium.android.compose")
    id("hexium.android.hilt")
    id("hexium.spotless")
}

android {
    namespace = "com.hexium.nodes.feature.auth"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":data"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)

    implementation(libs.androidx.hilt.navigation.compose)
}
