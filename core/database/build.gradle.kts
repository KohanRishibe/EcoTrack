plugins {
    id("ecotrack.android.library")
    id("ecotrack.room")
}

android {
    namespace = "com.ecotrack.core.database"
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.kotlinx.coroutines.android)
}
