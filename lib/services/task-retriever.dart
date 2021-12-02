import 'package:flutter/services.dart';

import 'event-retriever.dart';

class TaskItem {
  final String taskName;
  final bool isFinished;
  final int taskId;

  const TaskItem._(this.taskName, this.isFinished, this.taskId);

  TaskItem.fromJson(Map<String, String> json)
      : taskName = json['taskName'].toString(),
        isFinished = (json['endTime'].toString().toLowerCase() == 'true'),
        taskId = int.parse(json['taskId'] ?? "-1");

  Map<String, String> toJson() => {
        'taskName': taskName,
        'isFinished': isFinished.toString(),
        'taskId': taskId.toString(),
      };

  @override
  bool operator ==(other) {
    return (other is TaskItem) && (other.taskId == taskId);
  }

  @override
  int get hashCode => super.hashCode;
}

class TaskRetriever {
  static const taskRetriever = MethodChannel('calendar/tasks');

  static DateTime today() => new DateTime(
      DateTime.now().year, DateTime.now().month, DateTime.now().day, 0, 0, 0);

  static void init() {
    // List<String> normalEvents = await eventRetriever.invokeMethod('getEvents');
  }

  static Future<List<TaskItem>> getTasks(EventItem e) async {
    // print(
    //     'Retrieving events from ${start.toIso8601String()} til ${end.toIso8601String()}');
    List<dynamic> results =
        await taskRetriever.invokeMethod('getTasks', <String, String>{
      'id': e.id.toString(),
      'start': e.startTime.toIso8601String(),
      'end': e.endTime.toIso8601String(),
      'label': e.label,
      'description': e.description
    });

    // print('${results.length} retrieved for range ${start} til ${end}');
    List<Map<String, String>> eventsJson =
        results.map((e) => Map<String, String>.from(e)).toList();
    return eventsJson.map((e) => TaskItem.fromJson(e)).toList();
  }

  static Future<void> createTask(EventItem e, String taskName) async {
    await taskRetriever.invokeMethod('createTask', <String, String>{
      'id': e.id.toString(),
      'start': e.startTime.toIso8601String(),
      'end': e.endTime.toIso8601String(),
      'label': e.label,
      'description': e.description,
      'taskName': taskName
    });
  }

  static Future<void> finishTask(EventItem e, TaskItem taskItem) async {
    return await taskRetriever.invokeMethod('finishTask', <String, String>{
      'id': e.id.toString(),
      'start': e.startTime.toIso8601String(),
      'end': e.endTime.toIso8601String(),
      'label': e.label,
      'description': e.description,
      'taskName': taskItem.taskName,
      'taskId': taskItem.taskId.toString()
    });
  }

  static Future<List<TaskItem>> getUnfinishedTask() async {
    List<dynamic> tasks = await taskRetriever
        .invokeMethod('getUnfinishedTasks', <String, String>{});
    return tasks
        .map((e) => Map<String, String>.from(e))
        .map((e) => TaskItem.fromJson(e))
        .toList();
  }
}
