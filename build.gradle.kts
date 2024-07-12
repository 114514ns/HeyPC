import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.ir.builders.declarations.buildTypeParameter
import java.net.URI
import java.net.URL
import java.util.Base64


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
            //password = Base64.getDecoder().decode("Z2hwX3M1SDczNWNNOGhtR0p0dnRGb09DTmNMT2Z5M1YzSzJDRzlJdQ==").decodeToString()
        }
    }
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
    implementation("cn.pprocket:heybox:240711-7")

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

