import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_fcm/popup.dart';

class SecondPage extends StatefulWidget {
  static const String PATH = '/s';
  @override
  _SecondPageState createState() => _SecondPageState();
}

class _SecondPageState extends State<SecondPage> {
  var platform =
      const MethodChannel('com.example.flutter_fcm/FCMCodeMethodChannel');

  @override
  void initState() {
    super.initState();
    platform.setMethodCallHandler((call) {
      if (call.method == 'sendCode') {
        Navigator.of(context).pushNamed(PopUpPage.PATH,
            arguments: PopUpArguments(code: call.arguments));
      }
      return null;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        child: Center(
          child: Text('Second Page'),
        ),
      ),
    );
  }
}
