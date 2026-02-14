# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /usr/local/Cellar/android-sdk/24.3.3/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.

# Retrofit
-keepattributes Signature
-keepattributes Exceptions

# Gson
-keepattributes EnclosingMethod

# Hilt
-keep class com.hexium.nodes.HexiumApp_HiltComponents { *; }
-keep class com.hexium.nodes.HexiumApp { *; }

# Room
-keep class androidx.room.RoomDatabase

# Keep Data Models for Gson Serialization/Deserialization
# Use @Keep annotation or ensure specific fields are used for reflection if needed.
# For R8 full mode, we should avoid wildcard keeps on data models unless strictly necessary.
# If these models are used with GSON, we need to keep the fields to prevent renaming.
-keepclassmembers class com.hexium.nodes.data.model.** { <fields>; }
