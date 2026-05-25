import com.ecotrack.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("ecotrack.android.library")
            pluginManager.apply("ecotrack.android.compose")
            pluginManager.apply("ecotrack.hilt")

            dependencies {
                add("implementation", project(":core:common"))
                add("implementation", project(":core:design"))
                add("implementation", project(":core:ui"))
                add("implementation", project(":domain"))

                add("implementation", libs.findLibrary("androidx-lifecycle-runtime-compose").get())
                add("implementation", libs.findLibrary("androidx-lifecycle-viewmodel-compose").get())
                add("implementation", libs.findLibrary("androidx-navigation-compose").get())
                add("implementation", libs.findLibrary("hilt-navigation-compose").get())
                add("implementation", libs.findLibrary("androidx-compose-material-icons-extended").get())
                add("implementation", libs.findLibrary("coil-compose").get())
            }
        }
    }
}
