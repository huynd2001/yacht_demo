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
  static final DateTime MIN_DATE = DateTime(1000, 1, 1);
  static final DateTime MAX_DATE = DateTime(3000, 12, 31);

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
                                          initialDate: DateTime.now(),
                                          firstDate: MIN_DATE,
                                          lastDate: MAX_DATE,
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
                                                initialTime: TimeOfDay.now())
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
                                          initialDate: DateTime.now(),
                                          firstDate: MAX_DATE,
                                          lastDate: MIN_DATE,
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
                                                initialTime: TimeOfDay.now())
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
