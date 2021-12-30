# MVM
[![](https://jitpack.io/v/mirkowu/mvm.svg)](https://jitpack.io/#mirkowu/mvm)
-----------------------------------
### 前言
M:Model 数据层
V:View 显示层
M:Mediator 中间层


无论是MVP中的Presenter 还是MVVM中的ViewModel 其本质上都相当于中间人的性质，是连接Model层和View层的媒介。
中间层分别持有Model和View，我们在中间层 得到Model层实例获取数据，再通过回调的方式传递给View层，这里的回调无论是
接口回调，还是Observer等观察者模式，数据绑定，EventBus等类型的传递，其本质都是为了将数据传递给View层，在这方面无论是MVP、
MVVM或衍生出来的变种其行为目的都是一致的。
但由于数据的保存，处理方式，中间层的状态及更新UI方式的不同，才演化出这些架构。

回顾我们所写过的MVP,MVVM等模式，我们会发现其相同之处，都是作为一个中间层来进行数据交互，而最根本的解构方面似乎还是
很鸡肋，完全没达到传说中的高内聚低耦合，具体代码还要看个人水平。

### 使用
依赖jitpack
```
maven { url 'https://jitpack.io' }
```

//你可以直接使用
```
        implementation("com.github.mirkowu:mvm:$ext.mvm_version") { //总仓库
            exclude group: "com.github.mirkowu.mvm", module: "lib_bugly" //bugly 包含升级SDK 二选一
            //exclude group: "com.github.mirkowu.mvm", module: "lib_crash" //bugly 不含升级SDK 二选一
        }

```
//也可以按需索取，部分库之间有依赖，请一同依赖
```
    implementation "com.github.mirkowu.mvm:lib_base:$ext.mvm_version" //基础库
    implementation "com.github.mirkowu.mvm:lib_widget:$ext.mvm_version" //UI组件库
    implementation "com.github.mirkowu.mvm:lib_network:$ext.mvm_version" //网络库
    implementation "com.github.mirkowu.mvm:lib_util:$ext.mvm_version" //工具库
    implementation "com.github.mirkowu.mvm:lib_image:$ext.mvm_version" //图片加载库(默认glide)
    implementation "com.github.mirkowu.mvm:lib_webview:$ext.mvm_version" //X5 + JsBridge 的WebView
    implementation "com.github.mirkowu.mvm:lib_photo:$ext.mvm_version" //相册选择库
    implementation "com.github.mirkowu.mvm:lib_qr:$ext.mvm_version" //二维码扫描
    implementation "com.github.mirkowu.mvm:lib_camera:$ext.mvm_version" //摄像头
    implementation "com.github.mirkowu.mvm:lib_upgrade:$ext.mvm_version" //版本更新(弹窗和下载安装功能)
    implementation "com.github.mirkowu.mvm:lib_stat:$ext.mvm_version" //umeng统计
    implementation "com.github.mirkowu.mvm:lib_screen:$ext.mvm_version" //屏幕适配
    implementation "com.github.mirkowu.mvm:lib_bugly:$ext.mvm_version" //bugly 包含升级SDK 二选一
//    implementation "com.github.mirkowu.mvm:lib_crash:$ext.mvm_version" //bugly 不含升级SDK 二选一
```

你可以在Application的onCreate()中进行对应的初始化
```java
       LogUtil.init(BuildConfig.DEBUG);

       WebViewUtil.initMultiProcess(this);

        //换成你自己的bugly账号 请根据官方SDK对接，此处只做演示
        BuglyManager.init(this, "buglyId", BuildConfig.DEBUG);

      /*** 防止初始化多次，视项目情况设置
       * 需要多进程初始化的方法此方法前面 不需要多进程处理的放在后面
       */
       if (!ProcessUtils.isMainProcess()) {
            return;
       }



        //umeng 请根据官方SDK对接，此处只做演示
        UmengManager.preInit(this, "id", "umeng", BuildConfig.DEBUG);
        UmengManager.init(this, null);

        //屏幕适配
        AutoSizeManager.getInstance().setConfig(this);

        //RxJava2 取消订阅后，抛出的异常无法捕获，导致程序崩溃
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Throwable {
                LogUtil.e(throwable, "RxJavaPlugins");
            }
        });
```

### [Base组件库功能](https://github.com/MirkoWu/MVM/tree/master/lib_base)

### [Widget组件库功能](https://github.com/MirkoWu/MVM/tree/master/lib_widget)

### [Network组件库功能](https://github.com/MirkoWu/MVM/tree/master/lib_network)

### [Util组件库功能](https://github.com/MirkoWu/MVM/tree/master/lib_util)

### [Image组件库功能](https://github.com/MirkoWu/MVM/tree/master/lib_image)

### [WebView组件库功能](https://github.com/MirkoWu/MVM/tree/master/lib_webview)

### [Photo组件库功能](https://github.com/MirkoWu/MVM/tree/master/lib_photo)

### [QR组件库功能](https://github.com/MirkoWu/MVM/tree/master/lib_qr)

### [Camerax组件库功能](https://github.com/MirkoWu/MVM/tree/master/lib_camera)

### [Upgrade组件库功能](https://github.com/MirkoWu/MVM/tree/master/lib_upgrade)

### [Screen组件库功能](https://github.com/MirkoWu/MVM/tree/master/lib_screen)

### [Stat组件库功能](https://github.com/MirkoWu/MVM/tree/master/lib_stat)

### [Bugly组件库功能](https://github.com/MirkoWu/MVM/tree/master/lib_bugly)

### [Crash组件库功能](https://github.com/MirkoWu/MVM/tree/master/lib_crash)



