package com.mirkowu.lib_webview.dsbridge;

public interface IDSBridge {

    void addJavascriptObject(Object object, String namespace);

    void removeJavascriptObject(String namespace);

    public void removeAllJavascriptObject();

    public <T> void callHandler(String method, Object[] args, final OnReturnValue<T> handler);

    public void callHandler(String method, Object[] args);

    public <T> void callHandler(String method, OnReturnValue<T> handler);


    public void hasJavascriptMethod(String handlerName, OnReturnValue<Boolean> existCallback);

    void setJavascriptCloseWindowListener(JavascriptCloseWindowListener listener);

    void evaluateJavascript(final String script);

    void disableJavascriptDialogBlock(boolean disable);
}
