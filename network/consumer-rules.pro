-keepattributes *Annotation*
-keep class kotlin.Metadata { *; }
-keepclassmembers class kotlin.Metadata { *; }
-keep class com.google.gson.** { *; }
-keep class uz.fido.network.domain.model.** { *; }
-keep class uz.fido.network.di.**{*;}
-keep class uz.myid.android.sdk.**{*;}
-keep class org.bouncycastle.** {*;}
-keep class java.security.**{ *; }
-keep class com.android.org.bouncycastle.jcajce.**{*;}
-dontwarn kotlin.time.**

# Retain cryptography-related classes
#-keep class javax.crypto.** { *; }
#-keep class sun.security.** { *; }
#-keep class org.bouncycastle.** { *; }

# Keep keystore-related methods
#-keep public class * extends java.security.KeyStoreSpi
#-keep public class * extends javax.crypto.SecretKeyFactorySpi

-keepnames class kotlinx.** { *; }
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**