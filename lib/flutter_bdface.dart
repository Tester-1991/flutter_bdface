import 'dart:async';

import 'package:flutter/services.dart';

class FlutterBdface {
  static const MethodChannel _channel = const MethodChannel('flutter_bdface');

  static Future<void> get platformVersion async {
    await _channel.invokeMethod('bdface');
  }
}
