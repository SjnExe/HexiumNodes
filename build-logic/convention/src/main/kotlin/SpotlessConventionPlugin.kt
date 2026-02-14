import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class SpotlessConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.diffplug.spotless")

            extensions.configure<SpotlessExtension> {
                kotlin {
                    target("**/*.kt")
                    ktlint().editorConfigOverride(mapOf("ktlint_standard_function-naming" to "disabled", "ktlint_standard_no-wildcard-imports" to "disabled"))
                }
            }
        }
    }
}
