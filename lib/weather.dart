import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'calendar.dart';

class Weather extends StatelessWidget {
  const Weather({ Key? key }) : super(key: key);

  @override
  Widget build(BuildContext context) =>
      Text("Weather");
}

class WeatherWidget extends StatefulWidget {
  const WeatherWidget({Key? key}) : super(key: key);

  @override
  WeatherDisplay createState() => WeatherDisplay();
}

class WeatherItem extends DateItem {
  WeatherItem(DateTime startTime, DateTime endTime, this.weather) : super(startTime, endTime);

  String weather;
}

class WeatherDisplay extends State<WeatherWidget> {

  static const platform = MethodChannel('yacht/weather');

  List<WeatherItem> _dateList = newList();

  Future<String> _getWeather() async {
    try {
      final String result = await platform.invokeMethod('getWeather');
      return result;
    } on PlatformException catch (e) {
      return 'Error';
    }
  }

  static List<WeatherItem> newList() {

    DateTime today = new DateTime(DateTime.now().year, DateTime.now().month, DateTime.now().day, 0, 0, 0);



    return List.generate(7, (index) => today.add(new Duration(hours: Duration.hoursPerDay * index)))
        .map((d) => WeatherItem(d, d.add(new Duration(hours: Duration.hoursPerDay)) , 'SUNNY')).toList();
  }

  @override
  Widget build(BuildContext context) {

    List<Widget> widgets = _dateList.map((d) => Card(
        child: Container(
          width: 80,
          height: 100,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: <Widget>[
              ListTile(
                title: Text(d.startTime.day.toString() + '-' + d.startTime.month.toString()),
                subtitle: Text(d.weather)
              )
            ],
          ),
        )
    )).cast<Widget>().toList();

    return Column(
        children: <Widget>[
          Container(
            height: 200,
            child: ListView(
                shrinkWrap: true,
                padding: const EdgeInsets.all(20.0),
                scrollDirection: Axis.horizontal,
                children: widgets
            ),
          )
        ]
    );
  }
}