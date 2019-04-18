package com.baidu;

/**
 * 返回数据的基类
 * Created by LL on 2018/6/21/021.
 */

public class BaseModel<T> {

    int code;
    T data;
    String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
