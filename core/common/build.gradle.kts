plugins {
    id("hexium.android.library")
    id("hexium.spotless")
}

android {
    namespace = "com.hexium.nodes.core.common"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
    api(libs.timber)
}
