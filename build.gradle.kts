
plugins {
    var composeVersion = "1.7.1"
    kotlin("multiplatform") version "2.0.20"
    id("org.jetbrains.kotlin.plugin.compose")version "2.1.0-RC2"
    id("org.jetbrains.compose") version composeVersion
    kotlin("plugin.lombok") version "2.0.0"
    id("io.freefair.lombok") version "8.6"
    kotlin("plugin.serialization") version "2.0.20"
    id("com.android.application") version "8.5.2"
    id("org.jetbrains.kotlin.android") version "2.0.20" apply false
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
    androidTarget()
    wasmJs {
        browser {

        }
        binaries.executable()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {

                implementation(compose.material3)
                implementation(compose.runtime)
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.materialIconsExtended)
                implementation("cn.pprocket:heybox:241212-1")
                implementation("cn.pprocket:HeyBase:241212-1")
                implementation("org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha10")
                implementation(compose.material3AdaptiveNavigationSuite)
                implementation("cn.pprocket:TiebaSDK:241208-6")
                implementation("com.github.alorma.compose-settings:ui-tiles:2.4.0")
                implementation("com.materialkolor:material-kolor:1.7.0")
                implementation("com.russhwolf:multiplatform-settings:1.2.0")
                compileOnly("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
                compileOnly("io.ktor:ktor-client-core:3.0.0-beta-2")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
                implementation("io.coil-kt.coil3:coil-compose:3.0.0-alpha10")
                implementation("io.coil-kt.coil3:coil-compose-core:3.0.0-alpha10")
                implementation("io.coil-kt.coil3:coil-network-ktor3:3.0.0-alpha10")

            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs) {
                    exclude("org.jetbrains.compose.material")
                }
                implementation("dev.gitlive:firebase-perf:2.1.0")
                implementation("dev.gitlive:firebase-analytics:2.1.0")
            }
        }
        val wasmJsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-browser:0.1")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("com.google.accompanist:accompanist-systemuicontroller:0.36.0")
            }
        }
    }
}
android {
    namespace = "cn.pprocket.android"
    compileSdk = 34
    lint {
        baseline = file("lint-baseline.xml")
    }
    defaultConfig {
        minSdk = 26
        targetSdk = 34

        applicationId = "cn.pprocket.android"
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
compose.desktop {
    application {
        mainClass = "cn.pprocket.App_jvmKt"

        buildTypes.release.proguard {
            version.set("7.5.0")
            configurationFiles.from("proguard.pro")
        }
        nativeDistributions {
            packageName = "HeyPC"
            packageVersion = "1.0.0"
            windows {
                //iconFile.set(project.file("icons/icon.ico"))
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