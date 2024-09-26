import org.jetbrains.compose.ComposeBuildConfig
import org.jetbrains.compose.desktop.application.dsl.TargetFormat



plugins {
    var composeVersion = "1.6.11"
    kotlin("multiplatform") version "2.0.20"
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose") version composeVersion
    kotlin("plugin.lombok") version "2.0.0"
    id("io.freefair.lombok") version "8.6"
    kotlin("plugin.serialization") version "2.0.20"
}
group = "cn.pprocket"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
    maven("https://jitpack.io")
    mavenLocal()
    maven("https://jogamp.org/deployment/maven")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}
dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality




}
kotlin {
    jvm()


    js() {
        browser {
            webpackTask {
                mainOutputFileName = "app.js"  // 自定义文件名
            }
        }
        binaries.executable()

    }


    wasmJs {
        browser {
            webpackTask {
                mainOutputFileName = "app.js"  // 自定义文件名
            }
        }
        binaries.executable()
    }



    sourceSets {
        println(ComposeBuildConfig.composeVersion)
        val commonMain by getting {
            dependencies {

                implementation(compose.material3)
                implementation(compose.runtime)
                implementation(compose.materialIconsExtended)
                implementation("cn.pprocket:heybox:240926-1")
                implementation("org.jetbrains.androidx.navigation:navigation-compose:2.7.0-alpha07")
                implementation("com.github.alorma.compose-settings:ui-tiles:2.4.0")
                implementation("media.kamel:kamel-image:1.0.0-beta.7")
                implementation("com.materialkolor:material-kolor:1.7.0")
                implementation("com.russhwolf:multiplatform-settings:1.2.0")
                compileOnly("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs) {
                    exclude("org.jetbrains.compose.material")
                }
            }
        }
        val wasmJsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-browser:0.1")
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "AppKt"

        buildTypes.release.proguard {
            version.set("7.5.0")
            configurationFiles.from("proguard.pro")
        }
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "HeyPC"
            packageVersion = "1.0.0"
            windows {
                iconFile.set(project.file("icons/icon.ico"))
            }
            modules("jdk.unsupported")
            modules("java.management")
        }
    }

}

tasks.withType<org.gradle.jvm.tasks.Jar> {
    manifest {
        attributes["SplashScreen-Image"] = "image.gif"
    }
}
