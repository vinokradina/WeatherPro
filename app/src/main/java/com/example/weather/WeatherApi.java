package com.example.weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {
    @GET("weather")
    Call<WeatherResponse> getWeatherByCity(
            @Query("q") String city,
            @Query("units") String units,
            @Query("appid") String apiKey
    );
}
