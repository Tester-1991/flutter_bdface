import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_bdface/flutter_bdface.dart';
import 'package:flutter_bdface_example/permission/permission_manager.dart';
import 'package:simple_permissions/simple_permissions.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      await FlutterBdface.platformVersion;
    } on PlatformException {}

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: InkWell(
            onTap: () async{
              List permissions = [Permission.WriteExternalStorage];
              var result = await PermissionManager.requestPermission(permissions);
              initPlatformState();
            },
            child: Text('Running on: $_platformVersion\n'),
          ),
        ),
      ),
    );
  }
}
