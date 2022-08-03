package com.mirkowu.mvm.aidl.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mirkowu.lib_base.mediator.EmptyMediator;
import com.mirkowu.lib_base.util.ViewBindingUtilKt;
import com.mirkowu.lib_util.LogUtil;
import com.mirkowu.mvm.R;
import com.mirkowu.mvm.aidl.IMyAidlInterface;
import com.mirkowu.mvm.aidl.bean.Book;
import com.mirkowu.mvm.aidl.server.MyService;
import com.mirkowu.mvm.base.BaseActivity;
import com.mirkowu.mvm.databinding.ActivityClientBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ClientActivity extends BaseActivity<EmptyMediator> {

    public static void start(Context context) {
        Intent starter = new Intent(context, ClientActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }

    private ActivityClientBinding mBinding;

    private int count;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_client;
    }

    @Override
    protected void bindContentView() {
        mBinding = ActivityClientBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        //super.bindContentView();
    }

    @Override
    protected void initialize() {

//        ViewBindingUtilKt.bindingView(mBinding);
//        ViewBindingUtilKt.bindingView( );


        mBinding.btnAddBook.setOnClickListener(v -> {

            try {
                if (myAidlInterface != null) {
                    myAidlInterface.addBook(new Book(count, "名称" + count));
                }
                count++;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
        mBinding.btnQueryBooks.setOnClickListener(v -> {
            try {
                if (myAidlInterface != null) {
                    List<Book> list = myAidlInterface.getBookList();
                    mBinding.tvResult.setText(list.toString());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

//        Intent intent = new Intent(this, MyService.class);
        Intent intent = new Intent();
        intent.setClassName(this, "com.mirkowu.mvm.aidl.server.MyService");
        intent.setAction("com.mirkowu.mvm.aidl.IMyAidlInterface");

        bindService(intent, mConnection, BIND_AUTO_CREATE);

        // Messenger messenger = new Messenger(new Handler());

    }

    private IMyAidlInterface myAidlInterface;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            myAidlInterface = IMyAidlInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            myAidlInterface = null;
        }
    };

    @Override
    protected void onDestroy() {
        unbindService(mConnection);
        super.onDestroy();
    }
}