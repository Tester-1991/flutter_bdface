package com.baidu;

/**
 * 自定义的返回数据的错误
 * Created by LL on 2018/5/27/027.
 */

public class NetThrowable extends Exception {

    String errorMsg;
    int errorCode;

    public NetThrowable(int errorCode, String errorMsg) {
        super(errorMsg);
        this.errorMsg = errorMsg;
        this.errorCode = errorCode;
    }
}
