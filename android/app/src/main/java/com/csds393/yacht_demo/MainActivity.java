package com.csds393.yacht_demo;

import android.os.AsyncTask;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.csds393.yacht.calendar.CalendarDao;
import com.csds393.yacht.calendar.CalendarEvent;
import com.csds393.yacht.database.DB;
import com.csds393.yacht.settings.Settings;
import com.csds393.yacht.weather.DayWeather;
import com.csds393.yacht.weather.HalfDayWeather;
import com.csds393.yacht.weather.Weather;

import java.util.Optional;
import java.util.Random;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity {
    private static final String WEATHER_CHANNEL = "calendar/weather";
    private static final String EVENTS_CHANNEL = "calendar/events";

    private static class CallAPITask extends AsyncTask<Pair<Float, Float>, Integer, List<DayWeather>> {

        LocalDate startDate, endDate;
        MethodChannel.Result result;
        String start, end;

        public CallAPITask(String start, String end, MethodChannel.Result result){
            super();
            this.startDate = LocalDateTime.parse(start).toLocalDate();
            this.endDate = LocalDateTime.parse(end).toLocalDate();
            this.start = start;
            this.end = end;
            this.result = result;
        }

        protected List<DayWeather> doInBackground(Pair<Float, Float>... locations) {
            int count = locations.length;
            List<DayWeather> totalList = new ArrayList<>();
            for (int i = 0; i < count; i++) {

                List<DayWeather> list = Weather.INSTANCE.getForecast(locations[i].first, locations[i].second);

                if(list == null)
                    totalList.addAll(new ArrayList<>());
                else
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

        protected void onPostExecute(List<DayWeather> weathers) {
            HalfDayWeather weatherDate = weathers.stream()
                    .filter(w -> w.getDate().equals(startDate))
                    .map(DayWeather::getMorningWeather)
                    .findFirst().orElse(null);

            Map<String, String> returnValue = new HashMap<>();
            if(weatherDate == null) {
                result.error("Lmao", "Mo weather found", "lmao");
                return ;
            }
            returnValue.put("temp", String.valueOf(weatherDate.getTemperature()));
            returnValue.put("windSpeed", String.valueOf(weatherDate.getWindSpeed().getStart()));

            String weatherString;
            switch(weatherDate.getSky()) {
                case SNOW:
                    weatherString = "snow";
                    break;
                case CLEAR:
                case SUNNY:
                    weatherString = "clearday";
                    break;
                case RAIN:
                    weatherString = "rainyday";
                    break;
                case CLOUDY:
                    weatherString = "partlycloudyday";
                    break;
                default:
                    weatherString = "notloaded";
            }

            returnValue.put("weather", weatherString);
            returnValue.put("startTime", start);
            returnValue.put("endTime", end);

            result.success(returnValue);
        }
    }

    private static class GetEventTask extends AsyncTask<Integer, Integer, List<CalendarEvent>> {

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

    private static class CreateEventTask extends AsyncTask<Integer, Integer, Integer> {

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

    private static class ModifyEventTask extends AsyncTask<Integer, Integer, Integer> {

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

    private static class RemoveEventTask extends AsyncTask<Integer, Integer, Integer> {

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

    public static Optional<Integer> parseInt(String toParse) {
        try {
            return Optional.of(Integer.parseInt(toParse));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {

        super.configureFlutterEngine(flutterEngine);

        DB.initializeDB(this.getApplicationContext());
        Settings.init(this.getApplicationContext());

        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), WEATHER_CHANNEL)
            .setMethodCallHandler(
            (call, result) -> {
                if ("getWeather".equals(call.method)) {
                    String start = call.argument("start");
                    String end = call.argument("end");

                    new CallAPITask(start, end, result).execute(new Pair<>(41.499321f, -81.694359f));
                }
            }
        );

        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), EVENTS_CHANNEL)
                .setMethodCallHandler(
                        (call, result) -> {
                            String start = call.argument("start");
                            String end = call.argument("end");
                            String id = call.argument("id");
                            String label = call.argument("label");
                            String description = call.argument("description");
                            switch(call.method) {
                                case "getEvents":
                                    new GetEventTask(start, end, result).execute(1);
                                    break;
                                case "removeEvent":
                                    new RemoveEventTask(start, end, label, description, result).execute(parseInt(id).orElse(-1));
                                    break;
                                case "modifyEvent":
                                    new ModifyEventTask(start, end, label, description, result).execute(parseInt(id).orElse(-1));
                                    break;
                                case "createEvent":
                                    new CreateEventTask(start, end, label, description, result).execute(1);
                                    break;
                                default:
                                    ;
                            }
                        }
                );
    }
}
