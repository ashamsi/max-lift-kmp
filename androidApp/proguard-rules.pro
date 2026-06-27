# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/artur-shamsi/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools-adk/proguard.html

# Add any KMP specific rules here if needed.

-keep class com.google.android.gms.ads.** { *; }
-dontwarn com.google.android.gms.ads.**