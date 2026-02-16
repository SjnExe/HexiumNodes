import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register

class DetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("io.gitlab.arturbosch.detekt")

            extensions.configure<DetektExtension> {
                toolVersion = "1.23.8"
                source.setFrom(
                    files(
                        "src/main/java",
                        "src/main/kotlin",
                        "src/test/java",
                        "src/test/kotlin"
                    )
                )
                config.setFrom(files("${project.rootDir}/config/detekt/detekt.yml"))
                buildUponDefaultConfig = true
                allRules = false
                autoCorrect = true
            }

            tasks.named<Detekt>("detekt") {
                reports {
                    html.required.set(true)
                    xml.required.set(true)
                    txt.required.set(false)
                    sarif.required.set(true)
                }
            }

            dependencies {
                 "detektPlugins"("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.8")
            }
        }
    }
}
