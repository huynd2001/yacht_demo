import 'package:flutter/widgets.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:yacht_demo/services/event-retriever.dart';

import 'calendar.dart';

class EventDisplay extends StatelessWidget {
  final EventItem event;

  const EventDisplay({Key? key, required this.event}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return new Stack(
      children: <Widget>[
        new Padding(
          padding: const EdgeInsets.only(left: 50.0),
          child: new Card(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: <Widget>[
                ListTile(
                  leading: Icon(Icons.lightbulb),
                  title: Text(event.task),
                  subtitle: Text(
                      DateFormat('h:mm a', 'en_US').format(event.startTime) +
                          ' - ' +
                          DateFormat('h:mm a', 'en_US').format(event.endTime)),
                )
              ],
            ),
          ),
        ),
      ],
    );
  }
}

class EventInDaysDisplay extends StatelessWidget {
  final DateTime begin;
  final DateTime end;

  const EventInDaysDisplay({Key? key, required this.begin, required this.end})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    List<EventItem> events =
        EventRetriever.retrieveEventFromStartEnd(begin, end);

    return Column(
      children: [
        Text(DateFormat('EEE, MMM d').format(begin)),
        ListView.builder(
          itemBuilder: (_, index) {
            return EventDisplay(event: events[index]);
          },
          itemCount: events.length,
        )
      ],
    );
  }
}

class DayDisplay extends StatelessWidget {
  DateTime getToday() => new DateTime(
      DateTime.now().year, DateTime.now().month, DateTime.now().day, 0, 0, 0);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
            backgroundColor: Colors.blue, title: const Text('Date View')),
        body: ListView.builder(
            itemBuilder: (_, index) {
              DateTime startTime = getToday().add(Duration(days: index));
              DateTime endTime = startTime.add(Duration(days: 1));
              return EventInDaysDisplay(begin: startTime, end: endTime);
            },
            addAutomaticKeepAlives: false));
  }
}
