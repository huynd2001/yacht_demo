import 'dart:convert';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:infinite_listview/infinite_listview.dart';
import 'package:intl/intl.dart';
import 'package:yacht_demo/day-display.dart';
import 'package:yacht_demo/services/event-retriever.dart';

class DateItem {
  final DateTime startTime;
  final DateTime endTime;

  DateItem(this.startTime, this.endTime);
}

class DateWidget extends StatefulWidget {
  DateWidget({Key? key}) : super(key: key);

  @override
  _CalendarDisplay createState() => _CalendarDisplay();
}

class CalendarItemDisplay extends StatefulWidget {
  final DateTime startTime;
  final DateTime endTime;

  CalendarItemDisplay(
      {Key? key, required this.startTime, required this.endTime})
      : super(key: key);

  @override
  _CalendarItemDisplayState createState() => _CalendarItemDisplayState();
}

class _CalendarItemDisplayState extends State<CalendarItemDisplay> {
  List<EventItem> events = List.empty(growable: true);

  @override
  Widget build(BuildContext context) {
    EventRetriever.retrieveEventFromStartEnd(
            this.widget.startTime, this.widget.endTime)
        .then((value) => {events.addAll(value)});

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
                      title:
                          Text(DateFormat('M-d').format(this.widget.startTime)),
                      subtitle: Text(
                        DateFormat('EEE').format(this.widget.startTime),
                      ))
                ] +
                events.map((e) => Text(e.description)).take(4).toList(),
          ),
        ),
      ),
    );
  }
}

class _CalendarDisplay extends State<DateWidget> {
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
            return CalendarItemDisplay(startTime: startTime, endTime: endTime);
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
                                          formEndTime, '', formTaskName));
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
