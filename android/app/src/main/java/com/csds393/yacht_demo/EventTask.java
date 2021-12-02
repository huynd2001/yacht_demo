package com.csds393.yacht_demo;

import android.os.AsyncTask;

import com.csds393.yacht.calendar.CalendarEvent;
import com.csds393.yacht.database.DB;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.flutter.plugin.common.MethodChannel;

public class EventTask {

    static class GetEventTask extends AsyncTask<Integer, Integer, List<CalendarEvent>> {

        LocalDateTime startDate, endDate;
        MethodChannel.Result result;
        String start, end;

        public GetEventTask(String start, String end, MethodChannel.Result result){
            super();
            this.startDate = LocalDateTime.parse(start);
            this.endDate = LocalDateTime.parse(end);
            this.start = start;
            this.end = end;
            this.result = result;
        }

        protected List<CalendarEvent> doInBackground(Integer... input) {
            int count = input.length;
            List<CalendarEvent> totalList = new ArrayList<>();
            for (int i = 0; i < count; i++) {

                List<CalendarEvent> list = DB.getInstance()
                        .getCalendarDao()
                        .getEventsStartingOnDay(this.startDate.toLocalDate());

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

        protected void onPostExecute(List<CalendarEvent> events) {


            if(events == null) {
                result.error("Lmao", "Internal Error: null found for getting", "lmao");
                return;
            }

            result.success(events.stream().map(event -> {
                Map<String, String> retMap = new HashMap<>();
                retMap.put("start", event.getStartDate().atTime(event.getStartTime()).toString());
                retMap.put("end", event.getEndDate().atTime(event.getEndTime()).toString());
                retMap.put("label", event.getDetails().getLabel());
                retMap.put("description", event.getDetails().getDescription());
                retMap.put("id", Optional.ofNullable(event.getId()).orElse(-1L).toString());
                return retMap;
            }).collect(Collectors.toList()));
        }
    }

    static class CreateEventTask extends AsyncTask<Integer, Integer, Integer> {

        LocalDateTime startDate, endDate;
        MethodChannel.Result result;
        String label;
        String description;

        public CreateEventTask(String start, String end, String label, String description, MethodChannel.Result result){
            super();
            this.startDate = LocalDateTime.parse(start);
            this.endDate = LocalDateTime.parse(end);
            this.result = result;
            this.label = label;
            this.description = description;
        }

        protected Integer doInBackground(Integer... input) {
            int count = input.length;

            for (int i = 0; i < count; i++) {

                Map<String, String> newMap = new HashMap<>();
                newMap.put("startDate", startDate.toLocalDate().toString());
                newMap.put("endDate", endDate.toLocalDate().toString());
                newMap.put("startTime", startDate.toLocalTime().toString());
                newMap.put("endTime", endDate.toLocalTime().toString());
                newMap.put("label", label);
                newMap.put("description", description);

                DB.getInstance()
                        .getCalendarDao()
                        .insertEvent(CalendarEvent.fromMap(newMap));

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

    static class ModifyEventTask extends AsyncTask<Integer, Integer, Integer> {

        LocalDateTime startDate, endDate;
        MethodChannel.Result result;
        String label;
        String description;

        public ModifyEventTask(String start, String end, String label, String description, MethodChannel.Result result){
            super();
            this.startDate = LocalDateTime.parse(start);
            this.endDate = LocalDateTime.parse(end);
            this.result = result;
            this.label = label;
            this.description = description;
        }

        protected Integer doInBackground(Integer... ids) {
            int count = ids.length;

            for (int i = 0; i < count; i++) {

                Map<String, String> newMap = new HashMap<>();
                newMap.put("startDate", startDate.toLocalDate().toString());
                newMap.put("endDate", endDate.toLocalDate().toString());
                newMap.put("startTime", startDate.toLocalTime().toString());
                newMap.put("endTime", endDate.toLocalTime().toString());
                newMap.put("label", label);
                newMap.put("description", description);
                newMap.put("id", ids[i].toString());

                DB.getInstance()
                        .getCalendarDao()
                        .updateEvent(CalendarEvent.fromMap(newMap));

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

    static class RemoveEventTask extends AsyncTask<Integer, Integer, Integer> {

        LocalDateTime startDate, endDate;
        MethodChannel.Result result;
        String label;
        String description;

        public RemoveEventTask(String start, String end, String label, String description, MethodChannel.Result result){
            super();
            this.startDate = LocalDateTime.parse(start);
            this.endDate = LocalDateTime.parse(end);
            this.result = result;
            this.label = label;
            this.description = description;
        }

        protected Integer doInBackground(Integer... ids) {
            int count = ids.length;

            for (int i = 0; i < count; i++) {

                Map<String, String> newMap = new HashMap<>();
                newMap.put("startDate", startDate.toLocalDate().toString());
                newMap.put("endDate", endDate.toLocalDate().toString());
                newMap.put("startTime", startDate.toLocalTime().toString());
                newMap.put("endTime", endDate.toLocalTime().toString());
                newMap.put("label", label);
                newMap.put("description", description);
                newMap.put("id", ids[i].toString());

                DB.getInstance()
                        .getCalendarDao()
                        .deleteEvent(CalendarEvent.fromMap(newMap));

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
