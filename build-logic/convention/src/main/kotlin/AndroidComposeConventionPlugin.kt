import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.LibraryExtension
import com.ecotrack.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            when {
                pluginManager.hasPlugin("com.android.application") -> {
                    extensions.configure<ApplicationExtension> {
                        configureAndroidCompose(this)
                    }
                }
                pluginManager.hasPlugin("com.android.library") -> {
                    extensions.configure<LibraryExtension> {
                        configureAndroidCompose(this)
                    }
                }
            }

            dependencies {
                val bom = platform(libs.findLibrary("androidx-compose-bom").get())
                add("implementation", bom)
                add("androidTestImplementation", bom)
                add("implementation", libs.findLibrary("androidx-compose-ui").get())
                add("implementation", libs.findLibrary("androidx-compose-ui-graphics").get())
                add("implementation", libs.findLibrary("androidx-compose-ui-tooling-preview").get())
                add("implementation", libs.findLibrary("androidx-compose-material3").get())
                add("implementation", libs.findLibrary("androidx-compose-material-icons-core").get())
                add("debugImplementation", libs.findLibrary("androidx-compose-ui-tooling").get())
                add("debugImplementation", libs.findLibrary("androidx-compose-ui-test-manifest").get())
            }
        }
    }

    private fun configureAndroidCompose(
        extension: CommonExtension<*, *, *, *, *, *>,
    ) {
        extension.buildFeatures {
            compose = true
        }
    }
}
