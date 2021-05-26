package com.mirkowu.lib_webview.jsbridge;


public interface WebViewJavascriptBridge {

    public void loadUrl(String url);

    public void send(String data);

    public void send(String data, CallBackFunction responseCallback);

    public void registerHandler(String handlerName, BridgeHandler handler);

    public void callHandler(String handlerName, String data, CallBackFunction callBack);

    public void handlerReturnData(String url);


    public void flushMessageQueue();

    public void loadLocalJS();
}
