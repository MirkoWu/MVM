
# >>>>>>>>>>>>>>>----------1.实体类---------->>>>>>>>>>>>>>>
# 保留本库不被混淆
-keep class com.mirkowu.mvm.bean.** {*;}

-dontwarn com.mirkowu.mvm.**
# <<<<<<<<<<<<<<<----------1.实体类----------<<<<<<<<<<<<<<<


# >>>>>>>>>>>>>>>----------2.第三方包---------->>>>>>>>>>>>>>>

# ==================eventbus start===================
#eventbus
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
# ==================eventbus end===================

## ==================Share SDK start===================
##Share SDK
#-keep class cn.sharesdk.**{*;}
#-keep class com.sina.**{*;}
#-keep class **.R$* {*;}
#-keep class **.R{*;}
#-keep class com.mob.**{*;}
#-keep class m.framework.**{*;}
#-dontwarn cn.sharesdk.**
#-dontwarn com.sina.**
#-dontwarn com.mob.**
#-dontwarn **.R$*
#
## ==================Share SDK end===================

# ==================umeng start===================
#Umeng SDK
-keep class com.umeng.** {*;}

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep public class com.mirkowu.planlife.R$*{
public static final int *;
}


#Umeng Push
-dontwarn com.umeng.**
-dontwarn com.taobao.**
-dontwarn anet.channel.**
-dontwarn anetwork.channel.**
-dontwarn org.android.**
-dontwarn org.apache.thrift.**
-dontwarn com.xiaomi.**
-dontwarn com.huawei.**
-dontwarn com.meizu.**
-keepattributes *Annotation*
-keep class com.taobao.** {*;}
-keep class org.android.** {*;}
-keep class anet.channel.** {*;}
-keep class com.umeng.** {*;}
-keep class com.xiaomi.** {*;}
-keep class com.huawei.** {*;}
-keep class com.meizu.** {*;}
-keep class org.apache.thrift.** {*;}
-keep class com.alibaba.sdk.android.**{*;}
-keep class com.ut.**{*;}
-keep class com.ta.**{*;}
-keep public class **.R$*{
   public static final int *;
}
# ==================umeng end===================
#
## ==================baidu统计 start===================
##baidu统计
#-keep class com.baidu.mobstat.** { *; }
#-keep class com.baidu.bottom.** { *; }
## ==================baidu统计 end===================
#
## ==================穿山甲平台 start===================
##穿山甲平台 1-31
#-keep class com.bytedance.sdk.openadsdk.** { *; }
#-keep public interface com.bytedance.sdk.openadsdk.downloadnew.** {*;}
#-keep class com.pgl.sys.ces.* {*;}
## ==================穿山甲平台 end===================


# ==================七牛上传 start===================
-keep class com.qiniu.**{*;}
-keep class com.qiniu.**{public <init>();}
-ignorewarnings
# ==================七牛上传 end===================

## ==================友巨ASO start===================
#-keep ,allowobfuscation @interface com.yj.mcsdk.annotation.Keep
#-keep class com.yj.mcsdk.annotation.Keep{*;}
#-keep @com.yj.mcsdk.annotation.Keep class *
#-keepclassmembers class * {
#    @com.yj.mcsdk.annotation.Keep *;
#}
## ==================友巨ASO end===================


## ==================阿里云oss start===================
#-keep class com.alibaba.sdk.android.oss.** { *; }
#-dontwarn okio.**
#-dontwarn org.apache.commons.codec.binary.**
## ==================阿里云oss end===================

# ==================安全联盟oaid  start===================
-keep class com.bun.miitmdid.core.** {*;}
# ==================安全联盟oaid end===================

# >>>>>>>>>>>>>> GSYVideoPlayer  >>>>>>>>>>>>>>
-keep class com.shuyu.gsyvideoplayer.video.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.video.**
-keep class com.shuyu.gsyvideoplayer.video.base.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.video.base.**
-keep class com.shuyu.gsyvideoplayer.utils.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.utils.**
-keep class tv.danmaku.ijk.** { *; }
-dontwarn tv.danmaku.ijk.**

-keep public class * extends android.view.View{
        *** get*();
        void set*(***);
        public <init>(android.content.Context);
        public <init>(android.content.Context, android.util.AttributeSet);
        public <init>(android.content.Context, android.util.AttributeSet, int);
}

# <<<<<<<<<<<<<<<  GSYVideoPlayer  <<<<<<<<<<<<<<<


# <<<<<<<<<<<<<<<----------2.第三方包----------<<<<<<<<<<<<<<<


# >>>>>>>>>>>>>>>----------3.与js互相调用的类---------->>>>>>>>>>>>>>>

# <<<<<<<<<<<<<<<----------3.与js互相调用的类----------<<<<<<<<<<<<<<<


# >>>>>>>>>>>>>>>----------4.反射相关的类和方法---------->>>>>>>>>>>>>>>

# <<<<<<<<<<<<<<<----------4.反射相关的类和方法----------<<<<<<<<<<<<<<<
