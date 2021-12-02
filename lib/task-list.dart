import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:yacht_demo/services/event-retriever.dart';
import 'package:yacht_demo/services/task-retriever.dart';

import 'components/task-creator.dart';

class TaskItemDisplay extends StatelessWidget {
  final TaskItem taskItem;
  final Function(bool) callback;
  const TaskItemDisplay(
      {Key? key, required this.taskItem, required this.callback})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return CheckboxListTile(
        title: Text(taskItem.taskName),
        value: taskItem.isFinished,
        onChanged: (val) {
          callback(val!);
        });
  }
}

class TaskListDisplay extends StatefulWidget {
  final EventItem eventItem;

  TaskListDisplay({Key? key, required this.eventItem}) : super(key: key);

  @override
  _TaskListDisplayState createState() => _TaskListDisplayState();
}

class _TaskListDisplayState extends State<TaskListDisplay> {
  List<TaskItem> tasks = List.empty(growable: true);

  gettingTasks() {
    TaskRetriever.getTasks(widget.eventItem).then((value) => {
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
    return Row(
      children: [
        ListView(
          children: tasks
              .map((e) => TaskItemDisplay(
                  taskItem: e,
                  callback: (val) {
                    setState(() {});
                  }))
              .toList(),
          physics: NeverScrollableScrollPhysics(),
        ),
        ElevatedButton(
            onPressed: () {
              showDialog(
                  context: context,
                  builder: (BuildContext context) {
                    return TaskCreator(callback: (taskName) {
                      TaskRetriever.createTask(this.widget.eventItem, taskName);
                      ScaffoldMessenger.of(context).showSnackBar(
                        const SnackBar(content: Text('Task Added!')),
                      );
                      Navigator.pop(context);
                      this.setState(() {});
                    });
                  });
            },
            child: Text('ADD EVENT')),
      ],
    );
  }
}
