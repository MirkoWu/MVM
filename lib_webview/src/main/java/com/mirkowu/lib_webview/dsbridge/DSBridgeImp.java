package com.mirkowu.lib_webview.dsbridge;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.webkit.JavascriptInterface;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.mirkowu.lib_webview.CommonWebView;
import com.mirkowu.lib_webview.client.BaseWebChromeClient;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DSBridgeImp implements IDSBridge {


    private static final String BRIDGE_NAME = "_dsbridge";
    private static final String LOG_TAG = "dsBridge";
    private static boolean isDebug = false;
    private Map<String, Object> javaScriptNamespaceInterfaces = new HashMap();
    int callID = 0;
    private static final int EXEC_SCRIPT = 1;
    private static final int LOAD_URL = 2;
    private static final int LOAD_URL_WITH_HEADERS = 3;
    private static final int JS_CLOSE_WINDOW = 4;
    private static final int JS_RETURN_VALUE = 5;
    BridgeHandler mainThreadHandler = null;
    private volatile boolean alertboxBlock = true;
    private JavascriptCloseWindowListener javascriptCloseWindowListener = null;
    private ArrayList<CallInfo> callInfoList = new ArrayList<>();

    private CommonWebView mWebView;
    private Context mContext;

    public DSBridgeImp(@NonNull CommonWebView webView) {
        this.mContext = webView.getContext();
        this.mWebView = webView;
        init();
    }


    class BridgeHandler extends Handler {
        //  Using WeakReference to avoid memory leak
        WeakReference<Activity> mActivityReference;

        BridgeHandler(Activity activity) {
            mActivityReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final Activity activity = mActivityReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case EXEC_SCRIPT:
                        _evaluateJavascript((String) msg.obj);
                        break;
                    case LOAD_URL:
                        mWebView.loadUrl((String) msg.obj);
                        break;
                    case LOAD_URL_WITH_HEADERS: {
                        RequestInfo info = (RequestInfo) msg.obj;
                        mWebView.loadUrl(info.url, info.headers);
                    }
                    break;
                    case JS_CLOSE_WINDOW: {
                        if (javascriptCloseWindowListener == null
                                || javascriptCloseWindowListener.onClose()) {
                            if (mContext instanceof Activity) {
                                ((Activity) mContext).onBackPressed();
                            }
                        }
                    }
                    break;
                    case JS_RETURN_VALUE: {
                        int id = msg.arg1;
                        OnReturnValue handler = handlerMap.get(id);
                        if (handler != null) {
                            if (isDebug) {
                                handler.onValue(msg.obj);
                            } else {
                                try {
                                    handler.onValue(msg.obj);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            if (msg.arg2 == 1) {
                                handlerMap.remove(id);
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    class RequestInfo {
        String url;
        Map<String, String> headers;

        RequestInfo(String url, Map<String, String> additionalHttpHeaders) {
            this.url = url;
            this.headers = additionalHttpHeaders;
        }
    }

    Map<Integer, OnReturnValue> handlerMap = new HashMap<>();

    /**
     * Set debug mode. if in debug mode, some errors will be prompted by a dialog
     * and the exception caused by the native handlers will not be captured.
     *
     * @param enabled
     */
    public static void setWebContentsDebuggingEnabled(boolean enabled) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(enabled);
        }
        isDebug = enabled;
    }


    @Keep
    void init() {
        mainThreadHandler = new BridgeHandler((Activity) mContext);
        addInternalJavascriptObject();
        mWebView.addJavascriptInterface(new Object() {

            private void PrintDebugInfo(String error) {
                Log.d(LOG_TAG, error);
                if (isDebug) {
                    evaluateJavascript(String.format("alert('%s')", "DEBUG ERR MSG:\\n" + error.replaceAll("\\'", "\\\\'")));
                }
            }

            @Keep
            @JavascriptInterface
            public String call(String methodName, String argStr) throws JSONException {
                String error = "Js bridge  called, but can't find a corresponded " +
                        "JavascriptInterface object , please check your code!";
                String[] nameStr = parseNamespace(methodName.trim());
                methodName = nameStr[1];
                Object jsb = javaScriptNamespaceInterfaces.get(nameStr[0]);
                JSONObject ret = new JSONObject();
                ret.put("code", -1);
                if (jsb == null) {
                    PrintDebugInfo(error);
                    return ret.toString();
                }
                Object arg;
                Method method = null;
                String callback = null;

                try {
                    JSONObject args = new JSONObject(argStr);
                    if (args.has("_dscbstub")) {
                        callback = args.getString("_dscbstub");
                    }
                    arg = args.get("data");
                } catch (JSONException e) {
                    error = String.format("The argument of \"%s\" must be a JSON object string!", methodName);
                    PrintDebugInfo(error);
                    e.printStackTrace();
                    return ret.toString();
                }


                Class<?> cls = jsb.getClass();
                boolean asyn = false;
                try {
                    method = cls.getDeclaredMethod(methodName,
                            new Class[]{Object.class, CompletionHandler.class});
                    asyn = true;
                } catch (Exception e) {
                    try {
                        method = cls.getDeclaredMethod(methodName, new Class[]{Object.class});
                    } catch (Exception ex) {

                    }
                }

                if (method == null) {
                    error = "Not find method \"" + methodName + "\" implementation! please check if the  signature or namespace of the method is right ";
                    PrintDebugInfo(error);
                    return ret.toString();
                }

                JavascriptInterface annotation = method.getAnnotation(JavascriptInterface.class);
                if (annotation == null) {
                    error = "Method " + methodName + " is not invoked, since  " +
                            "it is not declared with JavascriptInterface annotation! ";
                    PrintDebugInfo(error);
                    return ret.toString();
                }

                Object retData;
                method.setAccessible(true);
                try {
                    if (asyn) {
                        final String cb = callback;
                        method.invoke(jsb, arg, new CompletionHandler() {

                            @Override
                            public void complete(Object retValue) {
                                complete(retValue, true);
                            }

                            @Override
                            public void complete() {
                                complete(null, true);
                            }

                            @Override
                            public void setProgressData(Object value) {
                                complete(value, false);
                            }

                            private void complete(Object retValue, boolean complete) {
                                try {
                                    JSONObject ret = new JSONObject();
                                    ret.put("code", 0);
                                    ret.put("data", retValue);
                                    //retValue = URLEncoder.encode(ret.toString(), "UTF-8").replaceAll("\\+", "%20");
                                    if (cb != null) {
                                        //String script = String.format("%s(JSON.parse(decodeURIComponent(\"%s\")).data);", cb, retValue);
                                        String script = String.format("%s(%s.data);", cb, ret.toString());
                                        if (complete) {
                                            script += "delete window." + cb;
                                        }
                                        evaluateJavascript(script);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        retData = method.invoke(jsb, arg);
                        ret.put("code", 0);
                        ret.put("data", retData);
                        return ret.toString();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    error = String.format("Call failed：The parameter of \"%s\" in Java is invalid.", methodName);
                    PrintDebugInfo(error);
                    return ret.toString();
                }
                return ret.toString();
            }

        }, BRIDGE_NAME);

    }

    private String[] parseNamespace(String method) {
        int pos = method.lastIndexOf('.');
        String namespace = "";
        if (pos != -1) {
            namespace = method.substring(0, pos);
            method = method.substring(pos + 1);
        }
        return new String[]{namespace, method};
    }

    @Keep
    private void addInternalJavascriptObject() {
        addJavascriptObject(new Object() {

            @Keep
            @JavascriptInterface
            public boolean hasNativeMethod(Object args) throws JSONException {

                JSONObject jsonObject = (JSONObject) args;
                String methodName = jsonObject.getString("name").trim();
                String type = jsonObject.getString("type").trim();
                String[] nameStr = parseNamespace(methodName);
                Object jsb = javaScriptNamespaceInterfaces.get(nameStr[0]);
                if (jsb != null) {
                    Class<?> cls = jsb.getClass();
                    boolean asyn = false;
                    Method method = null;
                    try {
                        method = cls.getDeclaredMethod(nameStr[1],
                                new Class[]{Object.class, CompletionHandler.class});
                        asyn = true;
                    } catch (Exception e) {
                        try {
                            method = cls.getDeclaredMethod(nameStr[1], new Class[]{Object.class});
                        } catch (Exception ex) {

                        }
                    }
                    if (method != null) {
                        JavascriptInterface annotation = method.getAnnotation(JavascriptInterface.class);
                        if (annotation != null) {
                            if ("all".equals(type) || (asyn && "asyn".equals(type) || (!asyn && "syn".equals(type)))) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            }

            @Keep
            @JavascriptInterface
            public String closePage(Object object) throws JSONException {
                Message msg = new Message();
                msg.what = JS_CLOSE_WINDOW;
                mainThreadHandler.sendMessage(msg);
                return null;
            }

            @Keep
            @JavascriptInterface
            public void disableJavascriptDialogBlock(Object object) throws JSONException {
                JSONObject jsonObject = (JSONObject) object;
                boolean disable = jsonObject.getBoolean("disable");
                DSBridgeImp.this.disableJavascriptDialogBlock(disable);
            }

            @Keep
            @JavascriptInterface
            public void dsinit(Object jsonObject) {
                dispatchStartupQueue();
            }

            @Keep
            @JavascriptInterface
            public void returnValue(Object obj) throws JSONException {
                JSONObject jsonObject = (JSONObject) obj;
                Message msg = new Message();
                msg.what = JS_RETURN_VALUE;
                msg.arg1 = jsonObject.getInt("id");
                msg.arg2 = jsonObject.getBoolean("complete") ? 1 : 0;
                if (jsonObject.has("data")) {
                    msg.obj = jsonObject.get("data");
                }
                mainThreadHandler.sendMessage(msg);
            }

        }, "_dsb");
    }

    private void _evaluateJavascript(String script) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.evaluateJavascript(script, null);
        } else {
            loadUrl("javascript:" + script);
        }
    }

    /**
     * This method can be called in any thread, and if it is not called in the main thread,
     * it will be automatically distributed to the main thread.
     *
     * @param script
     */
    public void evaluateJavascript(final String script) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            _evaluateJavascript(script);
        } else {
            Message msg = new Message();
            msg.what = EXEC_SCRIPT;
            msg.obj = script;
            mainThreadHandler.sendMessage(msg);
        }
    }

    /**
     * This method can be called in any thread, and if it is not called in the main thread,
     * it will be automatically distributed to the main thread.
     *
     * @param url
     */
    public void loadUrl(String url) {
        Message msg = new Message();
        msg.what = LOAD_URL;
        msg.obj = url;
        mainThreadHandler.sendMessage(msg);
    }

    /**
     * This method can be called in any thread, and if it is not called in the main thread,
     * it will be automatically distributed to the main thread.
     *
     * @param url
     * @param additionalHttpHeaders
     */
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        Message msg = new Message();
        msg.what = LOAD_URL_WITH_HEADERS;
        msg.obj = new RequestInfo(url, additionalHttpHeaders);
        mainThreadHandler.sendMessage(msg);
    }

    /**
     * set a listener for javascript closing the current activity.
     */
    @Override
    public void setJavascriptCloseWindowListener(JavascriptCloseWindowListener listener) {
        javascriptCloseWindowListener = listener;
    }

    @Override
    public void disableJavascriptDialogBlock(boolean disable) {
        boolean alertboxBlock = !disable;
        WebChromeClient chromeClient = mWebView.getWebChromeClient();
        if (chromeClient instanceof BaseWebChromeClient) {
            ((BaseWebChromeClient) chromeClient).setAlertBoxBlock(alertboxBlock);
        }
    }

    private synchronized void dispatchStartupQueue() {
        for (CallInfo info : callInfoList) {
            dispatchJavascriptCall(info);
        }
        callInfoList = null;
    }

    private void dispatchJavascriptCall(CallInfo info) {
        evaluateJavascript(String.format("window._handleMessageFromNative(%s)", info.toString()));
    }

    @Override
    public synchronized <T> void callHandler(String method, Object[] args, OnReturnValue<T> handler) {
        CallInfo callInfo = new CallInfo(method, callID, args);
        if (handler != null) {
            handlerMap.put(callID++, handler);
        }

        if (callInfoList != null) {
            callInfoList.add(callInfo);
        } else {
            dispatchJavascriptCall(callInfo);
        }

    }

    @Override
    public void callHandler(String method, Object[] args) {
        callHandler(method, args, null);
    }

    @Override
    public <T> void callHandler(String method, OnReturnValue<T> handler) {
        callHandler(method, null, handler);
    }


    /**
     * Test whether the handler exist in javascript
     *
     * @param handlerName
     * @param existCallback
     */
    @Override
    public void hasJavascriptMethod(String handlerName, OnReturnValue<Boolean> existCallback) {
        callHandler("_hasJavascriptMethod", new Object[]{handlerName}, existCallback);
    }


    /**
     * Add a java object which implemented the javascript interfaces to dsBridge with namespace.
     * Remove the object using {@link #removeJavascriptObject(String) removeJavascriptObject(String)}
     *
     * @param object
     * @param namespace if empty, the object have no namespace.
     */
    @Override
    public void addJavascriptObject(Object object, String namespace) {
        if (namespace == null) {
            namespace = "";
        }
        if (object != null) {
            javaScriptNamespaceInterfaces.put(namespace, object);
        }
    }

    /**
     * remove the javascript object with supplied namespace.
     *
     * @param namespace
     */
    @Override
    public void removeJavascriptObject(String namespace) {
        if (namespace == null) {
            namespace = "";
        }
        javaScriptNamespaceInterfaces.remove(namespace);
    }

    @Override
    public void removeAllJavascriptObject() {
        javaScriptNamespaceInterfaces.clear();
    }

    public void onDestroy() {
        mContext = null;
        mWebView = null;
        removeAllJavascriptObject();
    }
}
