import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.ir.builders.declarations.buildTypeParameter
import java.net.URI
import java.net.URL
import java.util.Base64


plugins {
    kotlin("jvm") version "1.9.20"
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
    mavenLocal()
    maven("https://jogamp.org/deployment/maven")
    maven ( "https://s01.oss.sonatype.org/content/repositories/snapshots/")
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
    implementation("cn.pprocket:heybox:240714-5")
    implementation(compose.html.core)
    implementation(compose.runtime)
    implementation(compose.materialIconsExtended)

}

compose.desktop {
    application {
        mainClass = "AppKt"


        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "HeyPC"
            packageVersion = "1.0.0"
        }
    }
}

