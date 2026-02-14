plugins {
    id("hexium.android.library")
    id("hexium.android.compose")
    id("hexium.spotless")
}

android {
    namespace = "com.hexium.nodes.core.ui"
}

dependencies {
    api(libs.androidx.material.icons.extended)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)
    implementation(libs.material)
}
