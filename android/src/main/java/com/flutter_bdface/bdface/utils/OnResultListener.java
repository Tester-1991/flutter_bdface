/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.flutter_bdface.bdface.utils;


import com.flutter_bdface.bdface.exception.FaceError;

public interface OnResultListener<T> {
    void onResult(T result);

    void onError(FaceError error);
}
