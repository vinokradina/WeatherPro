package com.example.weather;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class WeatherResponse {
    private Main main;
    private Wind wind;
    private Weather[] weather;

    public Main getMain() {
        return main;
    }
    public Wind getWind() {
        return wind;
    }
    public Weather[] getWeather() {
        return weather;
    }

    public class Main {
        private double temp;
        private double feels_like;

        public double getTemp() {
            return temp;
        }
        public double getFeelsLike() {
            return feels_like;
        }
    }

    public class Wind {
        private double speed;

        public double getSpeed() {
            return speed;
        }
    }

    public class Weather {
        private String main;
        private String description;

        public String getMain() {
            return main;
        }
        public String getDescription() {
            return description;
        }

    }
    public static int getWeatherIcon(String weatherCondition) {
        if (weatherCondition == null) {
            return R.drawable.weather;
        }

        String condition = weatherCondition.toLowerCase();

        if (Pattern.compile(".*clear.*").matcher(condition).find()) {
            return R.drawable.sun;
        } else if (Pattern.compile(".*cloud.*").matcher(condition).find()) {
            return R.drawable.cloud;
        } else if (Pattern.compile(".*rain.*").matcher(condition).find()) {
            return R.drawable.rain;
        } else if (Pattern.compile(".*snow.*").matcher(condition).find()) {
            return R.drawable.snow;
        } else {
            return R.drawable.weather;
        }
    }

}
