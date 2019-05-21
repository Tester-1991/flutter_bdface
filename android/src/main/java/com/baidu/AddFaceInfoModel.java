package com.baidu;

import java.io.Serializable;

/**
 * 补全人脸识别资料的上传接口
 */
public class AddFaceInfoModel implements Serializable {

    String faceToken;
    String faceUrl;

    public AddFaceInfoModel(String faceToken, String faceUrl) {
        this.faceToken = faceToken;
        this.faceUrl = faceUrl;
    }

    public String getFaceToken() {
        return faceToken;
    }

    public void setFaceToken(String faceToken) {
        this.faceToken = faceToken;
    }

    public String getFaceUrl() {
        return faceUrl;
    }

    public void setFaceUrl(String faceUrl) {
        this.faceUrl = faceUrl;
    }

}
