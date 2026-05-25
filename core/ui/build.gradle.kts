plugins {
    id("ecotrack.android.library")
    id("ecotrack.android.compose")
}

android {
    namespace = "com.ecotrack.core.ui"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:design"))
    implementation(libs.androidx.lifecycle.runtime.compose)
}
