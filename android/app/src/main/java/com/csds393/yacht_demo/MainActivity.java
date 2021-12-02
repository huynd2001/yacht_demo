package com.csds393.yacht_demo;

import android.os.AsyncTask;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.csds393.yacht.database.DB;
import com.csds393.yacht.settings.Settings;

import java.util.Optional;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity {
    private static final String WEATHER_CHANNEL = "calendar/weather";
    private static final String EVENTS_CHANNEL = "calendar/events";
    private static final String TASKS_CHANNEL = "calendar/tasks";
    private static final String RECUR_EVENTS_CHANNEL = "calendar/recur";

    public static Optional<Integer> parseInt(String toParse) {
        try {
            return Optional.of(Integer.parseInt(toParse));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Optional<Long> parseLong(String toParse) {
        try {
            return Optional.of(Long.parseLong(toParse));
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

                    new WeatherTask.GetWeatherTask(start, end, result).execute(new Pair<>(41.499321f, -81.694359f));
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
                                    new EventTask.GetEventTask(start, end, result).execute(1);
                                    break;
                                case "removeEvent":
                                    new EventTask.RemoveEventTask(start, end, label, description, result).execute(parseInt(id).orElse(-1));
                                    break;
                                case "modifyEvent":
                                    new EventTask.ModifyEventTask(start, end, label, description, result).execute(parseInt(id).orElse(-1));
                                    break;
                                case "createEvent":
                                    new EventTask.CreateEventTask(start, end, label, description, result).execute(1);
                                    break;
                                default:
                                    ;
                            }
                        }
                );

        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), TASKS_CHANNEL)
                .setMethodCallHandler(
                        (call, result) -> {
                            String id = call.argument("id");
                            String taskName = call.argument("taskName");
                            String taskId = call.argument("taskId");
                            String finished = call.argument("isFinished");
                            String start = call.argument("start");
                            String end = call.argument("end");
                            String label = call.argument("label");
                            String description = call.argument("description");
                            switch(call.method) {
                                case "getTasks":
                                    new TaskTask.GetTaskTask(parseLong(id).orElse(-1L), result).execute(1);
                                    break;
                                case "tickTask":
                                    new TaskTask.TickTaskTask(finished.equals(true), taskName, parseLong(taskId).orElse(-1L), result).execute(parseInt(id).orElse(-1));
                                    break;
                                case "createTask":
                                    new TaskTask.CreateTaskTask(taskName, parseLong(id).orElse(-1L), result).execute(1);
                                    break;
                                default:
                                    ;
                            }
                        }
                );
    }
}
