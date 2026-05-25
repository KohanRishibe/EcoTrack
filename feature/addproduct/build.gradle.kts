plugins {
    id("ecotrack.android.feature")
}

android {
    namespace = "com.ecotrack.feature.addproduct"
}

dependencies {
    implementation(libs.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)
    implementation(libs.mlkit.barcode)
    implementation(libs.accompanist.permissions)
}
