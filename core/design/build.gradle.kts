plugins {
    id("ecotrack.android.library")
    id("ecotrack.android.compose")
}

android {
    namespace = "com.ecotrack.core.design"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
}
