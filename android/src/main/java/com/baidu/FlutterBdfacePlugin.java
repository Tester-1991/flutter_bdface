package com.baidu;

import android.content.Intent;

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

    public static void resultData(Map data){
        flutterResult.success(data);
    }

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        flutterRegistrar = registrar;
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_bdface");
        channel.setMethodCallHandler(new FlutterBdfacePlugin());

    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (flutterRegistrar.activity() == null) {
            result.error("no_activity", "image_cropper plugin requires a foreground activity.", null);
            return;
        }
        if (call.method.equals("bdface")) {

            flutterResult = result;

            FlutterActivity root = (FlutterActivity) flutterRegistrar.activity();
            //初始化
            PreferenceUtil.context = root.getApplicationContext();

            Map<String, Object> args = (Map<String, Object>) call.arguments;
            //设置用户id
            String userId = (String) args.get(Constant.USERID);

            PreferenceUtil.putString(Constant.USERID, userId);

            //设置token
            String token = (String) args.get(Constant.TOKEN);

            PreferenceUtil.putString(Constant.TOKEN, token);

            BdFaceUtil.getInstance().setContext(PreferenceUtil.context);

            BdFaceUtil.getInstance().init();

            Intent intent = new Intent(root, FaceDetectActivity.class);

            intent.putExtra(FaceDetectActivity.EXTRA_KEY_FROM, FaceDetectActivity.REGISTFORRESULT);

            root.startActivity(intent);
        }
    }
}
