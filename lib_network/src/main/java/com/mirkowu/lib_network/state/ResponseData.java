package com.mirkowu.lib_network.state;

import com.mirkowu.lib_network.ErrorBean;
import com.mirkowu.lib_network.ErrorType;

public class ResponseData<T> {
    public RequestState state;
    public ErrorBean error;
    public T data;

    private ResponseData() {
    }

    private ResponseData(RequestState state) {
        this.state = state;
    }

    private ResponseData(T data) {
        this.data = data;
        this.state = RequestState.SUCCESS;
    }

    private ResponseData(ErrorBean error) {
        this.error = error;
        this.state = RequestState.FAILURE;
    }

    public boolean isSuccess() {
        return state == RequestState.SUCCESS;
    }

    public boolean isFailure() {
        return state == RequestState.FAILURE;
    }

    public boolean isLoading() {
        return state == RequestState.LOADING;
    }

    public boolean isFinish() {
        return state == RequestState.FINISH;
    }

    public static <T> ResponseData<T> create(Object object) {
        if (object instanceof RequestState) {
            return new ResponseData((RequestState) object);
        } else if (object instanceof ErrorBean) {
            return new ResponseData((ErrorBean) object);
        } else {
            return new ResponseData(object);
        }
    }

    public static <T> ResponseData<T> loading() {
        return new ResponseData(RequestState.LOADING);
    }

    public static <T> ResponseData<T> finish() {
        return new ResponseData(RequestState.FINISH);
    }

    public static <T> ResponseData<T> success(T data) {
        return new ResponseData(data);
    }

    public static <T> ResponseData<T> error(ErrorBean bean) {
        return new ResponseData(bean);
    }

    public static <T> ResponseData<T> error(ErrorType type, int code, String msg, Throwable e) {
        return new ResponseData(new ErrorBean(type, code, msg, e));
    }
}
