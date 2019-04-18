package com.baidu;

import android.content.Intent;

import com.baidu.bdface.FaceDetectActivity;

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
            FlutterActivity root = (FlutterActivity) flutterRegistrar.activity();
            ToastUtils.show(root.getApplicationContext(), "执行", 0);
            //初始化
            PreferenceUtil.context = root.getApplicationContext();

            BdFaceUtil.getInstance().setContext(PreferenceUtil.context);

            BdFaceUtil.getInstance().init();

            Intent intent = new Intent(root,FaceDetectActivity.class);

            intent.putExtra(FaceDetectActivity.EXTRA_KEY_FROM, FaceDetectActivity.REGIST);

            root.startActivity(intent);
        }
    }
}
