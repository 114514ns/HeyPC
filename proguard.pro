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







# A resource is loaded with a relative path so the package of this class must be preserved.
-keeppackagenames okhttp3.internal.publicsuffix.*
-adaptresourcefilenames okhttp3/internal/publicsuffix/PublicSuffixDatabase.gz



-dontwarn okhttp3.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keep enum okhttp3.** { *; }

-keep class okio.** { *; }
-keep interface okio.** { *; }
-keep enum okio.** { *; }

-dontwarn io.ktor.**
-dontwarn cn.pprocket.**



-dontwarn org.slf4j.**








