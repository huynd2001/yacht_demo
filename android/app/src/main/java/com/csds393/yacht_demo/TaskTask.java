package com.csds393.yacht_demo;

import android.os.AsyncTask;

import com.csds393.yacht.calendar.CalendarEvent;
import com.csds393.yacht.calendar.Task;
import com.csds393.yacht.database.DB;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.flutter.plugin.common.MethodChannel;

public class TaskTask {

    static class GetTaskTask extends AsyncTask<Integer, Integer, List<Task>> {

        MethodChannel.Result result;
        long eventId;

        public GetTaskTask(Long eventId, MethodChannel.Result result){
            super();
            this.eventId = eventId;
            this.result = result;
        }

        protected List<Task> doInBackground(Integer... input) {
            int count = input.length;
            List<Task> totalList = new ArrayList<>();
            for (int i = 0; i < count; i++) {

                List<Task> list = DB.getInstance().getCalendarDao().getTasksForEvent(eventId);

                totalList.addAll(list);

                publishProgress((int) ((i / (float) count) * 100));
                // Escape early if cancel() is called
                if (isCancelled()) break;
            }
            return totalList;
        }

        protected void onProgressUpdate(Integer... progress) {
//            setProgressPercent(progress[0]);
        }

        protected void onPostExecute(List<Task> tasks) {


            if(tasks == null) {
                result.error("Lmao", "Internal Error: null found for getting", "lmao");
                return;
            }

            result.success(tasks.stream().map(task -> {
                Map<String, String> retMap = new HashMap<>();
                retMap.put("taskName", task.getName());
                retMap.put("taskId", String.valueOf(task.getTaskID()));
                retMap.put("isFinished", String.valueOf(task.getCompleted()));
                return retMap;
            }).collect(Collectors.toList()));
        }
    }

    static class GetUnfinishedTaskTask extends AsyncTask<Integer, Integer, List<Task>> {

        MethodChannel.Result result;

        public GetUnfinishedTaskTask(MethodChannel.Result result){
            super();
            this.result = result;
        }

        protected List<Task> doInBackground(Integer... input) {
            int count = input.length;

            List<Task> tasks = new ArrayList<>();

            for (int i = 0; i < count; i++) {

                Map<String, String> newMap = new HashMap<>();

                tasks.addAll(DB.getInstance()
                        .getCalendarDao()
                        .getIncompleteTasksByDateMap().entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(Map.Entry::getValue)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList()));

                publishProgress((int) ((i / (float) count) * 100));
                // Escape early if cancel() is called
                if (isCancelled()) break;
            }
            return tasks;
        }

        protected void onProgressUpdate(Integer... progress) {
//            setProgressPercent(progress[0]);
        }

        protected void onPostExecute(List<Task> tasks) {

            result.success(tasks.stream()
                .map(task -> {
                    Map<String, String> retMap = new HashMap<>();
                    retMap.put("taskName", task.getName());
                    retMap.put("taskId", String.valueOf(task.getTaskID()));
                    retMap.put("isFinished", String.valueOf(task.getCompleted()));
                    return retMap;
                }).collect(Collectors.toList()));
        }
    }

    static class TickTaskTask extends AsyncTask<Integer, Integer, Integer> {

        MethodChannel.Result result;
        Boolean isCompleted;
        String taskName;
        Long taskID;

        public TickTaskTask(Boolean isCompleted, String taskName, Long taskID, MethodChannel.Result result){
            super();
            this.taskName = taskName;
            this.taskID = taskID;
            this.isCompleted = isCompleted;
            this.result = result;
        }

        protected Integer doInBackground(Integer... input) {
            int count = input.length;
            for (int i = 0; i < count; i++) {

                Map<String, String> newMap = new HashMap<>();

                newMap.put("name", taskName);
                newMap.put("completed", isCompleted.toString());
                newMap.put("taskID", taskID.toString());

                DB.getInstance()
                        .getCalendarDao()
                        .updateTask(Task.fromMap(newMap));

                publishProgress((int) ((i / (float) count) * 100));
                // Escape early if cancel() is called
                if (isCancelled()) break;
            }
            return 0;
        }

        protected void onProgressUpdate(Integer... progress) {
//            setProgressPercent(progress[0]);
        }

        protected void onPostExecute(Integer nothing) {

            result.success(null);
        }
    }

    static class CreateTaskTask extends AsyncTask<Integer, Integer, Integer> {

        String taskName;
        Long eventId;
        MethodChannel.Result result;

        public CreateTaskTask(String taskName, Long eventId, MethodChannel.Result result){
            super();
            this.result = result;
            this.taskName = taskName;
            this.eventId = eventId;
        }

        protected Integer doInBackground(Integer... ids) {
            int count = ids.length;

            for (int i = 0; i < count; i++) {

                DB.getInstance()
                        .getCalendarDao()
                        .insertTask(taskName, eventId);

                publishProgress((int) ((i / (float) count) * 100));
                // Escape early if cancel() is called
                if (isCancelled()) break;
            }
            return null;
        }



        protected void onProgressUpdate(Integer... progress) {
//            setProgressPercent(progress[0]);
        }

        protected void onPostExecute(Integer nothing) {

            result.success(null);
        }
    }
}
