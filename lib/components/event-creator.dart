import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

class EventCreator extends StatefulWidget {
  final Function(
          DateTime? start, DateTime? end, String label, String description)
      callback;

  EventCreator({Key? key, required this.callback}) : super(key: key);

  @override
  _EventEditorState createState() => _EventEditorState();
}

class _EventEditorState extends State<EventCreator> {
  static final DateTime MIN_DATE = DateTime(1000, 1, 1);
  static final DateTime MAX_DATE = DateTime(3000, 12, 31);

  String formLabel = "";
  String formDescription = "";
  DateTime? formStartTime;
  DateTime? formEndTime;
  final _formKey = GlobalKey<FormState>();

  @override
  Widget build(BuildContext context) {
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
                        border: UnderlineInputBorder(), labelText: 'Label'),
                    onSaved: (val) {
                      this.formLabel = val!;
                    },
                  ),
                ),
                Padding(
                  padding: EdgeInsets.all(8.0),
                  child: TextFormField(
                    validator: (value) {
                      if (value == null) {
                        return 'Please enter some text';
                      }
                      return null;
                    },
                    decoration: const InputDecoration(
                        border: UnderlineInputBorder(),
                        labelText: 'Description'),
                    onSaved: (val) {
                      this.formDescription = val!;
                    },
                  ),
                ),
                Padding(
                  padding: EdgeInsets.all(8.0),
                  child: Row(children: <Widget>[
                    Container(
                      padding: const EdgeInsets.all(8.0),
                      child: GestureDetector(
                        onTap: () {
                          showDatePicker(
                            context: context,
                            initialDate: DateTime.now(),
                            firstDate: MIN_DATE,
                            lastDate: MAX_DATE,
                          ).then((value) => {
                                setState(() {
                                  formStartTime = value ?? DateTime.now();
                                }),
                                print(formStartTime),
                              });
                        },
                        // ignore: unnecessary_null_comparison
                        child: Text((formStartTime == null)
                            ? 'Start Date'
                            : DateFormat("MM-dd").format(formStartTime!)),
                      ),
                    ),
                    Container(
                      padding: const EdgeInsets.all(8.0),
                      child: GestureDetector(
                        onTap: () {
                          showTimePicker(
                                  context: context,
                                  initialTime: TimeOfDay.now())
                              .then((value) => {
                                    setState(() {
                                      TimeOfDay time = value ?? TimeOfDay.now();
                                      formStartTime = new DateTime(
                                          formStartTime!.year,
                                          formStartTime!.month,
                                          formStartTime!.day,
                                          time.hour,
                                          time.minute);
                                    }),
                                    print(formStartTime),
                                  });
                        },
                        child: Text((formStartTime == null)
                            ? 'Start Time'
                            : DateFormat("h:mm a").format(formStartTime!)),
                      ),
                    ),
                  ]),
                ),
                Padding(
                  padding: EdgeInsets.all(8.0),
                  child: Row(children: <Widget>[
                    Container(
                      padding: const EdgeInsets.all(8.0),
                      child: GestureDetector(
                        onTap: () {
                          showDatePicker(
                            context: context,
                            initialDate: DateTime.now(),
                            firstDate: MIN_DATE,
                            lastDate: MAX_DATE,
                          ).then((value) => {
                                setState(() {
                                  formEndTime = value ?? DateTime.now();
                                }),
                                print(formEndTime),
                              });
                        },
                        child: Text((formEndTime == null)
                            ? 'End Date'
                            : DateFormat("MM-dd").format(formEndTime!)),
                      ),
                    ),
                    Container(
                      padding: const EdgeInsets.all(8.0),
                      child: GestureDetector(
                        onTap: () {
                          showTimePicker(
                                  context: context,
                                  initialTime: TimeOfDay.now())
                              .then((value) => {
                                    setState(() {
                                      TimeOfDay time = value ?? TimeOfDay.now();
                                      formEndTime = new DateTime(
                                          formEndTime!.year,
                                          formEndTime!.month,
                                          formEndTime!.day,
                                          time.hour,
                                          time.minute);
                                    }),
                                    print(formEndTime),
                                  });
                        },
                        child: Text((formEndTime == null)
                            ? 'End Time'
                            : DateFormat("h:mm a").format(formEndTime!)),
                      ),
                    ),
                  ]),
                ),
                Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: TextButton(
                    child: Text("Submit"),
                    onPressed: () {
                      if (_formKey.currentState!.validate()) {
                        _formKey.currentState!.save();
                        print(
                            '${this.formStartTime}  ${this.formEndTime}  ${this.formLabel}     ${this.formDescription}');
                        this.widget.callback(
                            this.formStartTime,
                            this.formEndTime,
                            this.formLabel,
                            this.formDescription);
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
