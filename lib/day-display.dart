import 'package:flutter/widgets.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:yacht_demo/services/event-retriever.dart';
import 'package:infinite_listview/infinite_listview.dart';

import 'calendar.dart';

class EventDisplay extends StatelessWidget {
  final EventItem event;

  const EventDisplay({Key? key, required this.event}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return new Stack(
      children: <Widget>[
        new Padding(
          padding: const EdgeInsets.only(right: 50.0),
          child: new Card(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: <Widget>[
                ListTile(
                  leading: Icon(Icons.lightbulb),
                  title: Text(event.description),
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

class EventInDaysDisplay extends StatefulWidget {
  final DateTime begin;
  final DateTime end;

  const EventInDaysDisplay({Key? key, required this.begin, required this.end})
      : super(key: key);

  @override
  _EventInDaysDisplayState createState() => _EventInDaysDisplayState();
}

class _EventInDaysDisplayState extends State<EventInDaysDisplay> {
  List<EventItem> events = List.empty(growable: true);

  @override
  Widget build(BuildContext context) {
    EventRetriever.retrieveEventFromStartEnd(widget.begin, widget.end)
        .then((value) => setState(() => events.addAll(value)));

    return Column(
      children: [
        Text(DateFormat('EEE, MMM d').format(widget.begin)),
        Divider(color: Colors.black),
        ConstrainedBox(
            constraints: new BoxConstraints(
              minHeight: 50,
            ),
            child: ListView.builder(
              itemBuilder: (_, index) {
                return EventDisplay(event: events[index]);
              },
              itemCount: events.length,
              shrinkWrap: true,
              physics: NeverScrollableScrollPhysics(),
            ))
      ],
    );
  }
}

class DayDisplay extends StatefulWidget {
  final DateTime startDate;

  const DayDisplay({Key? key, required this.startDate}) : super(key: key);

  @override
  _DayDisplayState createState() => _DayDisplayState();
}

class _DayDisplayState extends State<DayDisplay> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
            backgroundColor: Colors.blue, title: const Text('Date View')),
        body: InfiniteListView.builder(
            itemBuilder: (_, index) {
              DateTime startTime =
                  EventRetriever.today().add(Duration(days: index));
              DateTime endTime = startTime.add(Duration(days: 1));
              return EventInDaysDisplay(begin: startTime, end: endTime);
            },
            addAutomaticKeepAlives: false));
  }
}
