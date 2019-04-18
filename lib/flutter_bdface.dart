import 'dart:async';

import 'package:flutter/services.dart';

class FlutterBdface {
  static const MethodChannel _channel = const MethodChannel('flutter_bdface');

  static Map _data = Map();

  static void setData(Map data) {
    _data.clear();
    _data.addAll(data);
  }

  static Future<Map> get platformVersion async {
    return await _channel.invokeMethod<Map>(
      'bdface',
      _data,
    );
  }
}
