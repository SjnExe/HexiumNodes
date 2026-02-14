plugins {
    id("hexium.android.library")
    id("hexium.spotless")
}

android {
    namespace = "com.hexium.nodes.core.model"
}

dependencies {
    implementation(libs.androidx.core.ktx)
}
