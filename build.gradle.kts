import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    var composeVersion = "1.7.3"
    kotlin("multiplatform") version "2.0.20"
    id("org.jetbrains.kotlin.plugin.compose")version "2.1.0-RC2"
    id("org.jetbrains.compose") version composeVersion
    kotlin("plugin.serialization") version "2.0.20"
    id("com.android.application") version "8.7.3"
    id("org.jetbrains.kotlin.android") version "2.0.20" apply false
}
group = "cn.pprocket"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
    maven("https://jitpack.io")
    maven("https://jogamp.org/deployment/maven")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}
kotlin {
    jvm()
    androidTarget()
    @OptIn(ExperimentalWasmDsl::class)
    /*
    wasmJs {
        browser {

        }
        binaries.executable()
    }

     */
    sourceSets {
        val commonMain by getting {
            dependencies {

                implementation(compose.material3)
                implementation(compose.runtime)
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.materialIconsExtended)
                implementation("cn.pprocket:heybox:250203-1")
                implementation("cn.pprocket:HeyBase:250202-2")
                implementation("org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha12")
                implementation(compose.material3AdaptiveNavigationSuite)
                //implementation("cn.pprocket:TiebaSDK:241208-6")
                implementation("cn.pprocket:BiliAdaptor:250202-6")
                implementation("com.github.alorma.compose-settings:ui-tiles:2.6.0")
                implementation("com.materialkolor:material-kolor:2.0.2")
                compileOnly("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
                compileOnly("io.ktor:ktor-client-core:3.0.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0-RC")
                implementation("io.coil-kt.coil3:coil-compose:3.0.4")
                implementation("io.coil-kt.coil3:coil-compose-core:3.0.4")
                implementation("io.coil-kt.coil3:coil-network-ktor3:3.0.4")
                implementation("io.github.kdroidfilter:composemediaplayer:0.2.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
                api("io.github.kevinnzou:compose-webview-multiplatform:1.9.40")
                //implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")
            }
        }
        val jvmMain by getting {
            dependencies {
                runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.10.1")
                implementation(compose.desktop.currentOs) {
                    exclude("org.jetbrains.compose.material")
                }
            }
        }
        /*
        val wasmJsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-browser:0.3")
            }
        }

         */
        val androidMain by getting {

        }
    }
}
android {
    namespace = "cn.pprocket.android"
    compileSdk = 35
    lint {
        baseline = file("lint-baseline.xml")
    }
    defaultConfig {
        minSdk = 26
        targetSdk = 35
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
            version.set("7.6.1")
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
