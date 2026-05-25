plugins {
    id("ecotrack.android.library")
    id("ecotrack.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.ecotrack.core.network"
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.serialization.json)
}
