import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:infinite_listview/infinite_listview.dart';
import 'package:intl/intl.dart';
import 'package:yacht_demo/services/event-retriever.dart';
import 'package:yacht_demo/services/weather-retriever.dart';

import 'calendar.dart';

class Weather extends StatelessWidget {
  const Weather({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) => Text("Weather");
}

class WeatherWidget extends StatefulWidget {
  const WeatherWidget({Key? key}) : super(key: key);

  @override
  WeatherDisplay createState() => WeatherDisplay();
}

class WeatherItem extends DateItem {
  WeatherItem._(DateTime startTime, DateTime endTime, this.temp, this.weather,
      this.windSpeed)
      : super(startTime, endTime);

  String weather;
  int windSpeed;
  int temp;

  factory WeatherItem.fromJson(Map<String, String> json) {
    return WeatherItem._(
        DateTime.parse(json['startTime'].toString()),
        DateTime.parse(json['endTime'].toString()),
        json['temp'] as int,
        json['weather'].toString(),
        json['windSpeed'] as int);
  }

  Map<String, String> toJson() => {
        'startTime': startTime.toIso8601String(),
        'endTime': endTime.toIso8601String(),
        'temp': temp.toString(),
        'weather': weather.toString(),
        'windSpeed': windSpeed.toString()
      };
}

class WeatherItemDisplay extends StatefulWidget {
  WeatherItemDisplay({Key? key, required this.startTime, required this.endTime})
      : super(key: key);

  final DateTime startTime;
  final DateTime endTime;

  @override
  _WeatherItemDisplayState createState() => _WeatherItemDisplayState();
}

class _WeatherItemDisplayState extends State<WeatherItemDisplay> {
  String _temp = "-";
  String _windSpeed = "-";
  String _weather = "notloaded";

  @override
  void initState() {
    super.initState();
    WeatherRetriever.retrieveDayWeather(widget.startTime, widget.endTime)
        .then((value) => {
              setState(() {
                _temp = value.temp as String;
                _windSpeed = value.windSpeed as String;
                _weather = value.weather;
              })
            });
  }

  @override
  Widget build(BuildContext context) {
    return Card(
        child: Container(
      width: 80,
      height: 100,
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: <Widget>[
          ListTile(
              title: Column(children: <Widget>[
                Text(DateFormat('M-d').format(widget.startTime))
              ]),
              subtitle: Column(children: <Widget>[
                Image(image: AssetImage('assets/weather/$_weather.png')),
                Text('$_temp F'),
                Text('$_windSpeed mph')
              ]))
        ],
      ),
    ));
  }
}

class WeatherDisplay extends State<WeatherWidget> {
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
              return WeatherItemDisplay(startTime: startTime, endTime: endTime);
            },
            scrollDirection: Axis.horizontal),
      )
    ]);
  }
}
