plugins {
    id("ecotrack.android.library")
}

android {
    namespace = "com.ecotrack.core.ml"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.mlkit.image.labeling)
    implementation(libs.mlkit.text.recognition)
}
