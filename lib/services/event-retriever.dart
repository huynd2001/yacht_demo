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
}

class EventRetriever {
  static const eventRetriever = MethodChannel('calendar/events');

  static final List<EventItem> _events = List.empty(growable: true);

  static DateTime today() => new DateTime(
      DateTime.now().year, DateTime.now().month, DateTime.now().day, 0, 0, 0);

  static void init() {
    // List<String> normalEvents = await eventRetriever.invokeMethod('getEvents');
  }

  // static void addEvent(EventItem event) {
  //   _events.add(event);
  // }

  // static void removeEvent(EventItem event) {
  //   EventItem reEvent = _events.firstWhere((EventItem e) => (event.id == e.id));
  //   _events.remove(reEvent);
  // }

  static Future<List<EventItem>> retrieveEventFromStartEnd(
      DateTime start, DateTime end) async {
    List<Map<String, String>> results = await eventRetriever.invokeMethod(
        'getEvents', <String, String>{
      'start': start.toIso8601String(),
      'end': end.toIso8601String()
    });
    List<Map<String, String>> eventsJson =
        results.map((e) => Map<String, String>.from(e)).toList();
    return eventsJson.map((e) => EventItem.fromJson(e)).toList();
  }

  static Future<bool> createEvent(DateTime startTime, DateTime endTime,
      String label, String description) async {
    int id = await eventRetriever.invokeMethod('createEvent', <String, String>{
      'start': startTime.toIso8601String(),
      'end': endTime.toIso8601String(),
      'label': label,
      'description': description
    });
    return id != -1;
  }

  static Future<bool> removeEvent(int eventID, DateTime startTime,
      DateTime endTime, String label, String description) async {
    return await eventRetriever.invokeMethod('removeEvent', <String, String>{
      'id': eventID.toString(),
      'start': startTime.toIso8601String(),
      'end': endTime.toIso8601String(),
      'label': label,
      'description': description
    }) as bool;
  }

  static Future<bool> modifyEvent(int eventID, DateTime startTime,
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
