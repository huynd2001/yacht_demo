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
  WeatherItem(DateTime startTime, DateTime endTime, this.temp, this.weather, this.windSpeed) : super(startTime, endTime);

  String weather;
  int windSpeed;
  int temp;
}

class WeatherDisplay extends State<WeatherWidget> {

  static const platform = MethodChannel('yacht/weather');

  List<WeatherItem> _weatherList = newList();

  Future<String> _getWeather() async {
    try {
      final String result = await platform.invokeMethod('getWeather');
      return result;
    } on PlatformException catch (e) {
      return 'Error';
    }
  }

  static List<WeatherItem> newList() {

    // DateTime today = new DateTime(DateTime.now().year, DateTime.now().month, DateTime.now().day, 0, 0, 0);

    // return List.generate(7, (index) => today.add(new Duration(hours: Duration.hoursPerDay * index)))
    //     .map((d) => WeatherItem(d, d.add(new Duration(hours: Duration.hoursPerDay)) , 'SUNNY')).toList();

    return MockCalendar().mockList();

  }

  @override
  Widget build(BuildContext context) {

    List<Widget> widgets = _weatherList.map((d) => Card(
        child: Container(
          width: 80,
          height: 100,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: <Widget>[
              ListTile(
                title: Column(
                  children: <Widget>[
                    Text(d.startTime.day.toString() + '-' + d.startTime.month.toString())
                  ]
                ),
                subtitle: Column(
                    children: <Widget>[
                      Image(image: AssetImage('assets/weather/${d.weather}.png')),
                      Text('${d.temp} F'),
                      Text('${d.windSpeed} mph')
                  ]
                )
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

class MockCalendar {
  List<WeatherItem> mockList() {

    DateTime today = new DateTime(DateTime.now().year, DateTime.now().month, DateTime.now().day, 0, 0, 0);

    return <WeatherItem>[
      WeatherItem(today.add(new Duration(days: 0)), today.add(new Duration(days: 1)), 78, 'clearday', 8),
      WeatherItem(today.add(new Duration(days: 1)), today.add(new Duration(days: 2)), 70, 'clearday', 6),
      WeatherItem(today.add(new Duration(days: 2)), today.add(new Duration(days: 3)), 67, 'clearday', 4),
      WeatherItem(today.add(new Duration(days: 3)), today.add(new Duration(days: 4)), 71, 'clearday', 8),
      WeatherItem(today.add(new Duration(days: 4)), today.add(new Duration(days: 5)), 68, 'rainyday', 6),
      WeatherItem(today.add(new Duration(days: 5)), today.add(new Duration(days: 6)), 68, 'rainyday', 8),
      WeatherItem(today.add(new Duration(days: 6)), today.add(new Duration(days: 7)), 64, 'foggyday', 7),
    ];
  }
}