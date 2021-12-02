import 'package:flutter/services.dart';

import '../weather.dart';

class WeatherRetriever {
  static const _weatherRetriever = MethodChannel('calendar/weather');

  static void init() {}

  static Future<WeatherItem> retrieveDayWeather(
      DateTime start, DateTime end) async {
    var results = await _weatherRetriever.invokeMethod(
        'getWeather', <String, String>{
      'start': start.toIso8601String(),
      'end': end.toIso8601String()
    });
    Map<String, String> weatherJsons = Map<String, String>.from(results);
    return WeatherItem.fromJson(weatherJsons);
  }
}
