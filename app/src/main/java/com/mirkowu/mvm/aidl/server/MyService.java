package com.mirkowu.mvm.aidl.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.mirkowu.lib_util.LogUtil;
import com.mirkowu.mvm.aidl.IMyAidlInterface;
import com.mirkowu.mvm.aidl.bean.Book;

import java.util.ArrayList;
import java.util.List;

public class MyService extends Service {
    private List<Book> mList=new ArrayList<>();
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.e("MyService = "+getPackageName());
        return mIBinder;
    }

    public  IBinder mIBinder = new IMyAidlInterface.Stub() {
        @Override
        public void addBook(Book book) throws RemoteException {
            mList.add(book);
        }

        @Override
        public List<Book> getBookList() throws RemoteException {
            return mList;
        }
    };
}