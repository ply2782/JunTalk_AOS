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




#kakao DaumMap

-keep class net.daum.** {*;}
-keep class android.opengl.** {*;}
-keep class com.kakao.util.maps.helper.** {*;}
-keepattributes Signature
-keepclassmembers class * {
    public static <fields>;
    public *;
}





#firebase crashlytics

-keepattributes SourceFile,LineNumberTable        # Keep file names and line numbers.
-keep public class * extends java.lang.Exception  # Optional: Keep custom exceptions.
-keep public class com.google.firebase.crashlytics*{public *;}
-keep public class com.google.firebase.** {public *;}






#exoplayer2

-keep public class com.google.android.exoplayer2*{public *;}
-dontwarn com.google.android.exoplayer2.source.rtsp.*






#retrofit2

-keep class retrofit2.** { *; }
# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod
# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-keep public class com.squareup.retrofit2.**{public *;}
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclasseswithmembers interface * {
    @retrofit2.* <methods>;
}
# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
# Ignore JSR 305 annotations for embedding nullability infgsonormation.
-dontwarn javax.annotation.**
# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit
# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontnote retrofit2.Platform
-dontwarn retrofit2.**
-dontwarn org.codehaus.mojo.**
-keepattributes Exceptions
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations




##--- Begin:GSON ----
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature
# For using GSON @Expose annotation
-keepattributes *Annotation*
-dontwarn sun.misc.**
# Gson specific classes
-keep class sun.** { *; }
#-keep class com.google.gson.stream.** { *; }
# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }
# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
# keep enum so gson can deserialize it
-keepclassmembers enum * { *; }
-keep class net.mreunionlabs.wob.model.request.** { *; }
-keep class net.mreunionlabs.wob.model.response.** { *; }
-keep class net.mreunionlabs.wob.model.gson.** { *; }

-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken
##--- End:GSON ----




#Glide

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# admob

-keep public class com.google.android.gms.ads.**{ public *; }
-keep public class com.google.ads.** { public *;}


# okHttp3

-dontwarn okhttp3.**
-dontwarn okio.**
-dontnote okhttp3.**

# videotrimmer

-keep public class com.coremedia.**{public *;}
-keep public class com.mp4parser.** {public *;}
-keep public class com.bumptech.**{public *;}
-keep public class com.googlecode.mp4parser.**{public *;}

# custom my class

-keep public class com.cross.juntalk2.model.** {public *; }
-keep public class com.cross.juntalk2.retrofit.** {public *; }
-keep public class com.cross.juntalk2.room.** { public *; }
-keep public class com.cross.juntalk2.utils.**{public *;}
-keep class * extends androidx.databinding.**{public *;}
-keep class * extends androidx.databinding.DataBinderMapper { *; }
-dontwarn java.awt.image.BufferedImage
-dontwarn javax.imageio.ImageIO


