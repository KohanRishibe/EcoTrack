plugins {
    id("ecotrack.android.application")
    id("ecotrack.android.compose")
    id("ecotrack.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.ecotrack"
    defaultConfig {
        applicationId = "com.ecotrack"
        versionCode = 1
        versionName = "1.0.0"
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":data"))
    implementation(project(":domain"))
    implementation(project(":core:design"))
    implementation(project(":core:ui"))
    implementation(project(":feature:dashboard"))
    implementation(project(":feature:inventory"))
    implementation(project(":feature:addproduct"))
    implementation(project(":feature:shoppinglist"))
    implementation(project(":feature:productdetail"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:ai"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
