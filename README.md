# MVM
[![](https://jitpack.io/v/mirkowu/mvm.svg)](https://jitpack.io/#mirkowu/mvm)
-----------------------------------
我所理解的MVM
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

//你可以直接使用
```
    implementation "com.github.mirkowu:mvm:$ext.mvm_version" //总仓库

```
//也可以按需索取
```
    implementation "com.github.mirkowu.mvm:lib_base:$ext.mvm_version"
    implementation "com.github.mirkowu.mvm:lib_widget:$ext.mvm_version"
    implementation "com.github.mirkowu.mvm:lib_util:$ext.mvm_version"
    implementation "com.github.mirkowu.mvm:lib_network:$ext.mvm_version"
    implementation "com.github.mirkowu.mvm:lib_image:$ext.mvm_version"
    implementation "com.github.mirkowu.mvm:lib_webview:$ext.mvm_version"
    implementation "com.github.mirkowu.mvm:lib_photo:$ext.mvm_version"
```


