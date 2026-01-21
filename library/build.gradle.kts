import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

group = "com.sinasamaki"
version = "1.0.0-Alpha2"

kotlin {
    // Enable explicit API mode for better library design
    explicitApi()

    jvm()
    androidLibrary {
        namespace = "com.sinasamaki.chroma.dial"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withJava() // enable java compilation support
        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }

        compilations.configureEach {
            compilerOptions.configure {
                jvmTarget.set(
                    JvmTarget.JVM_11
                )
            }
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
//    linuxX64()

    sourceSets {
        commonMain.dependencies {
            // Public API dependencies - these types are exposed in Dial's public API
            api(compose.runtime)      // @Composable, @Stable, State
            api(compose.foundation)   // MutableInteractionSource
            api(compose.ui)           // Modifier

            // Internal implementation dependencies - not exposed in public API
            implementation(compose.material)
            implementation(compose.materialIconsExtended)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }

}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates(group.toString(), "chroma-dial", version.toString())

    pom {
        name = "ChromaDial"
        description = "Beautiful, customizable circular dial components for Compose Multiplatform"
        inceptionYear = "2026"
        url = "https://github.com/sinasamaki/ChromaDial/"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "sinasamaki"
                name = "sinasamaki"
                url = "https://github.com/sinasamaki"
                email = "dev@sinasamaki.com"
                organization = "sinasamaki OÜ"
                organizationUrl = "https://sinasamaki.com"
            }
        }
        scm {
            url = "https://github.com/sinasamaki/ChromaDial/"
            connection = "scm:git:git://github.com/sinasamaki/ChromaDial.git"
            developerConnection = "scm:git:ssh://git@github.com/sinasamaki/ChromaDial.git"
        }
    }
}
