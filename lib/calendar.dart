import 'dart:convert';

import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:infinite_listview/infinite_listview.dart';
import 'package:intl/intl.dart';
import 'package:yacht_demo/components/datetime-picker.dart';
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

  gettingEvents() {
    EventRetriever.retrieveEventFromStartEnd(
            this.widget.startTime, this.widget.endTime)
        .then((value) => {
              if (!listEquals(events, value))
                {
                  setState(() {
                    events = value;
                  })
                }
            });
  }

  @override
  Widget build(BuildContext context) {
    gettingEvents();

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

  void addEvent(
      DateTime startTime, DateTime endTime, String label, String description) {
    setState(() {
      EventRetriever.createEvent(startTime, endTime, label, description);
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
                  return EventEditor(
                      callback: (startTime, endTime, label, description) {
                    print("kek");
                    if (startTime != null &&
                        endTime != null &&
                        startTime.isBefore(endTime)) {
                      print('lmao');
                      addEvent(startTime, endTime, label, description);
                      ScaffoldMessenger.of(context).showSnackBar(
                        const SnackBar(content: Text('Event Added!')),
                      );
                      Navigator.pop(context);
                    }
                  });
                });
          },
          child: Text('ADD EVENT'))
    ]);
  }
}
