# https://github.com/JetBrains/compose-multiplatform/issues/4883

-keep class androidx.compose.runtime.** { *; }
-keep class androidx.collection.** { *; }
-keep class androidx.lifecycle.** { *; }
# -keep class androidx.compose.ui.text.platform.ReflectionUtil { *; }

# We're excluding Material 2 from the project as we're using Material 3
-dontwarn androidx.compose.material.**

# Kotlinx coroutines rules seems to be outdated with the latest version of Kotlin and Proguard
-keep class kotlinx.coroutines.** { *; }

-keep class androidx.lifecycle.** {*;}

-dontwarn  org.jsoup.**
-dontwarn com.googlecode.**




-libraryjars "J:\DevlopResource\GradleRepo\caches\jars-9\o_99f956f783888c5516a7543cbac6482c\android.jar"
-libraryjars "J:\DevlopResource\GradleRepo\caches\modules-2\files-2.1\org.openjfx\javafx-media\22.0.1\66b6cf4b67a678eb125a398ea9c72a7c51eb4645\javafx-media-22.0.1-win.jar"
-libraryjars "J:\DevlopResource\GradleRepo\caches\modules-2\files-2.1\org.openjfx\javafx-base\22.0.1\a970f89174b87c6118bf54080bc5f83b9afb30e9\javafx-base-22.0.1-win.jar"

# A resource is loaded with a relative path so the package of this class must be preserved.
-keeppackagenames okhttp3.internal.publicsuffix.*
-adaptresourcefilenames okhttp3/internal/publicsuffix/PublicSuffixDatabase.gz

-dontwarn kotlin.coroutines
-dontwarn  android.*
-dontwarn com.android
-dontwarn  io.github.kdroidfilter.*
-dontwarn okhttp3.**
-dontwarn javafx.*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keep enum okhttp3.** { *; }

-keep class okio.** { *; }
-keep interface okio.** { *; }
-keep enum okio.** { *; }

-dontwarn io.ktor.**
-dontwarn cn.pprocket.**



-dontwarn org.slf4j.**








