plugins {
    id("ecotrack.android.feature")
}

android {
    namespace = "com.ecotrack.feature.ai"
}

dependencies {
    implementation(libs.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.activity.compose)
}
