/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.bdface;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.AddFaceInfoModel;
import com.baidu.FlutterBdfacePlugin;
import com.baidu.NetObserver;
import com.baidu.aip.FaceSDKManager;
import com.baidu.aip.ImageFrame;
import com.baidu.aip.face.CameraImageSource;
import com.baidu.aip.face.DetectRegionProcessor;
import com.baidu.aip.face.FaceDetectManager;
import com.baidu.aip.face.FaceFilter;
import com.baidu.aip.face.PreviewView;
import com.baidu.aip.face.camera.ICameraControl;
import com.baidu.aip.face.camera.PermissionCallback;
import com.baidu.idl.facesdk.FaceInfo;
import com.baidu.BaseModel;
import com.baidu.BdFaceUtil;
import com.baidu.BdRegResultModel;
import com.baidu.Constant;
import com.baidu.Net;
import com.baidu.PreferenceUtil;
import com.baidu.R;
import com.baidu.StringUtil;
import com.baidu.ToastUtils;
import com.baidu.UpLoadingModel;
import com.baidu.bdface.exception.FaceError;
import com.baidu.bdface.model.RegResult;
import com.baidu.bdface.utils.ImageSaveUtil;
import com.baidu.bdface.utils.OnResultListener;
import com.baidu.bdface.widget.BrightnessTools;
import com.baidu.bdface.widget.FaceRoundView;
import com.baidu.bdface.widget.WaveHelper;
import com.baidu.bdface.widget.WaveView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.baidu.bdface.utils.Base64RequestBody.readFile;


/**
 * 实时检测调用identify进行人脸识别，MainActivity未给出改示例的入口，开发者可以在MainActivity调用
 * Intent intent = new Intent(MainActivity.this, FaceDetectActivity.class);
 * startActivity(intent);
 */
public class FaceDetectActivity extends AppCompatActivity {

    private static final int MSG_INITVIEW = 1001;
    private static final int MSG_BEGIN_DETECT = 1002;
    private TextView nameTextView;
    private PreviewView previewView;
    private View mInitView;
    private FaceRoundView rectView;
    private boolean mGoodDetect = false;
    private static final double ANGLE = 15;
    private ImageView closeIv;
    private boolean mDetectStoped = false;
    private ImageView mSuccessView;
    private Handler mHandler;
    private String mCurTips;
    private boolean mUploading = false;
    private long mLastTipsTime = 0;
    private int mCurFaceId = -1;

    private FaceDetectManager faceDetectManager;
    private DetectRegionProcessor cropProcessor = new DetectRegionProcessor();
    private WaveHelper mWaveHelper;
    private WaveView mWaveview;
    private int mBorderColor = Color.parseColor("#28FFFFFF");
    private int mBorderWidth = 0;
    private int mScreenW;
    private int mScreenH;
    private boolean mSavedBmp = false;
    // 开始人脸检测
    private boolean mBeginDetect = false;

    private TextView tvCancle;

    //保存的路径
    private String filePath;

    public static final String EXTRA_KEY_FROM = "from";

    public static final String REGIST = "regist";                       //注册
    public static final String REGISTFORRESULT = "registForResult";     //注册并返回结果
    public static final String FLY = "fly";
    public static final String LOGINFORRESULT = "login";                         //登录

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_detected);
        faceDetectManager = new FaceDetectManager(this);
        initScreen();
        initFace();
    }

    public void initFace() {
        initView();
        mHandler = new InnerHandler(this);
        mHandler.sendEmptyMessageDelayed(MSG_INITVIEW, 200);
        mHandler.sendEmptyMessageDelayed(MSG_BEGIN_DETECT, 200);
    }

    private void initScreen() {
        WindowManager manager = getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        mScreenW = outMetrics.widthPixels;
        mScreenH = outMetrics.heightPixels;
    }


    private void initView() {
        mInitView = findViewById(R.id.camera_layout);
        previewView = (PreviewView) findViewById(R.id.preview_view);

        rectView = (FaceRoundView) findViewById(R.id.rect_view);
        final CameraImageSource cameraImageSource = new CameraImageSource(this);
        cameraImageSource.setPreviewView(previewView);

        faceDetectManager.setImageSource(cameraImageSource);
        faceDetectManager.setOnFaceDetectListener(new FaceDetectManager.OnFaceDetectListener() {
            @Override
            public void onDetectFace(final int retCode, FaceInfo[] infos, ImageFrame frame) {
                if (mUploading) {
                    return;
                }
                String str = "";
                if (retCode == 0) {
                    if (infos != null && infos[0] != null) {
                        FaceInfo info = infos[0];
                        boolean distance = false;
                        if (info != null && frame != null) {
                            if (info.mWidth >= (0.9 * frame.getWidth())) {
                                distance = false;
                                str = getResources().getString(R.string.detect_zoom_out);
                            } else if (info.mWidth <= 0.4 * frame.getWidth()) {
                                distance = false;
                                str = getResources().getString(R.string.detect_zoom_in);
                            } else {
                                distance = true;
                            }
                        }
                        boolean headUpDown;
                        if (info != null) {
                            if (info.headPose[0] >= ANGLE) {
                                headUpDown = false;
                                str = getResources().getString(R.string.detect_head_up);
                            } else if (info.headPose[0] <= -ANGLE) {
                                headUpDown = false;
                                str = getResources().getString(R.string.detect_head_down);
                            } else {
                                headUpDown = true;
                            }

                            boolean headLeftRight;
                            if (info.headPose[1] >= ANGLE) {
                                headLeftRight = false;
                                str = getResources().getString(R.string.detect_head_left);
                            } else if (info.headPose[1] <= -ANGLE) {
                                headLeftRight = false;
                                str = getResources().getString(R.string.detect_head_right);
                            } else {
                                headLeftRight = true;
                            }

                            if (distance && headUpDown && headLeftRight) {
                                mGoodDetect = true;
                            } else {
                                mGoodDetect = false;
                            }
                        }
                    }
                } else if (retCode == 1) {
                    str = getResources().getString(R.string.detect_head_up);
                } else if (retCode == 2) {
                    str = getResources().getString(R.string.detect_head_down);
                } else if (retCode == 3) {
                    str = getResources().getString(R.string.detect_head_left);
                } else if (retCode == 4) {
                    str = getResources().getString(R.string.detect_head_right);
                } else if (retCode == 5) {
                    str = getResources().getString(R.string.detect_low_light);
                } else if (retCode == 6) {
                    str = getResources().getString(R.string.detect_face_in);
                } else if (retCode == 7) {
                    str = getResources().getString(R.string.detect_face_in);
                } else if (retCode == 10) {
                    str = getResources().getString(R.string.detect_keep);
                } else if (retCode == 11) {
                    str = getResources().getString(R.string.detect_occ_right_eye);
                } else if (retCode == 12) {
                    str = getResources().getString(R.string.detect_occ_left_eye);
                } else if (retCode == 13) {
                    str = getResources().getString(R.string.detect_occ_nose);
                } else if (retCode == 14) {
                    str = getResources().getString(R.string.detect_occ_mouth);
                } else if (retCode == 15) {
                    str = getResources().getString(R.string.detect_right_contour);
                } else if (retCode == 16) {
                    str = getResources().getString(R.string.detect_left_contour);
                } else if (retCode == 17) {
                    str = getResources().getString(R.string.detect_chin_contour);
                }

                boolean faceChanged = true;
                if (infos != null && infos[0] != null) {
                    Log.d("DetectLogin", "face id is:" + infos[0].face_id);
                    if (infos[0].face_id == mCurFaceId) {
                        faceChanged = false;
                    } else {
                        faceChanged = true;
                    }
                    mCurFaceId = infos[0].face_id;
                }

                if (faceChanged) {
                    showProgressBar(false);
                    onRefreshSuccessView(false, false);
                }

                final int resultCode = retCode;
                if (!(mGoodDetect && retCode == 0)) {
                    if (faceChanged) {
                        showProgressBar(false);
                        onRefreshSuccessView(false, false);
                    }
                }

                if (retCode == 6 || retCode == 7 || retCode < 0) {
                    rectView.processDrawState(true);
                } else {
                    rectView.processDrawState(false);
                }

                mCurTips = str;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ((System.currentTimeMillis() - mLastTipsTime) > 1000) {
                            if (StringUtil.isEmpty(mCurTips)) {
                                nameTextView.setVisibility(View.GONE);
                            } else {
                                nameTextView.setVisibility(View.VISIBLE);
                                nameTextView.setTextColor(getResources().getColor(R.color.whitecolor));
                                nameTextView.setText(mCurTips);
                            }
                            mLastTipsTime = System.currentTimeMillis();
                        }
                        if (mGoodDetect && resultCode == 0) {
                            nameTextView.setVisibility(View.GONE);
                            showProgressBar(true);
                        }
                    }
                });

                if (infos == null) {
                    mGoodDetect = false;
                }


            }
        });
        faceDetectManager.setOnTrackListener(new FaceFilter.OnTrackListener() {
            @Override
            public void onTrack(FaceFilter.TrackedModel trackedModel) {
                if (trackedModel.meetCriteria() && mGoodDetect) {
                    mUploading = true;
                    mGoodDetect = false;
                    if (!mSavedBmp && mBeginDetect) {
                        if (saveFaceBmp(trackedModel)) {
                            //暂时不识别照片
                            mBeginDetect = false;
                            //如果状态是注册 或者注册并返回结果 或者是首页注册的
                            if (getIntent().getStringExtra(EXTRA_KEY_FROM).equals(REGISTFORRESULT)
                                    || getIntent().getStringExtra(EXTRA_KEY_FROM).equals(REGIST)) {


                                //注册
                                String facePath = ImageSaveUtil.loadCameraBitmapPath(FaceDetectActivity.this
                                        , "head_tmp.jpg");
                                reg(facePath);
                            } else {//其他状态 登录状态
                                filePath = ImageSaveUtil.loadCameraBitmapPath(FaceDetectActivity.this
                                        , "head_tmp.jpg");
                                faceLogin(filePath);
                            }
                        }
                    }
                }
            }
        });

        cameraImageSource.getCameraControl().setPermissionCallback(new PermissionCallback() {
            @Override
            public boolean onRequestPermission() {
                return true;
            }
        });

        rectView.getViewTreeObserver().

                addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        start();
                        rectView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
        ICameraControl control = cameraImageSource.getCameraControl();
        control.setPreviewView(previewView);
        // 设置检测裁剪处理器
        faceDetectManager.addPreProcessor(cropProcessor);

        int orientation = getResources().getConfiguration().orientation;
        boolean isPortrait = (orientation == Configuration.ORIENTATION_PORTRAIT);

        if (isPortrait) {
            previewView.setScaleType(PreviewView.ScaleType.FIT_WIDTH);
        } else {
            previewView.setScaleType(PreviewView.ScaleType.FIT_HEIGHT);
        }

        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        cameraImageSource.getCameraControl().

                setDisplayOrientation(rotation);
        //   previewView.getTextureView().setScaleX(-1);
        nameTextView = (TextView) findViewById(R.id.name_text_view);

        closeIv = (ImageView) findViewById(R.id.closeIv);
        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSuccessView = (ImageView) findViewById(R.id.success_image);

        mSuccessView.getViewTreeObserver().

                addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (mSuccessView.getTag() == null) {
                            Rect rect = rectView.getFaceRoundRect();
                            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) mSuccessView.getLayoutParams();
                            int w = (int) getResources().getDimension(R.dimen.success_width);
                            rlp.setMargins(
                                    rect.centerX() - (w / 2),
                                    rect.top - (w / 2),
                                    0,
                                    0);
                            mSuccessView.setLayoutParams(rlp);
                            mSuccessView.setTag("setlayout");
                        }
                        mSuccessView.setVisibility(View.GONE);
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            mSuccessView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            mSuccessView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
                });

        init();
        tvCancle = findViewById(R.id.tv_cancel);
        tvCancle.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvCancle.setOnClickListener(v -> finish());
    }

    private void initWaveview(Rect rect) {
        RelativeLayout rootView = (RelativeLayout) findViewById(R.id.root_view);

        RelativeLayout.LayoutParams waveParams = new RelativeLayout.LayoutParams(
                rect.width(), rect.height());

        waveParams.setMargins(rect.left, rect.top, rect.left, rect.top);
        waveParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        waveParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

        mWaveview = new WaveView(this);
        rootView.addView(mWaveview, waveParams);

        // mWaveview = (WaveView) findViewById(R.id.wave);
        mWaveHelper = new WaveHelper(mWaveview);

        mWaveview.setShapeType(WaveView.ShapeType.CIRCLE);
        mWaveview.setWaveColor(
                Color.parseColor("#28FFFFFF"),
                Color.parseColor("#3cFFFFFF"));

//        mWaveview.setWaveColor(
//                Color.parseColor("#28f16d7a"),
//                Color.parseColor("#3cf16d7a"));

        mBorderColor = Color.parseColor("#28f16d7a");
        mWaveview.setBorder(mBorderWidth, mBorderColor);
    }

    private void visibleView() {
        mInitView.setVisibility(View.INVISIBLE);
    }

    private boolean saveFaceBmp(FaceFilter.TrackedModel model) {

        final Bitmap face = model.cropFace();
        if (face != null) {
            ImageSaveUtil.saveCameraBitmap(FaceDetectActivity.this, face, "head_tmp.jpg");
        }
        String filePath = ImageSaveUtil.loadCameraBitmapPath(this, "head_tmp.jpg");
        final File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }
        boolean saved = false;
        try {
            byte[] buf = readFile(file);
            if (buf.length > 0) {
                saved = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!saved) {
            Log.d("fileSize", "file size >=-99");
        } else {
            mSavedBmp = true;
        }
        return saved;
    }

    private void initBrightness() {
        int brightness = BrightnessTools.getScreenBrightness(FaceDetectActivity.this);
        if (brightness < 200) {
            BrightnessTools.setBrightness(this, 200);
        }
    }

    private void init() {
        FaceSDKManager.getInstance().getFaceTracker(this).set_min_face_size(200);
        FaceSDKManager.getInstance().getFaceTracker(this).set_isCheckQuality(true);
        // 该角度为商学，左右，偏头的角度的阀值，大于将无法检测出人脸，为了在1：n的时候分数高，注册尽量使用比较正的人脸，可自行条件角度
        FaceSDKManager.getInstance().getFaceTracker(this).set_eulur_angle_thr(15, 15, 15);
        FaceSDKManager.getInstance().getFaceTracker(this).set_isVerifyLive(true);
        FaceSDKManager.getInstance().getFaceTracker(this).set_notFace_thr(0.2f);
        FaceSDKManager.getInstance().getFaceTracker(this).set_occlu_thr(0.1f);
        initBrightness();
    }

    private void start() {
        Rect dRect = rectView.getFaceRoundRect();
        int preGap = getResources().getDimensionPixelOffset(R.dimen.preview_margin);
        int w = getResources().getDimensionPixelOffset(R.dimen.detect_out);

        int orientation = getResources().getConfiguration().orientation;
        boolean isPortrait = (orientation == Configuration.ORIENTATION_PORTRAIT);
        if (isPortrait) {
            // 检测区域矩形宽度
            int rWidth = mScreenW - 2 * preGap;
            // 圆框宽度
            int dRectW = dRect.width();
            // 检测矩形和圆框偏移
            int h = (rWidth - dRectW) / 2;
            int rLeft = w;
            int rRight = rWidth - w;
            int rTop = dRect.top - h - preGap + w;
            int rBottom = rTop + rWidth - w;
            RectF newDetectedRect = new RectF(rLeft, rTop, rRight, rBottom);
            cropProcessor.setDetectedRect(newDetectedRect);
        } else {
            int rLeft = mScreenW / 2 - mScreenH / 2 + w;
            int rRight = mScreenW / 2 + mScreenH / 2 + w;
            int rTop = 0;
            int rBottom = mScreenH;
            RectF newDetectedRect = new RectF(rLeft, rTop, rRight, rBottom);
            cropProcessor.setDetectedRect(newDetectedRect);
        }
        faceDetectManager.start();
        initWaveview(dRect);
    }

    @Override
    protected void onStop() {
        super.onStop();
        faceDetectManager.stop();
        mDetectStoped = true;
        onRefreshSuccessView(false, false);
        if (mWaveview != null) {
            mWaveview.setVisibility(View.GONE);
            mWaveHelper.cancel();
        }
    }

    private void showProgressBar(final boolean show) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (show) {
                    if (mWaveview != null) {
                        mWaveview.setVisibility(View.VISIBLE);
                        mWaveHelper.start();
                    }
                } else {
                    if (mWaveview != null) {
                        mWaveview.setVisibility(View.GONE);
                        mWaveHelper.cancel();
                    }
                }

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWaveview != null) {
            mWaveHelper.cancel();
            mWaveview.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDetectStoped) {
            faceDetectManager.start();
            mDetectStoped = false;
        }

    }

    private void onRefreshSuccessView(final boolean isShow, boolean faceSuccess) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isShow) {
                    //如果人脸注册成功
                    if (faceSuccess) {
                        mSuccessView.setImageResource(R.mipmap.icon_success);
                    } else {
                        mSuccessView.setImageResource(R.mipmap.icon_fail);
                    }
                    mSuccessView.setVisibility(View.VISIBLE);
                } else {
                    mSuccessView.setVisibility(View.INVISIBLE);
                }

            }
        });
    }

    private static class InnerHandler extends Handler {
        private WeakReference<FaceDetectActivity> mWeakReference;

        public InnerHandler(FaceDetectActivity activity) {
            super();
            this.mWeakReference = new WeakReference<FaceDetectActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mWeakReference == null || mWeakReference.get() == null) {
                return;
            }
            FaceDetectActivity activity = mWeakReference.get();
            if (activity == null) {
                return;
            }
            if (msg == null) {
                return;

            }
            switch (msg.what) {
                case MSG_INITVIEW:
                    activity.visibleView();
                    break;
                case MSG_BEGIN_DETECT:
                    activity.mBeginDetect = true;
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 上传图片进行比对，分数达到80认为是同一个人，认为登录可以通过
     * 建议上传自己的服务器，在服务器端调用https://aip.baidubce.com/rest/2.0/face/v3/search，比对分数阀值（如：80分），认为登录通过
     * 返回登录认证的参数给客户端
     *
     * @param filePath
     */
    private void faceLogin(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            Toast.makeText(this, "人脸图片不存在", Toast.LENGTH_SHORT).show();
            mUploading = false;
            mBeginDetect = true;
            mSavedBmp = false;
            return;
        }


        // uid应使用你系统的用户id，示例里暂时用用户名
        String uid = PreferenceUtil.getString(Constant.USERID);

        final File file = new File(filePath);
        if (!file.exists()) {
            Toast.makeText(this, "人脸图片不存在", Toast.LENGTH_SHORT).show();
            mUploading = false;
            mBeginDetect = true;
            mSavedBmp = false;
            return;
        }

        APIService.getInstance().verify(new OnResultListener<RegResult>() {
            @Override
            public void onResult(RegResult result) {
                if (result == null) {
                    mUploading = false;
                    mBeginDetect = true;
                    mSavedBmp = false;
                    ToastUtils.showShort(FaceDetectActivity.this, "服务器错误");
                    return;
                }
                displayData(result);
            }

            @Override
            public void onError(FaceError error) {
                nameTextView.setVisibility(View.VISIBLE);
                nameTextView.setTextColor(getResources().getColor(R.color.color_ff5));
                nameTextView.setText("识别失败" + "errorCode:" + error.getErrorCode() + ",errorMessage:" + error.getErrorMessage());
                if (error.getErrorCode() == 110) {
                    initBdFace();
                }
                onRefreshSuccessView(true, false);
                hideResultdelay();
            }
        }, file, uid);
    }

    @SuppressLint("CheckResult")
    private void displayData(RegResult result) {
        String res = result.getJsonRes();
        double maxScore = 0;
        if (TextUtils.isEmpty(res)) {
            mUploading = false;
            mBeginDetect = true;
            mSavedBmp = false;
            return;
        }
        JSONObject obj = null;
        try {
            obj = new JSONObject(res);
            JSONObject resObj = obj.optJSONObject("result");
            if (resObj != null) {
                JSONArray resArray = resObj.optJSONArray("user_list");
                int size = resArray.length();
                for (int i = 0; i < size; i++) {
                    JSONObject s = (JSONObject) resArray.get(i);
                    if (s != null) {
                        double score = s.getDouble("score");
                        if (score > maxScore) {
                            maxScore = score;
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (maxScore < 80) {
            nameTextView.setText("识别失败");
            onRefreshSuccessView(true, false);
            nameTextView.setTextColor(getResources().getColor(R.color.color_ff5));
            nameTextView.setVisibility(View.VISIBLE);
            hideResultdelay();
        } else {

            nameTextView.setVisibility(View.VISIBLE);

            nameTextView.setTextColor(getResources().getColor(R.color.whitecolor));

            nameTextView.setText("识别成功");

            onRefreshSuccessView(true, true);

            if (getIntent().getStringExtra(EXTRA_KEY_FROM).equals(FLY)) {

                FlutterBdfacePlugin.resultData(null);

                Observable.timer(1000, TimeUnit.MILLISECONDS)
                        .subscribe(aLong -> {

                            finish();

                        });
            }
        }
    }

    public static void toFaceDetect(Activity activity, String reg) {
        Intent intent = new Intent(activity, FaceDetectActivity.class);
        intent.putExtra(EXTRA_KEY_FROM, reg);
        activity.startActivity(intent);
    }

    public static void toFaceDetectForResult(Activity activity, String reg, int requestCode) {
        Intent intent = new Intent(activity, FaceDetectActivity.class);
        intent.putExtra(EXTRA_KEY_FROM, reg);
        activity.startActivityForResult(intent, requestCode);
    }

    @SuppressLint("CheckResult")
    public void hideResultdelay() {
        Observable.timer(2000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(l -> {
                    onRefreshSuccessView(false, false);
                    nameTextView.setVisibility(View.GONE);
                    mUploading = false;
                    mBeginDetect = true;
                    mSavedBmp = false;
                });
    }


    //人脸注册
    public void reg(String filePath) {
        final File file = new File(filePath);
        if (!file.exists()) {
            Toast.makeText(this, "文件不存在", Toast.LENGTH_LONG).show();
            mUploading = false;
            mBeginDetect = true;
            mSavedBmp = false;
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                faceReg(file);
            }
        }, 1000);


    }

    //网络请求人脸注册
    private void faceReg(File file) {
        String username = PreferenceUtil.getString(Constant.USERID);
        String uid = username;

        APIService.getInstance().reg(new OnResultListener<RegResult>() {
            @Override
            public void onResult(RegResult result) {
                //百度后台已经回调成功 添加了这个照片
                Gson gson = new Gson();
                BdRegResultModel bdRegResultModel = gson.fromJson(result.getJsonRes(), BdRegResultModel.class);
                if (bdRegResultModel.getError_code() == 0) {  //如果错误码为0 上传照片给后台
                    Log.e("bdface", "uploadImage----");
                    String faceToken = bdRegResultModel.getResult().getFace_token();
                    Log.e("bdface", "faceToken----" + faceToken);
                    uploadImage(file, faceToken);
                } else {
                    Log.e("bdface", "getError_code!=0----");
                    onRefreshSuccessView(true, false);
                    hideResultdelay();
                }
            }

            @Override
            public void onError(FaceError error) {
                Log.e("bdface", "error----" + error.getErrorMessage());
                if (error.getErrorCode() == 110) {
                    initBdFace();
                }
                onRefreshSuccessView(true, false);
                hideResultdelay();
            }
        }, file, uid, username);
    }

    /**
     * 如果没有人脸识别没有初始化成功 重新初始化
     */
    public void initBdFace() {
        if (!BdFaceUtil.getInstance().isBdFace()) {
            BdFaceUtil.getInstance().init();
        }
    }

    Disposable disposable;

    //    /**
//     * 补全照片信息接口
//     */
    public void addFaceInfo(String faceToken, String url) {
        AddFaceInfoModel addFaceInfoModel = new
                AddFaceInfoModel(faceToken, url);
        Gson gson = new Gson();
        String jStr = gson.toJson(addFaceInfoModel, AddFaceInfoModel.class);
        RequestBody requestBody =
                RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jStr);
        Net.getAirlookService().addFace(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetObserver<Object>() {
                    @Override
                    public void doOnSuccess(Object o) {
                        Log.e("bdface", "不全照片" + o.toString());
                    }

                    @Override
                    public void doOnNullData() {
                        super.doOnNullData();
                        onRefreshSuccessView(true, true);
                        PreferenceUtil.putInt(Constant.FACEREG, Constant.FACEALREADREG);
                        //如果是首页注册 返回成功结果值
                        if (getIntent().getStringExtra(EXTRA_KEY_FROM).equals(REGIST)) {
                            finish();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
//                        super.onError(e);
                        Log.e("bdface", "不全照片错误" + e.getMessage());
                        onRefreshSuccessView(true, false);
                        hideResultdelay();
                    }
                });
    }

    /**
     * 向服务器上传图片
     */
    public void uploadImage(File file, String faceToken) {
        // 创建 RequestBody，用于封装构建RequestBody
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        Call call = Net.getUpLoadImageService().uploadImaeg(body);

        call.enqueue(new Callback<BaseModel<UpLoadingModel>>() {
            @Override
            public void onResponse(Call<BaseModel<UpLoadingModel>> call, Response<BaseModel<UpLoadingModel>> response) {
                if (response.body().getCode() == 200) {  //如果人脸照片上传成功
                    //如果是注册并返回结果  应该是在认证页面调用 此时应该直接返回faceToken和url
                    if (getIntent().getStringExtra(EXTRA_KEY_FROM).equals(REGISTFORRESULT)) {
                        onRefreshSuccessView(true, true);
//                        Intent intent = getIntent();
//                        intent.putExtra(VerifiedActivity.RESULT_KEY_FACETOKE, faceToken);
//                        intent.putExtra(VerifiedActivity.RESULT_KEY_FACEURL
//                                , response.body().getData().getDomain() + "/" + response.body().getData().getImageUrl());
//                        setResult(RESULT_OK, intent);
                        Log.e("bdface", "图片上传成功");
                        Map<String, Object> map = new HashMap<>();
                        map.put("FACETOKEN", faceToken);
                        map.put("FACEURL", response.body().getData().getDomain() + "/" + response.body().getData().getImageUrl());
                        FlutterBdfacePlugin.resultData(map);
                        finish();
                    } else {  //如果只是注册 或者是主页的人脸注册 则应该调用补全照片信息的接口
                        addFaceInfo(faceToken, response.body().getData().getDomain() + "/" + response.body().getData().getImageUrl());
                    }
                } else {
                    onRefreshSuccessView(true, false);
                    hideResultdelay();
                }
            }

            @Override
            public void onFailure(Call<BaseModel<UpLoadingModel>> call, Throwable t) {
                onRefreshSuccessView(true, false);
                hideResultdelay();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        disposable = null;
    }
}
