package com.example.weather;

import static com.example.weather.WeatherResponse.getWeatherIcon;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.PopupMenu;



public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "theme_prefs";
    private static final String THEME_KEY = "is_dark_theme";

    private static final String API_KEY = "115acd090382f3ffb6d93ddbf8cdba10";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    private static final int LOCATION_REQUEST_CODE = 1000;

    private TextView cityNameText;
    private TextView temperatureText;
    private TextView descriptionText;
    private TextView windText;
    private TextView feelsLikeText;
    private ImageView weatherIcon;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setQueryHint("Введите город...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                boolean isFarenheit = getSharedPreferences("WeatherAppPrefs", MODE_PRIVATE)
                        .getBoolean("temperature_unit", false);
                fetchWeatherForCity(query, isFarenheit);
                saveCityToPreferences(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_search) {
            showCitySelectionMenu();
            return true;
        } else if (itemId == R.id.action_settings) {
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showCitySelectionMenu() {
        PopupMenu popupMenu = new PopupMenu(this, findViewById(R.id.action_search));
        popupMenu.getMenu().add("Москва");
        popupMenu.getMenu().add("Санкт-Петербург");
        popupMenu.getMenu().add("Новосибирск");
        popupMenu.getMenu().add("Екатеринбург");
        popupMenu.getMenu().add("Казань");
        popupMenu.getMenu().add("Нижний Новгород");
        popupMenu.getMenu().add("Красноярск");
        popupMenu.getMenu().add("Челябинск");
        popupMenu.getMenu().add("Самара");
        popupMenu.getMenu().add("Уфа");
        popupMenu.getMenu().add("Ростов-на-Дону");
        popupMenu.getMenu().add("Краснодар");
        popupMenu.getMenu().add("Омск");
        popupMenu.getMenu().add("Воронеж");
        popupMenu.getMenu().add("Пермь");
        popupMenu.getMenu().add("Волгоград");
        popupMenu.getMenu().add("Саратов");
        popupMenu.getMenu().add("Тюмень");

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            String selectedCity = menuItem.getTitle().toString();
            SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            boolean isFarenheit = sharedPreferences.getBoolean("temperature_unit", false);
            saveCityToPreferences(selectedCity);
            fetchWeatherForCity(selectedCity, isFarenheit);
            return true;
        });

        popupMenu.show();
    }

    private void saveCityToPreferences(String city) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("city", city);
        editor.apply();
    }

    private String getCityFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return sharedPreferences.getString("city", "Москва");
    }

    private void fetchWeatherForCity(String city, boolean isFarenheit) {
        String units = isFarenheit ? "imperial" : "metric";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApi weatherApi = retrofit.create(WeatherApi.class);

        Call<WeatherResponse> call = weatherApi.getWeatherByCity(city, units, API_KEY);
        call.enqueue(new Callback<WeatherResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weather = response.body();
                    cityNameText.setText("Город: " + city);
                    temperatureText.setText("Температура: " +
                            weather.getMain().getTemp() +
                            (isFarenheit ? "°F" : "°C"));
                    descriptionText.setText("Погода: " +
                            weather.getWeather()[0].getDescription());
                    windText.setText("Ветер: " +
                            weather.getWind().getSpeed() + " м/с");
                    feelsLikeText.setText("Ощущается как: " +
                            weather.getMain().getFeelsLike() +
                            (isFarenheit ? "°F" : "°C"));
                    weatherIcon.setImageResource(getWeatherIcon(weather.getWeather()[0].getDescription()));

                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                cityNameText.setText("Город: " + city);
                temperatureText.setText("Температура: Ошибка");
                descriptionText.setText("Погода: Ошибка");
                windText.setText("Ветер: Ошибка");
                feelsLikeText.setText("Ощущается как: Ошибка");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weatherIcon = findViewById(R.id.weatherIcon);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
        } else {

        }

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (!sharedPreferences.contains(THEME_KEY)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        else {
            boolean isDarkTheme = sharedPreferences.getBoolean(THEME_KEY, false);
            if (isDarkTheme) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cityNameText = findViewById(R.id.cityNameText);
        temperatureText = findViewById(R.id.temperatureText);
        descriptionText = findViewById(R.id.descriptionText);
        windText = findViewById(R.id.windText);
        feelsLikeText = findViewById(R.id.feelsLikeText);

        boolean isFarenheit = sharedPreferences.getBoolean("temperature_unit", false);
        String savedCity = getCityFromPreferences();
        fetchWeatherForCity(savedCity, isFarenheit);

    }
}