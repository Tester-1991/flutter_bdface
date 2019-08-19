package com.baidu;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.baidu.bdface.FaceDetectActivity;

import java.util.Map;

import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * FlutterBdfacePlugin
 */
public class FlutterBdfacePlugin implements MethodCallHandler {

    private static Registrar flutterRegistrar;

    public static Result flutterResult;

    public static FlutterBdfacePlugin instance = new FlutterBdfacePlugin();

    public static void resultData(Map data) {
        flutterResult.success(data);
    }

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        Log.e("bdface", "插件注册registerWith");
        flutterRegistrar = registrar;
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_bdface");
        channel.setMethodCallHandler(instance);

    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (flutterRegistrar.activity() == null) {
            result.error("no_activity", "image_cropper plugin requires a foreground activity.", null);
            return;
        }
        if (call.method.equals("bdface")) {

            Log.e("bdface", "call method bdface");

            flutterResult = result;

            FlutterActivity root = (FlutterActivity) flutterRegistrar.activity();
            //初始化
//            PreferenceUtil.context = root.getApplicationContext();

            Map<String, Object> args = (Map<String, Object>) call.arguments;

            //设置用户id
            String userId = (String) args.get(Constant.USERID);

            Log.e("bdface", "userId-----" + userId);

            PreferenceUtil.putString(Constant.USERID, userId);

            //设置token
            String token = (String) args.get(Constant.TOKEN);

            Log.e("bdface", "token-----" + token);

            //获取intent
            String intentMessage = (String) args.get(Constant.INTENTKEY);

            Log.e("bdface", "INTENTKEY-----" + intentMessage);

            PreferenceUtil.putString(Constant.TOKEN, token);

//            BdFaceUtil.getInstance().setContext(PreferenceUtil.context);

            Intent intent = new Intent(root, FaceDetectActivity.class);

            if (intentMessage.equals(FaceDetectActivity.REGISTFORRESULT)) {

                intent.putExtra(FaceDetectActivity.EXTRA_KEY_FROM, FaceDetectActivity.REGISTFORRESULT);

            } else if (intentMessage.equals(FaceDetectActivity.FLY)) {

                Log.e("bdface", "跳转人脸识别界面,FLY");

                intent.putExtra(FaceDetectActivity.EXTRA_KEY_FROM, FaceDetectActivity.FLY);
            }

            root.startActivity(intent);
        }
    }

    public void init(Context context) {
        PreferenceUtil.context = context;
        BdFaceUtil.getInstance().init(context);
    }
}
