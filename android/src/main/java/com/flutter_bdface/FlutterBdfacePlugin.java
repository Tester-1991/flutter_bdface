package com.flutter_bdface;

import android.content.Intent;

import com.flutter_bdface.bdface.FaceDetectActivity;

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
        if (call.method.equals("bdface")) {

            //初始化
            PreferenceUtil.context = flutterRegistrar.activity();

            BdFaceUtil.getInstance().setContext(flutterRegistrar.activity());

            BdFaceUtil.getInstance().init();

            Intent intent = new Intent();

            intent.putExtra(FaceDetectActivity.EXTRA_KEY_FROM,FaceDetectActivity.REGIST);

            flutterRegistrar.activity().startActivity(intent);
        }
    }
}
