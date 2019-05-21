package com.baidu;

/**
 * 自定义的observer
 * Created by LL on 2018/6/21/021.
 */

import com.google.gson.JsonParseException;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.UnknownHostException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 重写了错误方法 根据错误类型做了相应操作
 * 空实现了其他方法除了onnext
 * Created by LL on 2018/5/27/027.
 */

public abstract class NetObserver<T> implements Observer<BaseModel<T>> {

    public static final String NETEXCEPTION = "网络连接异常，请检查网络后重试";

    public static final String GSONEXCEPTION = "GSON解析错误";


    @Override
    public void onSubscribe(Disposable d) {
    }

    @Override
    public void onNext(BaseModel<T> baseModel) {
        if (baseModel.getCode() == 200) {
            if (baseModel.getData() == null) {
                doOnNullData();
            } else {
                doOnSuccess(baseModel.getData());
            }
        } else {
            onError(new NetThrowable(baseModel.getCode(), baseModel.getMsg()));
        }
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof HttpException) {
        } else if (e instanceof JsonParseException
                || e instanceof JSONException) {
        } else if (e instanceof ConnectException) {
        } else if (e instanceof UnknownHostException) {
        } else {
            onErrorData(e);
        }
    }

    protected  void onErrorData(Throwable e){

    }

    @Override
    public void onComplete() {
    }

    /**
     * 处理错误情况
     *
     * @param code
     * @param msg
     */
    public void doOnRequestCodeError(int code, String msg) {
    }

    /**
     *
     */
    public void doOnNullData() {

    }

    public abstract void doOnSuccess(T t);

}
