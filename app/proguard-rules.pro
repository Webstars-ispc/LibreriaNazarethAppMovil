# ============================================================
# Reglas ProGuard/R8 para Librería Nazareth App Móvil
# ============================================================

# ------------------------------------------------------------
# Reglas generales
# ------------------------------------------------------------
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations

# ------------------------------------------------------------
# Retrofit
# ------------------------------------------------------------
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-dontwarn retrofit2.**

# ------------------------------------------------------------
# Gson
# ------------------------------------------------------------
-keep class com.google.gson.** { *; }
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keep public class * implements java.lang.reflect.Type

# Modelos propios con @SerializedName
-keep class com.ispc.librerianazareth.data.model.** { *; }

# ------------------------------------------------------------
# OkHttp
# ------------------------------------------------------------
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# ------------------------------------------------------------
# ZXing (scanner de códigos de barras)
# ------------------------------------------------------------
-keep class com.journeyapps.** { *; }
-keep class com.journeyapps.barcodescanner.** { *; }
-keep class com.google.zxing.** { *; }
-dontwarn com.journeyapps.**
-dontwarn com.google.zxing.**

# ------------------------------------------------------------
# AndroidX
# ------------------------------------------------------------
-keep class androidx.appcompat.** { *; }
-keep class androidx.annotation.** { *; }
-dontwarn androidx.**

# ------------------------------------------------------------
# Clases propias de la app
# ------------------------------------------------------------
-keep class com.ispc.librerianazareth.** { *; }

# ------------------------------------------------------------
# Enums
# ------------------------------------------------------------
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ------------------------------------------------------------
# Parcelable
# ------------------------------------------------------------
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}

# ------------------------------------------------------------
# Serializable
# ------------------------------------------------------------
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}