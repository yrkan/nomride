# NomRide ProGuard Rules

# Keep kotlinx.serialization classes
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep NomRide model classes (serializable)
-keep,includedescriptorclasses class com.nomride.model.**$$serializer { *; }
-keepclassmembers class com.nomride.model.** {
    *** Companion;
}
-keepclasseswithmembers class com.nomride.model.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep karoo-ext classes
-keep class io.hammerhead.karooext.** { *; }

# Keep NomRide extension and receiver
-keep class com.nomride.karoo.NomRideExtension { *; }
-keep class com.nomride.BootReceiver { *; }

# Remove debug logging in release
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
}
-assumenosideeffects class timber.log.Timber {
    public static void v(...);
    public static void d(...);
}
