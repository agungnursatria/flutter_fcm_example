import 'package:flutter/material.dart';

class PopUpArguments {
  String code;
  PopUpArguments({this.code});
}

class PopUpPage extends StatefulWidget {
  static const String PATH = '/popup';
  @override
  _PopUpPageState createState() => _PopUpPageState();
}

class _PopUpPageState extends State<PopUpPage> {
  String code = 'Unknown';
  @override
  void initState() {
    super.initState();

    WidgetsBinding.instance.addPostFrameCallback((_) {
      final PopUpArguments args = ModalRoute.of(context).settings.arguments;

      setState(() {
        code = args.code;
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        child: Center(
          child: Text(code),
        ),
      ),
    );
  }
}
