package com.example.playerlink.RAWG_api;

import android.os.StrictMode;

import com.example.playerlink.models.Game;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class DataService {

    private static ArrayList<Game> allGames = new ArrayList<>();
    private static ArrayList<String> allGamesNames = new ArrayList<>();

    public static ArrayList<Game> getAllGames() {
        return allGames;
    }

    public static ArrayList<String> getAllGamesNames() {
        return allGamesNames;
    }

    public static void loadAllGames() {
        String sURL = "https://api.rawg.io/api/games" + "?key=" + "e71ebd8ee128446aba497a75c23e036a";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            URL url = new URL(sURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader(connection.getInputStream()));
            JsonObject jsonObject = root.getAsJsonObject();

            // Get the "results" array from the JsonObject
            JsonArray resultsArray = jsonObject.getAsJsonArray("results");

            StringBuilder data = new StringBuilder();

            // Iterate over each element in the "results" array
            for (JsonElement element : resultsArray) {
                JsonObject obj = element.getAsJsonObject();
                String name = obj.get("name").getAsString();
                double rating = obj.get("rating").getAsDouble();
                String image = obj.get("background_image").getAsString();

                Game game = new Game(name, rating, image);
                allGames.add(game);
                allGamesNames.add(name);
            }

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
