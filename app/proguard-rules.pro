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

-keep class kotlin.Metadata { *; }
-keep class uz.fido.universaldigital.ui.fragments.products.model.FastAccessOperation { *; }
-dontwarn kotlin.time.**
-dontwarn org.slf4j.impl.StaticLoggerBinder
-keepnames class kotlinx.** { *; }
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**
-keep class uz.fido.universaldigital.ui.dialogs.BaseInfoDialog { *; }
-keep public class * extends androidx.fragment.app.Fragment {
    public <init>();
}
-keep class uz.fido.nfccardreaderlib.** { *; }
-dontwarn io.ktor.client.network.sockets.TimeoutExceptionsCommonKt
-dontwarn io.ktor.client.plugins.HttpTimeout$HttpTimeoutCapabilityConfiguration
-dontwarn io.ktor.client.plugins.HttpTimeout$Plugin
-dontwarn io.ktor.client.plugins.HttpTimeout
-dontwarn io.ktor.utils.io.CoroutinesKt


