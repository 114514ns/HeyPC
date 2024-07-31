import org.jetbrains.compose.desktop.application.dsl.TargetFormat


plugins {
    kotlin("jvm") version "1.9.20"
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose") version "1.7.0-alpha01"
    kotlin("plugin.lombok") version "2.0.0"
    id("io.freefair.lombok") version "8.6"
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
    implementation(compose.desktop.currentOs) {
        exclude("org.jetbrains.compose.material")
    }
    implementation(compose.material3)
    implementation(compose.runtime)
    implementation(compose.materialIconsExtended)
    implementation("com.github.ltttttttttttt:load-the-image:1.1.1")
    implementation("cn.pprocket:heybox:240730-8")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.7.0")
    implementation("org.jetbrains.androidx.navigation:navigation-compose:2.7.0-alpha07")
    implementation("com.google.zxing:core:3.5.3")
    implementation("com.google.zxing:javase:3.5.3")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    compileOnly("org.projectlombok:lombok:1.18.32")
    compileOnly("com.google.code.gson:gson:2.11.0")
    implementation("com.github.alorma.compose-settings:ui-tiles:2.4.0")
    implementation("com.github.skydoves:landscapist-coil3:2.3.6")
    implementation("com.materialkolor:material-kolor:1.7.0")
    implementation ("io.github.fornewid:material-motion-compose-core:2.0.1")

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
            packageName = "Compose Fluent Design Gallery"
            packageVersion = "1.0.0"
            windows {
                iconFile.set(project.file("icons/icon.ico"))
            }
        }
    }

}

tasks.withType<org.gradle.jvm.tasks.Jar> {
    manifest {
        attributes["SplashScreen-Image"] = "image.gif"
    }
}
