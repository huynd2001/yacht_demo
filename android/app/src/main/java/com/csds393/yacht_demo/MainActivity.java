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
    }
}
