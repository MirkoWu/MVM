package com.mirkowu.lib_webview.dsbridge;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class CallInfo {
    public String data = null;
    public int callbackId;
    public String method;

    public CallInfo(String handlerName, int id, Object[] args) {
        if (args == null) args = new Object[0];
        data = new JSONArray(Arrays.asList(args)).toString();
        callbackId = id;
        method = handlerName;
    }

    @Override
    public String toString() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("method", method);
            jo.put("callbackId", callbackId);
            jo.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo.toString();
    }

}