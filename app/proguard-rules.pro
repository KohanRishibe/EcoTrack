# Hilt
-keep class dagger.hilt.** { *; }
-keepclasseswithmembers class * {
    @dagger.hilt.* <methods>;
}

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Ktor / OkHttp
-dontwarn io.ktor.**
-dontwarn okhttp3.**
-dontwarn okio.**

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ML Kit — не блокировать R8
-dontwarn com.google.mlkit.**

# Domain models (serialization / UI)
-keep class com.ecotrack.domain.model.** { *; }
