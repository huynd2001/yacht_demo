package com.csds393.yacht_demo;

import androidx.annotation.NonNull;

import java.util.*;

import com.csds393.yacht.database.*;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity {
    private static final String CHANNEL = "calendar/weather";

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
            .setMethodCallHandler(
            (call, result) -> {
                switch(call.method) {
                    case "getWeather":
                        String start = call.argument("start");
                        String end = call.argument("end");

                        Map<String, String> returnValue = new HashMap<>();
                        returnValue.put("this", start);
                        returnValue.put("a", end);

                        result.success(returnValue);
                        break;
                    default:
                        ;
                }
            }
        );
    }
}
