import com.android.build.api.dsl.ApplicationExtension
import com.ecotrack.buildlogic.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.application")
            pluginManager.apply("org.jetbrains.kotlin.android")

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)

                defaultConfig {
                    targetSdk = 35
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    resourceConfigurations += listOf("ru", "en")
                }

                splits {
                    abi {
                        isEnable = true
                        reset()
                        include("armeabi-v7a", "arm64-v8a")
                        isUniversalApk = false
                    }
                }

                packaging {
                    resources {
                        excludes += setOf(
                            "META-INF/DEPENDENCIES",
                            "META-INF/LICENSE",
                            "META-INF/LICENSE.txt",
                            "META-INF/NOTICE",
                            "META-INF/NOTICE.txt",
                        )
                    }
                }

                buildTypes {
                    release {
                        isMinifyEnabled = true
                        isShrinkResources = true
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro",
                        )
                    }
                    debug {
                        isMinifyEnabled = false
                    }
                }
            }
        }
    }
}
