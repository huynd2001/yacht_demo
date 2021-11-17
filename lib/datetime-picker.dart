import 'package:flutter/widgets.dart';
import 'package:flutter/material.dart';

class Picker extends StatefulWidget {
  @override
  _DisplayState createState() => _DisplayState();
}

class _DisplayState extends State<Picker> {
  final _formKey = GlobalKey<FormState>();
  String formTaskName = "";
  DateTime formStartTime = DateTime.now();
  DateTime formEndTime = DateTime.now();

  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return AlertDialog(
      content: Stack(
        children: <Widget>[
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
                        border: UnderlineInputBorder(), labelText: 'Task'),
                    onSaved: (val) {
                      this.formTaskName = val!;
                      val = "";
                    },
                  ),
                ),
                Padding(
                  padding: EdgeInsets.all(8.0),
                  child: Row(children: <Widget>[
                    Container(
                      child: ElevatedButton(
                        onPressed: () {
                          showDatePicker(
                            context: context,
                            initialDate: DateTime(
                                DateTime.now().year,
                                DateTime.now().month,
                                DateTime.now().day,
                                0,
                                0,
                                0),
                            lastDate: DateTime.now().add(Duration(days: 7)),
                            firstDate: DateTime(
                                DateTime.now().year,
                                DateTime.now().month,
                                DateTime.now().day,
                                0,
                                0,
                                0),
                          ).then((value) => {
                                setState(() {
                                  formStartTime = value ?? DateTime.now();
                                }),
                                print(formStartTime),
                              });
                        },
                        child: Text('Start Date'),
                      ),
                    ),
                    Container(
                      child: ElevatedButton(
                        onPressed: () {
                          showTimePicker(
                                  context: context,
                                  initialTime: TimeOfDay(hour: 0, minute: 0))
                              .then((value) => {
                                    setState(() {
                                      TimeOfDay time = value ?? TimeOfDay.now();
                                      formStartTime = new DateTime(
                                          formStartTime.year,
                                          formStartTime.month,
                                          formStartTime.day,
                                          time.hour,
                                          time.minute);
                                    }),
                                    print(formStartTime),
                                  });
                        },
                        child: Text('Start Time'),
                      ),
                    ),
                  ]),
                ),
                Padding(
                  padding: EdgeInsets.all(8.0),
                  child: Row(children: <Widget>[
                    Container(
                      child: ElevatedButton(
                        onPressed: () {
                          showDatePicker(
                            context: context,
                            initialDate: DateTime(
                                DateTime.now().year,
                                DateTime.now().month,
                                DateTime.now().day,
                                0,
                                0,
                                0),
                            lastDate: DateTime.now().add(Duration(days: 7)),
                            firstDate: DateTime(
                                DateTime.now().year,
                                DateTime.now().month,
                                DateTime.now().day,
                                0,
                                0,
                                0),
                          ).then((value) => {
                                setState(() {
                                  formEndTime = value ?? DateTime.now();
                                }),
                                print(formEndTime),
                              });
                        },
                        child: Text('End Date'),
                      ),
                    ),
                    Container(
                      child: ElevatedButton(
                        onPressed: () {
                          showTimePicker(
                                  context: context,
                                  initialTime: TimeOfDay(hour: 0, minute: 0))
                              .then((value) => {
                                    setState(() {
                                      TimeOfDay time = value ?? TimeOfDay.now();
                                      formEndTime = new DateTime(
                                          formEndTime.year,
                                          formEndTime.month,
                                          formEndTime.day,
                                          time.hour,
                                          time.minute);
                                    }),
                                    print(formEndTime),
                                  });
                        },
                        child: Text('End Time'),
                      ),
                    ),
                  ]),
                ),
                Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: ElevatedButton(
                    child: Text("Submit"),
                    onPressed: () {
                      if (_formKey.currentState!.validate()) {
                        _formKey.currentState!.save();
                        print(this.formTaskName);
                        print(this.formStartTime);
                        print(this.formEndTime);
                      }
                    },
                  ),
                )
              ],
            ),
          ),
        ],
      ),
    );
  }
}
