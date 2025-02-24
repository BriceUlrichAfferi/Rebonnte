# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class com.google.android.gms.auth.api.** { *; }
-keep class com.google.android.gms.common.** { *; }
-keep class com.google.firebase.** { *; }

# Keep Firebase UI for Auth
-keep class com.firebase.ui.auth.** { *; }

# Keep specific classes mentioned in errors
-keep class com.google.android.gms.auth.api.credentials.** { *; }

# Keep annotations
-keepattributes *Annotation*

# Keep line numbers for debugging
-keepattributes SourceFile,LineNumberTable

# Additional rules for reflection, if used
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }
