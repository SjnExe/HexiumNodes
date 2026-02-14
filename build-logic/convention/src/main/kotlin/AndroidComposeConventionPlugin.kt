import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            val androidExtension = extensions.findByType(ApplicationExtension::class.java)
                ?: extensions.findByType(LibraryExtension::class.java)

            // We can't use CommonExtension easily with buildFeatures in generic way without casting.
            // But we can check type.
            if (androidExtension is ApplicationExtension) {
                androidExtension.buildFeatures.compose = true
            } else if (androidExtension is LibraryExtension) {
                androidExtension.buildFeatures.compose = true
            }

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            dependencies {
                val bom = libs.findLibrary("androidx-compose-bom").get()
                add("implementation", platform(bom))
                add("androidTestImplementation", platform(bom))

                add("implementation", libs.findLibrary("androidx-ui-tooling-preview").get())
                add("debugImplementation", libs.findLibrary("androidx-ui-tooling").get())
            }
        }
    }
}
