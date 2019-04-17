package com.flutter_bdface;


import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * 图片上传接口
 * Created by LL on 2018/7/13/013.
 */

public interface UpLoadImageService {

    @Multipart
    @POST("image/upload")
    Call<BaseModel<UpLoadingModel>> uploadImaeg(@Part MultipartBody.Part image);
}
