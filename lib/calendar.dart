import 'package:flutter/cupertino.dart';

class Calendar extends StatelessWidget {
  const Calendar({ Key? key }) : super(key: key);

  @override
  Widget build(BuildContext context) => Text(
    'Index 0: Calendar',
    style: TextStyle(fontSize: 30, fontWeight: FontWeight.bold),
  );
}

class Event {

  DateTime startTime;
  DateTime endTime;

  Event(this.startTime, this.endTime);
}

class MockCalendar {
  const MockCalendar();

}