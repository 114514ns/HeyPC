# https://github.com/JetBrains/compose-multiplatform/issues/4883

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

-dontwarn com.alibaba.**
-dontwarn org.apache.**
-dontwarn net.dongliu.**
-dontwarn org.slf4j.**

-keep class org.apache.** { *; }
-keep interface org.apache.** { *; }
-keep enum org.apache.** { *; }

# 保留本地库加载相关的代码
-keep class org.scijava.nativelib.** { *; }
-keep class unicorn.** { *; }
-keep class com.github.unidbg.** { *; }

# 保留 JNA 库中的所有类和方法
-keep class com.sun.jna.** { *; }



-keep class com.skydoves.** {*;}
-keep enum com.skydoves.** {*;}
-keep interface com.skydoves.** {*;}



