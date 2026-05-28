plugins {
    id("ecotrack.android.feature")
}

android {
    namespace = "com.ecotrack.feature.inventory"
}

dependencies {
    implementation(libs.coil.compose)
}
