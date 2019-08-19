package com.baidu;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.baidu.aip.FaceEnvironment;
import com.baidu.aip.FaceSDKManager;
import com.baidu.idl.facesdk.FaceTracker;
import com.baidu.bdface.APIService;
import com.baidu.bdface.exception.FaceError;
import com.baidu.bdface.model.AccessToken;
import com.baidu.bdface.utils.OnResultListener;

/**
 * 石岩
 * update 2019.01.15
 */
public class BdFaceUtil {

    private Context context;

    //百度人脸识别是否正在请求
    private boolean faceLoading = false;

    //百度人脸识别是否识别成功
    private boolean bdFace = false;

    //百度人脸识别
    private Handler handler = new Handler(Looper.getMainLooper());

//    public void setContext(Context context) {
//        this.context = context;
//    }

    private BdFaceUtil() {
    }

    public static BdFaceUtil getInstance() {

        return BdFaceUtilHolder.instance;
    }

    private static class BdFaceUtilHolder {

        private static final BdFaceUtil instance = new BdFaceUtil();

    }

    public void init(Context context) {

        this.context = context;

        //如果正在初始化 跳出方法
        if (faceLoading) {
            return;
        }

        //人脸识别正在初始化
        faceLoading = true;

        initLib();

        APIService.getInstance().init(context);

        APIService.getInstance().setGroupId(Config.groupID);

        // 用ak，sk获取token, 调用在线api，如：注册、识别等。为了ak、sk安全，建议放您的服务器，
        APIService.getInstance().initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                handler.post(() -> {
                    Log.e("bdface","人脸识别鉴权成功");
                    bdFace = true;
                    faceLoading = false;
                });
            }

            @Override
            public void onError(FaceError error) {
                bdFace = false;
                faceLoading = false;
                error.printStackTrace();

            }
        }, context, Config.apiKey, Config.secretKey);
    }

    /**
     * 初始化人脸识别SDK
     */
    private void initLib() {
        // 为了android和ios 区分授权，appId=appname_face_android ,其中appname为申请sdk时的应用名
        // 应用上下文
        // 申请License取得的APPID
        // assets目录下License文件名
        FaceSDKManager.getInstance().init(context, Config.licenseID, Config.licenseFileName);
        setFaceConfig();
    }

    /**
     * 设置脸部配置
     */
    private void setFaceConfig() {
        FaceTracker tracker = FaceSDKManager.getInstance().getFaceTracker(context);  //.getFaceConfig();
        // SDK初始化已经设置完默认参数（推荐参数），您也根据实际需求进行数值调整

        // 模糊度范围 (0-1) 推荐小于0.7
        tracker.set_blur_thr(FaceEnvironment.VALUE_BLURNESS);
        // 光照范围 (0-1) 推荐大于40
        tracker.set_illum_thr(FaceEnvironment.VALUE_BRIGHTNESS);
        // 裁剪人脸大小
        tracker.set_cropFaceSize(FaceEnvironment.VALUE_CROP_FACE_SIZE);
        // 人脸yaw,pitch,row 角度，范围（-45，45），推荐-15-15
        tracker.set_eulur_angle_thr(15, 15, 15);

        // 最小检测人脸（在图片人脸能够被检测到最小值）80-200， 越小越耗性能，推荐120-200
        tracker.set_min_face_size(FaceEnvironment.VALUE_MIN_FACE_SIZE);
        //
        tracker.set_notFace_thr(FaceEnvironment.VALUE_NOT_FACE_THRESHOLD);
        // 人脸遮挡范围 （0-1） 推荐小于0.5
        tracker.set_occlu_thr(FaceEnvironment.VALUE_OCCLUSION);
        // 是否进行质量检测
        tracker.set_isCheckQuality(true);
        // 是否进行活体校验
        tracker.set_isVerifyLive(false);
    }

    public boolean isBdFace() {

        return bdFace;
    }
}
