package com.csds393.yacht_demo;

import android.os.AsyncTask;
import android.util.Pair;

import com.csds393.yacht.weather.DayWeather;
import com.csds393.yacht.weather.HalfDayWeather;
import com.csds393.yacht.weather.Weather;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.MethodChannel;

public class WeatherTask {

    static class GetWeatherTask extends AsyncTask<Pair<Float, Float>, Integer, List<DayWeather>> {

        LocalDate startDate, endDate;
        MethodChannel.Result result;
        String start, end;

        public GetWeatherTask(String start, String end, MethodChannel.Result result){
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

}
