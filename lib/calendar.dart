import 'dart:convert';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:infinite_listview/infinite_listview.dart';
import 'package:intl/intl.dart';
import 'package:yacht_demo/day-display.dart';
import 'package:yacht_demo/services/event-retriever.dart';

class EventItem {
  final DateTime startTime;
  final DateTime endTime;
  final String task;
  final int id;
  static int eventIdTracking = 0;

  const EventItem._(this.startTime, this.endTime, this.task, this.id);

  static EventItem of(DateTime startTime, DateTime endTime, String task) {
    eventIdTracking++;
    return new EventItem._(startTime, endTime, task, eventIdTracking);
  }

  EventItem.fromJson(Map<String, String> json)
      : startTime = DateTime.parse(json['startTime'].toString()),
        endTime = DateTime.parse(json['endTime'].toString()),
        task = json['task'].toString(),
        id = int.parse(json['id'] ?? "-1");

  Map<String, String> toJson() => {
        'startTime': startTime.toIso8601String(),
        'endTime': endTime.toIso8601String(),
        'task': task,
        'id': id.toString()
      };
}

class DateItem {
  final DateTime startTime;
  final DateTime endTime;

  DateItem(this.startTime, this.endTime);
}

class DateWidget extends StatefulWidget {
  const DateWidget({Key? key}) : super(key: key);

  @override
  CalendarDisplay createState() => CalendarDisplay();
}

class CalendarDisplay extends State<DateWidget> {
  String formTaskName = "";
  DateTime formStartTime = DateTime.now();
  DateTime formEndTime = DateTime.now();

  final _formKey = GlobalKey<FormState>();

  void addEvent(EventItem e) {
    setState(() {
      EventRetriever.addEvent(e);
    });
  }

  @override
  Widget build(BuildContext context) {
    return Column(children: <Widget>[
      Container(
        height: 200,
        child: InfiniteListView.builder(
          itemBuilder: (_, index) {
            DateTime startTime =
                EventRetriever.today().add(Duration(days: index));
            DateTime endTime = startTime.add(Duration(days: 1));
            return GestureDetector(
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                      builder: (_) => DayDisplay(
                            startDate: EventRetriever.today(),
                          )),
                );
              },
              child: Card(
                child: Container(
                  width: 80,
                  height: 100,
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    children: <Widget>[
                          ListTile(
                              title: Text(DateFormat('M-d').format(startTime)),
                              subtitle: Text(
                                DateFormat('EEE').format(startTime),
                              ))
                        ] +
                        EventRetriever.retrieveEventFromStartEnd(
                                startTime, endTime)
                            .map((e) => Text(e.task))
                            .take(4)
                            .toList(),
                  ),
                ),
              ),
            );
          },
          scrollDirection: Axis.horizontal,
          addAutomaticKeepAlives: false,
        ),
      ),
      ElevatedButton(
          onPressed: () {
            showDialog(
                context: context,
                builder: (BuildContext context) {
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
                                      border: UnderlineInputBorder(),
                                      labelText: 'Task'),
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
                                          lastDate: DateTime.now()
                                              .add(Duration(days: 7)),
                                          firstDate: DateTime(
                                              DateTime.now().year,
                                              DateTime.now().month,
                                              DateTime.now().day,
                                              0,
                                              0,
                                              0),
                                        ).then((value) => {
                                              setState(() {
                                                formStartTime =
                                                    value ?? DateTime.now();
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
                                                initialTime: TimeOfDay(
                                                    hour: 0, minute: 0))
                                            .then((value) => {
                                                  setState(() {
                                                    TimeOfDay time = value ??
                                                        TimeOfDay.now();
                                                    formStartTime =
                                                        new DateTime(
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
                                          lastDate: DateTime.now()
                                              .add(Duration(days: 7)),
                                          firstDate: DateTime(
                                              DateTime.now().year,
                                              DateTime.now().month,
                                              DateTime.now().day,
                                              0,
                                              0,
                                              0),
                                        ).then((value) => {
                                              setState(() {
                                                formEndTime =
                                                    value ?? DateTime.now();
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
                                                initialTime: TimeOfDay(
                                                    hour: 0, minute: 0))
                                            .then((value) => {
                                                  setState(() {
                                                    TimeOfDay time = value ??
                                                        TimeOfDay.now();
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
                                      addEvent(EventItem.of(formStartTime,
                                          formEndTime, formTaskName));
                                      ScaffoldMessenger.of(context)
                                          .showSnackBar(
                                        const SnackBar(
                                            content: Text('Event Added!')),
                                      );
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
                });
          },
          child: Text('ADD EVENT'))
    ]);
  }
}

class MyCustomForm extends StatefulWidget {
  const MyCustomForm({Key? key}) : super(key: key);

  @override
  MyCustomFormState createState() => MyCustomFormState();
}

class MyCustomFormState extends State<MyCustomForm> {
  final _formKey = GlobalKey<FormState>();

  @override
  Widget build(BuildContext context) {
    // Build a Form widget using the _formKey created above.
    return Form(
      key: _formKey,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          TextFormField(
            // The validator receives the text that the user has entered.
            validator: (value) {
              if (value == null || value.isEmpty) {
                return 'Please enter some text';
              }
              return null;
            },
          ),
          Padding(
            padding: const EdgeInsets.symmetric(vertical: 16.0),
            child: ElevatedButton(
              onPressed: () {
                // Validate returns true if the form is valid, or false otherwise.
                if (_formKey.currentState!.validate()) {
                  // If the form is valid, display a snackbar. In the real world,
                  // you'd often call a server or save the information in a database.
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(content: Text('Processing Data')),
                  );
                }
              },
              child: const Text('Submit'),
            ),
          ),
        ],
      ),
    );
  }
}
