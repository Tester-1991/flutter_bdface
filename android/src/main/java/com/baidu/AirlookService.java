package com.baidu;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * 登录注册 以及个人资料  版本更新的接口
 * Created by LL on 2018/7/12/012.
 */

public interface AirlookService {

    int SMSAPP = 4;          //4-app手机注册或者登录
    int SMSUPDATEPHONE = 3;  //得到验证码  type修改手机号
    int SMSUPDATEPWD = 2;      //重置密码

    /**
     * 获取验证码
     */
    @GET("passport/app/getSmsCode")
    Observable<BaseModel<Object>> querySmsCode(@Query("loginName") String loginName  //登录名传入手机号
            , @Query("mobile") String mobile
            , @Query("type") int type);                                               //往上看


    /**
     * 短信验证码校验
     */
    @GET("passport/app/validateSmsCode")
    Observable<BaseModel<Object>> checkSmsCode(@Query("loginName") String loginName  //登录名传入手机号
            , @Query("mobile") String mobile
            , @Query("type") int type                                                //1-注册(pc)，2-重置密码，3-修改手机号,4-app手机注册或者登录
            , @Query("smsCode") String smsCode                                       //短信验证码
    );


    /**
     * 把deviceToken上传服务器
     *
     * @param userId
     * @param deviceToken
     * @param deviceType
     * @return
     */
    @POST("message/notice/register/{userId}/{deviceToken}/{deviceType}")
    Observable<BaseModel<Object>> putDeviceToken(@Path("userId") String userId
            , @Path("deviceToken") String deviceToken       //友盟token
            , @Path("deviceType") String deviceType);       //android

    /**
     * 退出登录
     */
    @FormUrlEncoded
    @POST("passport/app/logout")
    Observable<BaseModel<Object>> exitLogin(@Field("loginToken") String loginToken);



    /**
     * 首页获取用户空域未读消息个数
     */
    @GET("message/notice/count/{messageType}/{flyerId}")
    Observable<BaseModel<Integer>> queryAreaNoReadMsgCount(@Path("messageType") String messageType
            , @Path("flyerId") long flyerId);


    /**
     * 新增实名认证
     */
    @POST("passport/identity/personal/add")
    Observable<BaseModel<Object>> queryVerified(@Body RequestBody body);

    /**
     * 编辑实名认证
     */
    @POST("passport/identity/personal/edit")
    Observable<BaseModel<Object>> editVerified(@Body RequestBody body);

    /**
     * 补全人脸识别资料
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("passport/identity/personal/face")
    Observable<BaseModel<Object>> addFace(@Body RequestBody param);

    /**
     * 设置密码
     */
    @FormUrlEncoded
    @POST("passport/app/firstSetPassword")
    Observable<BaseModel<Object>> setPwd(@Field("loginName") String loginName
            , @Field("mobile") String mobile
            , @Field("password") String password);

    /**
     * 修改密码
     */
    @FormUrlEncoded
    @POST("passport/app/modifyPassword")
    Observable<BaseModel<Object>> updatePwd(@Field("loginName") String loginName
            , @Field("mobile") String mobile
            , @Field("password") String password);

    /**
     * 修改手机号
     */
    @FormUrlEncoded
    @POST("user/modifyMobile")
    Observable<BaseModel<Object>> modifyMobile(@Field("mobile") String mobile
            , @Field("smsCode") String smsCode);

    /**
     * 人脸标识
     */
    @FormUrlEncoded
    @POST("user/app/faceRecognFlag ")
    Observable<BaseModel<Object>> faceRecognFlag(@Field("flag") int flag); //0-未人脸识别，1-已经人脸识别


    /**
     * 申请飞行计划
     */
    @PUT("airapply/airspace-flight-plan/apply-for-flight/{id}")
    Observable<BaseModel<Boolean>> applyForFlight(@Path("id") String id);
}
