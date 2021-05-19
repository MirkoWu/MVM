package com.mirkowu.mvm.network;

import com.mirkowu.lib_network.AbsRxObserver;
import com.mirkowu.lib_network.ApiException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.observers.DisposableObserver;
import retrofit2.HttpException;

public abstract class RxObserver<T> extends AbsRxObserver<T> {

}
