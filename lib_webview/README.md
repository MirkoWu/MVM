# WebView组件库

### 使用

```
//初始化WebView, 默认在当前进程
WebViewUtil.init(this);

//初始化WebView, 启用新的进程
WebViewUtil.init(this,true);
```

### 功能
- [x] 支持X5浏览器
- [x] 支持JsBridge
- [x] 支持进度条
- [x] 支持错误处理
- [x] 支持文件选取
- [x] 支持拍照
- [x] 支持内置权限处理
- [x] 支持定义标题栏
- [x] 支持Fragment
- [x] 支持多进程，预启动


### 已知问题
- [ ] 多进程切换语言问题
- [ ] 多进程相关问题

### 感谢
[X5浏览器](https://x5.tencent.com/docs/index.html)
[JsBridge](https://github.com/lzyzsd/JsBridge)

引用版本
```
implementation 'com.tencent.tbs.tbssdk:sdk:43939'

implementation 'com.github.lzyzsd:jsbridge:1.0.4'
```
