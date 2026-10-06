# ============================================================
# Reglas ProGuard para Librería Nazareth App Móvil
# ============================================================

# ------------------------------------------------------------
# Retrofit
# ------------------------------------------------------------
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes EnclosingMethod

-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**

# Mantener las interfaces de Retrofit
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# ------------------------------------------------------------
# Gson
# ------------------------------------------------------------
-keep class com.google.gson.** { *; }
-keepattributes Signature
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Mantener los modelos (los que usan @SerializedName)
-keep class com.example.librerianazareth.data.model.** { *; }

# ------------------------------------------------------------
# OkHttp
# ------------------------------------------------------------
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

# ------------------------------------------------------------
# ZXing (scanner de códigos de barras)
# ------------------------------------------------------------
-keep class com.journeyapps.** { *; }
-keep class com.google.zxing.** { *; }
-dontwarn com.google.zxing.**

# ------------------------------------------------------------
# AndroidX
# ------------------------------------------------------------
-keep class androidx.** { *; }
-dontwarn androidx.**

# ------------------------------------------------------------
# Modelos propios
# ------------------------------------------------------------
-keep class com.example.librerianazareth.** { *; }

# ------------------------------------------------------------
# Reglas generales
# ------------------------------------------------------------
# Mantener nombres de clases con anotaciones
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations

# No ofuscar enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# No ofuscar Parcelable
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}

# No ofuscar Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}