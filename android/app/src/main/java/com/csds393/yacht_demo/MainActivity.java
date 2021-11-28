package com.csds393.yacht_demo;

import android.os.AsyncTask;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.csds393.yacht.weather.DayWeather;
import com.csds393.yacht.weather.HalfDayWeather;
import com.csds393.yacht.weather.Weather;
import com.csds393.yacht.database.DB;
import java.util.Random;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity {
    private static final String CHANNEL = "calendar/weather";

    private class CallAPITask extends AsyncTask<Pair<Float, Float>, Integer, List<DayWeather>> {

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
                case CLEAR:
                    weatherString = "partlycloudyday";
                    break;
                case RAINY:
                    weatherString = "rainyday";
                    break;
                case SUNNY:
                    weatherString = "clearday";
                    break;
                case CLOUDY:
                    weatherString = "foggyday";
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

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        DB.initializeDB(this.getApplicationContext());
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
            .setMethodCallHandler(
            (call, result) -> {
                switch(call.method) {
                    case "getWeather":
                        String start = call.argument("start");
                        String end = call.argument("end");

                         new CallAPITask(start, end, result).execute(new Pair<>(41.499321f, -81.694359f));

                        break;
                    case "getEvent":
                        break;
                    default:
                        ;
                }
            }
        );
    }
}
