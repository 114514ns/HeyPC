-keep class androidx.compose.runtime.** { *; }
-keep class androidx.collection.** { *; }
-keep class androidx.lifecycle.** { *; }
# -keep class androidx.compose.ui.text.platform.ReflectionUtil { *; }

# We're excluding Material 2 from the project as we're using Material 3
-dontwarn androidx.compose.material.**

# Kotlinx coroutines rules seems to be outdated with the latest version of Kotlin and Proguard
-keep class kotlinx.coroutines.** { *; }

-dontwarn  org.jsoup.**
-dontwarn com.googlecode.**



-dontwarn okhttp3.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keep enum okhttp3.** { *; }

-keep class okio.** { *; }
-keep interface okio.** { *; }
-keep enum okio.** { *; }

# https://github.com/JetBrains/compose-multiplatform/issues/4883


