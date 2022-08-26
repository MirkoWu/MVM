package com.mirkowu.mvm.ui.websocket;

import com.mirkowu.lib_util.LogUtil;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Map;

public class WSClient extends WebSocketClient {
    public static final String TAG = WSClient.class.getSimpleName();

    public WSClient(URI serverUri) {
        super(serverUri);
    }

    public WSClient(URI serverUri, Draft protocolDraft) {
        super(serverUri, protocolDraft);
    }

    public WSClient(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
    }

    public WSClient(URI serverUri, Draft protocolDraft, Map<String, String> httpHeaders) {
        super(serverUri, protocolDraft, httpHeaders);
    }

    public WSClient(URI serverUri, Draft protocolDraft, Map<String, String> httpHeaders, int connectTimeout) {
        super(serverUri, protocolDraft, httpHeaders, connectTimeout);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        LogUtil.d(TAG, "onOpen");
    }

    @Override
    public void onMessage(String message) {
        LogUtil.d(TAG, "onMessage = " + message);

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        LogUtil.d(TAG, "onClose: " + code + " " + reason);

    }

    @Override
    public void onError(Exception ex) {
        LogUtil.d(TAG, "onError" + ex.toString());
        ex.printStackTrace();

    }
}
