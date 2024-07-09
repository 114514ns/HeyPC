import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.net.URI
import java.net.URL

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

group = "cn.pprocket"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
    maven("https://jitpack.io")
    maven {
        url = URI("https://maven.pkg.github.com/114514ns/heybox")
        credentials {
            username = "114514ns"
            password = "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" //it's a read only token
        }
    }
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.androidx.navigation:navigation-compose:2.7.0-alpha07")
    implementation(compose.material3)
    implementation("com.github.ltttttttttttt:load-the-image:1.1.1")//this
    implementation("cn.pprocket:heybox:240709-1")
    implementation("androidx.compose.material:material-icons-extended:1.6.8")
    implementation("com.github.skydoves:landscapist-coil3:2.3.6")

}


compose.desktop {
    application {
        mainClass = "MainKt"


        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "HeyPC"
            packageVersion = "1.0.0"
        }
    }
}

