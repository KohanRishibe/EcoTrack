plugins {
    id("ecotrack.android.library")
    id("ecotrack.hilt")
    id("ecotrack.room")
}

android {
    namespace = "com.ecotrack.data"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:database"))
    implementation(project(":core:network"))
    implementation(project(":core:ml"))
    implementation(project(":domain"))

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.datastore.preferences)
}
