package com.mirkowu.mvm.ui.websocket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mirkowu.mvm.R;

import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, WebSocketActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }

    WSClient wsClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_socket);

        initSocket();
    }


    public void initSocket() {
        URI uri = URI.create("ws://www.aabbc.com");
        wsClient = new WSClient(uri) {
            @Override
            public void onMessage(String message) {
                super.onMessage(message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                super.onClose(code, reason, remote);
                if (code != 1000) {//意外断开马上重连
                    reconnectWS();
                }
            }
        };
        wsClient.setConnectionLostTimeout(10 * 1000);
//        wsClient.addHeader();
        new Thread(() -> {
            try {
                wsClient.connectBlocking();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

    }

    public void reconnectWS() {
        if (wsClient != null) {
            new Thread(() -> {
                try {
                    wsClient.reconnectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        }
    }

    public void closeWS() {
        if (wsClient != null) {
            wsClient.close();
        }
    }

    public void clickConnect(View view) {
        initSocket();
    }

    public void clickSendMessage(View view) {
        if(wsClient!=null &&wsClient.isOpen()){

        }else {

        }
    }


    @Override
    protected void onDestroy() {
        closeWS();
        super.onDestroy();
    }
}