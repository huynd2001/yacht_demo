import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class EventItem {

  final DateTime startTime;
  final DateTime endTime;
  final String task;

  const EventItem(this.startTime, this.endTime, this.task);
}

class DateItem {
  final DateTime startTime;
  final DateTime endTime;

  List<EventItem> events = List.empty();
  DateItem(this.startTime, this.endTime);

  DateItem add(EventItem item) {
    events.add(item);
    return this;
  }

}

class DateWidget extends StatefulWidget {
  const DateWidget({Key? key}) : super(key: key);

  @override
  CalendarDisplay createState() => CalendarDisplay();
}

class CalendarDisplay extends State<DateWidget> {
  
  List<DateItem> _dateList = newList();

  static List<DateItem> newList() {

    DateTime today = new DateTime(DateTime.now().year, DateTime.now().month, DateTime.now().day, 0, 0, 0);

    return List.generate(7, (index) => today.add(new Duration(hours: Duration.hoursPerDay * index)))
        .map((d) => DateItem(d, d.add(new Duration(hours: Duration.hoursPerDay)))).toList();
  }

  @override
  Widget build(BuildContext context) {

    List<Widget> widgets = _dateList.map((d) => Card(
        child: Container(
          width: 80,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: <Widget>[
              ListTile(
                title: Text(d.startTime.day.toString() + '-' + d.startTime.month.toString()),
              )
            ],
          ),
        )
    )).cast<Widget>().toList();
    widgets.add(ElevatedButton(
      onPressed: () {},
      child: const Text('ADD EVENT'),
    ));

    return ListView(
      shrinkWrap: true,
      padding: const EdgeInsets.all(20.0),
      scrollDirection: Axis.horizontal,
      children: widgets
    );
  }
}