import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

class TaskCreator extends StatefulWidget {
  final Function(String taskName) callback;

  TaskCreator({Key? key, required this.callback}) : super(key: key);

  @override
  _TaskCreatorState createState() => _TaskCreatorState();
}

class _TaskCreatorState extends State<TaskCreator> {
  static final DateTime MIN_DATE = DateTime(1000, 1, 1);
  static final DateTime MAX_DATE = DateTime(3000, 12, 31);

  String formTaskName = "";
  final _formKey = GlobalKey<FormState>();

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
        content: Stack(children: <Widget>[
      Form(
        key: _formKey,
        child: Column(
          children: <Widget>[
            Padding(
              padding: EdgeInsets.all(8.0),
              child: TextFormField(
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Please enter some text';
                  }
                  return null;
                },
                decoration: const InputDecoration(
                    border: UnderlineInputBorder(), labelText: 'Label'),
                onSaved: (val) {
                  this.formTaskName = val!;
                },
              ),
            ),
            Padding(
              padding: const EdgeInsets.all(8.0),
              child: TextButton(
                child: Text("Submit"),
                onPressed: () {
                  if (_formKey.currentState!.validate()) {
                    _formKey.currentState!.save();
                    this.widget.callback(this.formTaskName);
                  }
                },
              ),
            )
          ],
        ),
      )
    ]));
  }
}
