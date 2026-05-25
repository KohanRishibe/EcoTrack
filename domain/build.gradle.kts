plugins {
    id("ecotrack.android.library")
    id("ecotrack.hilt")
}

android {
    namespace = "com.ecotrack.domain"
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)
}
