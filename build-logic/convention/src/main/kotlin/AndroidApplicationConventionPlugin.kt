import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("hexium.detekt")
                apply("hexium.kover")
                apply("hexium.roborazzi")
            }

            extensions.configure<ApplicationExtension> {
                compileSdk = 36

                defaultConfig {
                    minSdk = 24
                    targetSdk = 36
                }

                compileOptions {
                    // Align Java target with Kotlin's maximum supported target (24)
                    // We still use the Java 25 Toolchain for compilation (APIs).
                    sourceCompatibility = JavaVersion.VERSION_24
                    targetCompatibility = JavaVersion.VERSION_24
                    isCoreLibraryDesugaringEnabled = true
                }

                testOptions {
                     unitTests {
                        isIncludeAndroidResources = true
                        all {
                            it.useJUnitPlatform()
                        }
                    }
                }

                 @Suppress("UnstableApiUsage")
                androidResources {
                    localeFilters += "en"
                }

                packaging {
                    resources {
                        excludes += "/META-INF/{AL2.0,LGPL2.1}"
                    }
                }
            }

            configureKotlin()

            dependencies {
                add("coreLibraryDesugaring", "com.android.tools:desugar_jdk_libs:2.1.4")
            }
        }
    }

    private fun Project.configureKotlin() {
        configure<KotlinAndroidProjectExtension> {
            jvmToolchain(25)
        }

        val javaToolchains = extensions.getByType<JavaToolchainService>()
        tasks.withType<JavaCompile>().configureEach {
            javaCompiler.set(javaToolchains.compilerFor {
                languageVersion.set(JavaLanguageVersion.of(25))
            })
        }

        tasks.withType<KotlinCompile>().configureEach {
            compilerOptions {
                // Kotlin 2.3.x does not yet support JVM_25 target, so we fall back to JVM_24
                // but the code runs on Java 25.
                jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_24)
            }
        }

        tasks.withType<Test>().configureEach {
            useJUnitPlatform()
            setProperty("failOnNoDiscoveredTests", false)
        }
    }
}
