import 'package:flutter/foundation.dart';
import 'package:flutter/widgets.dart';
import 'package:yacht_demo/services/task-retriever.dart';
import 'package:yacht_demo/task-list.dart';

class TasksWidget extends StatefulWidget {
  TasksWidget({Key? key}) : super(key: key);

  @override
  _TasksWidgetState createState() => _TasksWidgetState();
}

class _TasksWidgetState extends State<TasksWidget> {
  List<TaskItem> tasks = List.empty(growable: true);

  gettingTasks() {
    TaskRetriever.getUnfinishedTask().then((value) => {
          if (!listEquals(tasks, value))
            {
              setState(() {
                tasks = value;
              })
            }
        });
  }

  @override
  Widget build(BuildContext context) {
    gettingTasks();
    return (!tasks.isEmpty)
        ? ListView(
            shrinkWrap: true,
            children: tasks
                .map((t) => TaskItemDisplay(
                    taskItem: t,
                    callback: (val) {
                      TaskRetriever.ticking(t);
                      this.setState(() {});
                    }))
                .toList(),
          )
        : Text('Good job! You have finish every task there is!');
  }
}
