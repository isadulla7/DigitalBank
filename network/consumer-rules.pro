-keepattributes *Annotation*
-keep class kotlin.Metadata { *; }
-keepclassmembers class kotlin.Metadata { *; }
-keep class com.google.gson.** { *; }
-keep class uz.fido.network.domain.model.** { *; }

-dontwarn kotlin.time.**