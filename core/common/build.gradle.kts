plugins {
    id("ecotrack.android.library")
}

android {
    namespace = "com.ecotrack.core.common"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
}
