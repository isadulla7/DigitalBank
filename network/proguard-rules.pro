-keepattributes *Annotation*
-keep class kotlin.Metadata { *; }
-keepclassmembers class kotlin.Metadata { *; }
-keep class com.google.gson.** { *; }
-keep class uz.fido.network.domain.model.** { *; }
-keep class uz.fido.network.di.**{*;}
-keep class org.bouncycastle.** {*;}
-dontwarn kotlin.time.**
-keep class com.android.org.bouncycastle.jcajce.**{*;}
-keep class uz.fido.nfccardreaderlib.** { *; }
 # With R8 full mode generic signatures are stripped for classes that are not
 # kept. Suspend functions are wrapped in continuations where the type argument
 # is used.
 -keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

 # R8 full mode strips generic signatures from return types if not kept.
 -if interface * { @retrofit2.http.* public *** *(...); }
 -keep,allowoptimization,allowshrinking,allowobfuscation class <3>

 # With R8 full mode generic signatures are stripped for classes that are not kept.
 -keep,allowobfuscation,allowshrinking class retrofit2.Response

# Retain cryptography-related classes
#-keep class java.security.**{ *; }
#-keep class javax.crypto.** { *; }
#-keep class sun.security.** { *; }
#-keep class org.bouncycastle.** { *; }

# Keep keystore-related methods
#-keep public class * extends java.security.KeyStoreSpi
#-keep public class * extends javax.crypto.SecretKeyFactorySpi

-keepnames class kotlinx.** { *; }
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**
-dontwarn java.lang.invoke.StringConcatFactory
#-dontwarn java.lang.invoke.StringConcatFactory
#-dontwarn uz.fido.utils.const.APIServiceConst
#-dontwarn uz.fido.utils.device.GetDeviceInfo$DeviceInfo
#-dontwarn uz.fido.utils.device.GetDeviceInfo
#-dontwarn uz.fido.utils.security.CryptoUtil
#-dontwarn uz.fido.utils.security.DiffieHellman
#-dontwarn uz.fido.utils.security.EncryptPasswordKt
#-dontwarn uz.fido.utils.security.SecurePrefsManagerKt
#-dontwarn uz.fido.utils.utility.activity.InsertStringBetweenKt
#-dontwarn uz.fido.utils.utility.context.AppSignatureHelper
#-dontwarn uz.fido.utils.utility.context.ContextExtensionsKt
#-dontwarn uz.fido.utils.utility.language.Utility
#-dontwarn uz.fido.utils.utility.user.UserExtensionsKt

#-keep interface uz.fido.network.domain.datasource.services.* { *; }
#-keep interface uz.fido.network.room.* { *; }
#-keep interface retrofit2.Call
#-keep class retrofit2.Retrofit { *; }
#-keep class dagger.hilt.** { *; }
#-keep class javax.inject.** { *; }

