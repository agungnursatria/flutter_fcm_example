import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_fcm/popup.dart';
import 'package:flutter_fcm/second_page.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      initialRoute: MyHomePage.PATH,
      routes: <String, WidgetBuilder>{
        MyHomePage.PATH: (_) => MyHomePage(),
        PopUpPage.PATH: (_) => PopUpPage(),
        SecondPage.PATH: (_) => SecondPage(),
      },
    );
  }
}

class MyHomePage extends StatefulWidget {
  static const String PATH = '/';

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  int _counter = 0;
  var platform =
      const MethodChannel('com.example.flutter_fcm/FCMCodeMethodChannel');

  @override
  void initState() {
    platform.setMethodCallHandler((call) {
      if (call.method == 'sendCode') {
        Navigator.of(context).pushNamed(PopUpPage.PATH,
            arguments: PopUpArguments(code: call.arguments));
      }
      return null;
    });

    WidgetsBinding.instance.addPostFrameCallback((_) async {
      try {
        var code = await platform.invokeMethod('get_code');
        Navigator.of(context)
            .pushNamed(PopUpPage.PATH, arguments: PopUpArguments(code: code));
      } on PlatformException catch (e) {
        print("FCMCodeMethodChannel: ${e.toString()}");
      }
    });

    super.initState();
  }

  void _incrementCounter() {
    Navigator.of(context).pushNamed(SecondPage.PATH);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Home Page'),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(
              'You have pushed the button this many times:',
            ),
            Text(
              '$_counter',
              style: Theme.of(context).textTheme.display1,
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _incrementCounter,
        tooltip: 'Increment',
        child: Icon(Icons.add),
      ), // This trailing comma makes auto-formatting nicer for build methods.
    );
  }
}
