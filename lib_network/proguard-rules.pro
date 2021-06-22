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

# >>>>>>>>>>>>>>>----------网络lib混淆配置---------->>>>>>>>>>>>>>>
-keep class com.mirkowu.lib_network.** {*;}
-dontwarn com.mirkowu.lib_network.**
# <<<<<<<<<<<<<<<----------网络lib混淆配置----------<<<<<<<<<<<<<<<

# ==================okhttp start===================
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*
# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform
# ==================okhttp end=====================

# ==================retrofit2 start===================
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain service method parameters.
-keepclassmembernames,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# ==================retrofit2 end=====================

# ==================gson start=====================
-dontwarn com.google.gson.**
-keep class com.google.gson.**{*;}
-keep interface com.google.gson.**{*;}
-dontwarn sun.misc.**
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

#实体类单独配置
# ==================gson end=====================