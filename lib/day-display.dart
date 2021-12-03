import 'package:flutter/foundation.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:yacht_demo/components/event-editor.dart';
import 'package:yacht_demo/services/event-retriever.dart';
import 'package:infinite_listview/infinite_listview.dart';
import 'package:yacht_demo/task-list.dart';

class EventDisplay extends StatelessWidget {
  final EventItem event;

  final Function callback;

  const EventDisplay({Key? key, required this.event, required this.callback})
      : super(key: key);

  static String _eventRange(DateTime startTime, DateTime endTime) {
    return DateFormat('h:mm a', 'en_US').format(startTime) +
        ' - ' +
        DateFormat('h:mm a', 'en_US').format(endTime) +
        ((startTime.day == endTime.day &&
                startTime.month == endTime.month &&
                startTime.year == endTime.year)
            ? ""
            : ' (+${DateTime(endTime.year, endTime.month, endTime.day).difference(DateTime(startTime.year, startTime.month, startTime.day)).inDays} days)');
  }

  @override
  Widget build(BuildContext context) {
    return new Stack(
      children: <Widget>[
        new Padding(
            padding: const EdgeInsets.only(right: 50.0),
            child: Card(
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: <Widget>[
                  ListTile(
                    leading: Icon(Icons.lightbulb),
                    title: Text(event.label),
                    subtitle: Text(_eventRange(event.startTime, event.endTime)),
                  ),
                  Text(event.description),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.end,
                    children: <Widget>[
                      TextButton(
                        child: const Text('MODIFY'),
                        onPressed: () {
                          this.callback();
                        },
                      ),
                      const SizedBox(width: 8),
                      TextButton(
                        child: const Text('TASKS'),
                        onPressed: () {
                          Navigator.push(
                              context,
                              MaterialPageRoute(
                                builder: (_) => Scaffold(
                                    appBar: AppBar(
                                        backgroundColor: Colors.blue,
                                        title: const Text('Task List')),
                                    body: TaskListDisplay(eventItem: event)),
                              ));
                        },
                      ),
                      const SizedBox(width: 8),
                    ],
                  ),
                ],
              ),
            ))
      ],
    );
  }
}

class EventInDaysDisplay extends StatefulWidget {
  final DateTime begin;
  final DateTime end;
  final Function() changeCallBack;

  EventInDaysDisplay(
      {Key? key,
      required this.begin,
      required this.end,
      required this.changeCallBack})
      : super(key: key);

  @override
  _EventInDaysDisplayState createState() => _EventInDaysDisplayState();
}

class _EventInDaysDisplayState extends State<EventInDaysDisplay> {
  List<EventItem> events = List.empty(growable: true);

  @override
  Widget build(BuildContext context) {
    EventRetriever.retrieveEventFromStartEnd(this.widget.begin, this.widget.end)
        .then((value) => {
              if (!listEquals(events, value))
                {
                  setState(() {
                    events = value;
                  })
                }
            });

    return Column(children: [
      Text(DateFormat('EEE, MMM d').format(widget.begin)),
      Divider(color: Colors.black),
      ConstrainedBox(
          constraints: new BoxConstraints(
            minHeight: 50,
          ),
          child: ListView(
            addAutomaticKeepAlives: false,
            children: events
                .map((e) => EventDisplay(
                    event: e,
                    callback: () => {
                          showDialog(
                              context: context,
                              builder: (BuildContext context) {
                                return EventEditor(
                                    event: e,
                                    saveCallback:
                                        (start, end, label, description, id) {
                                      if (start != null &&
                                          end != null &&
                                          start.isBefore(end)) {
                                        EventRetriever.modifyEvent(
                                            id, start, end, label, description);
                                        this.setState(() {});
                                        ScaffoldMessenger.of(context)
                                            .showSnackBar(
                                          const SnackBar(
                                              content: Text('Event Modified!')),
                                        );
                                        Navigator.pop(context);
                                        this.widget.changeCallBack();
                                      }
                                    },
                                    removeCallback: (e) {
                                      EventRetriever.removeEvent(
                                          e.id,
                                          e.startTime,
                                          e.endTime,
                                          e.label,
                                          e.description);
                                      this.setState(() {});
                                      ScaffoldMessenger.of(context)
                                          .showSnackBar(
                                        const SnackBar(
                                            content: Text('Event Removed!')),
                                      );
                                      Navigator.pop(context);
                                      this.widget.changeCallBack();
                                    });
                              })
                        }))
                .toList(),
            shrinkWrap: true,
            physics: NeverScrollableScrollPhysics(),
          ))
    ]);
  }
}

class DayDisplay extends StatelessWidget {
  final DateTime startDate;

  final Function() callback;

  const DayDisplay({Key? key, required this.startDate, required this.callback})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
            backgroundColor: Colors.blue, title: const Text('Date View')),
        body: InfiniteListView.builder(
            itemBuilder: (_, index) {
              DateTime startTime = this.startDate.add(Duration(days: index));
              DateTime endTime = startTime.add(Duration(days: 1));
              return EventInDaysDisplay(
                  begin: startTime, end: endTime, changeCallBack: callback);
            },
            addAutomaticKeepAlives: false));
  }
}
