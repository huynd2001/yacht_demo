import 'package:flutter/services.dart';

class EventItem {
  final DateTime startTime;
  final DateTime endTime;
  final String description;
  final String label;
  final int id;

  const EventItem._(
      this.startTime, this.endTime, this.label, this.description, this.id);

  EventItem.fromJson(Map<String, String> json)
      : startTime = DateTime.parse(json['startTime'].toString()),
        endTime = DateTime.parse(json['endTime'].toString()),
        description = json['description'].toString(),
        label = json['label'].toString(),
        id = int.parse(json['id'] ?? "-1");

  Map<String, String> toJson() => {
        'startTime': startTime.toIso8601String(),
        'endTime': endTime.toIso8601String(),
        'label': label,
        'description': description,
        'id': id.toString()
      };

  @override
  bool operator ==(other) {
    return (other is EventItem) && (other.id == id);
  }

  @override
  int get hashCode => super.hashCode;
}

class EventRetriever {
  static const eventRetriever = MethodChannel('calendar/events');

  static DateTime today() => new DateTime(
      DateTime.now().year, DateTime.now().month, DateTime.now().day, 0, 0, 0);

  static void init() {
    // List<String> normalEvents = await eventRetriever.invokeMethod('getEvents');
  }

  static Future<List<EventItem>> retrieveEventFromStartEnd(
      DateTime start, DateTime end) async {
    List<dynamic> results = await eventRetriever.invokeMethod(
        'getEvents', <String, String>{
      'start': start.toIso8601String(),
      'end': end.toIso8601String()
    });

    // print('${results.length} retrieved for range ${start} til ${end}');
    List<Map<String, String>> eventsJson =
        results.map((e) => Map<String, String>.from(e)).toList();
    return eventsJson.map((e) => EventItem.fromJson(e)).toList();
  }

  static Future<void> createEvent(DateTime startTime, DateTime endTime,
      String label, String description) async {
    await eventRetriever.invokeMethod('createEvent', <String, String>{
      'start': startTime.toIso8601String(),
      'end': endTime.toIso8601String(),
      'label': label,
      'description': description
    });
  }

  static Future<void> removeEvent(int eventID, DateTime startTime,
      DateTime endTime, String label, String description) async {
    return await eventRetriever.invokeMethod('removeEvent', <String, String>{
      'id': eventID.toString(),
      'start': startTime.toIso8601String(),
      'end': endTime.toIso8601String(),
      'label': label,
      'description': description
    });
  }

  static Future<void> modifyEvent(int eventID, DateTime startTime,
      DateTime endTime, String label, String description) async {
    return await eventRetriever.invokeMethod('modifyEvent', <String, String>{
      'id': eventID.toString(),
      'start': startTime.toIso8601String(),
      'end': endTime.toIso8601String(),
      'label': label,
      'description': description
    });
  }
}
