# Retrofit 2
-dontwarn retrofit2.**
-keepattributes Signature, InnerClasses, EnclosingMethod, RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# OkHttp 3
-dontwarn okhttp3.**
-dontwarn okio.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Gson
-keepattributes Signature
-keep class com.example.librerianazareth.data.model.** { *; }
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-keep public class * implements java.lang.reflect.Type

# ZXing (JourneyApps)
-dontwarn com.journeyapps.**
-keep class com.journeyapps.barcodescanner.** { *; }
-keep class com.google.zxing.** { *; }

# App (Activities y clases internas)
-keep class com.example.librerianazareth.** { *; }

# AndroidX / Material (ya cubiertos por reglas de Android por defecto)
-keep class androidx.appcompat.** { *; }
-keep class androidx.annotation.** { *; }