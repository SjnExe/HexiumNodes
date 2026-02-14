plugins {
    id("hexium.android.library")
    id("hexium.android.hilt")
    id("hexium.spotless")
}

android {
    namespace = "com.hexium.nodes.data"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)

    // Hilt - Plugin handles deps, but we might need explicit deps if plugin deps are not sufficient?
    // Convention plugin adds hilt-android and hilt-compiler (ksp).
    // So we don't need to add them here.

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Security
    implementation(libs.androidx.security.crypto)
}
