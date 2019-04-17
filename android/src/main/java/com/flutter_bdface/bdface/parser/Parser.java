/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.flutter_bdface.bdface.parser;


import com.flutter_bdface.bdface.exception.FaceError;

/**
 * JSON解析
 * @param <T>
 */
public interface Parser<T> {
    T parse(String json) throws FaceError;
}
