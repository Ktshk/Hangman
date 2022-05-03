package com.example.hangman;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Connection {

    public Connection() {
    }

    public int login(CharSequence name, CharSequence pwd, List<String> words) throws Exception {
        HttpURLConnection httpConnection = null;
        try {
            URL url = new URL("https://hangman-137.herokuapp.com/");
            httpConnection = createHttpConnection(true, url);

            JSONObject requestJSON = createRequestJSON(name, "login");
            requestJSON.put("password", pwd);

            sendJSON(httpConnection, requestJSON);

            JSONObject responseJSON = receiveJSON(httpConnection);
            if (responseJSON.getString("response").equals("OK")) {
                words.clear();
                JSONArray wordsJSONArray = responseJSON.getJSONArray("words");
                for (int i = 0; i < wordsJSONArray.length(); i++) {
                    words.add(wordsJSONArray.getString(i));
                }
                return responseJSON.getInt("rating");
            } else {
                throw new Exception(responseJSON.getString("message"));
            }
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }

    }

    public int updateRating(CharSequence name, int ratingDelta) throws Exception {
        HttpURLConnection httpConnection = null;
        try {
            URL url = new URL("https://hangman-137.herokuapp.com/");
            httpConnection = createHttpConnection(true, url);

            JSONObject requestJSON = createRequestJSON(name, "update");
            requestJSON.put("rating", ratingDelta);

            sendJSON(httpConnection, requestJSON);

            JSONObject responseJSON = receiveJSON(httpConnection);
            if (responseJSON.getString("response").equals("OK")) {
                return responseJSON.getInt("rating");
            } else {
                throw new Exception(responseJSON.getString("message"));
            }
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }

    }

    public List<User> getAllUsers() throws Exception {
        HttpURLConnection httpConnection = null;
        try {
            URL url = new URL("https://hangman-137.herokuapp.com/records");
            httpConnection = createHttpConnection(false, url);

            JSONObject responseJSON = receiveJSON(httpConnection);

            List<User> users = new ArrayList<>();
            JSONArray wordsJSONArray = responseJSON.getJSONArray("records");
            for (int i = 0; i < wordsJSONArray.length(); i++) {
                users.add(new User(wordsJSONArray.getJSONObject(i).getString("name"),
                        wordsJSONArray.getJSONObject(i).getInt("rating")));
            }
            Collections.sort(users);

            return users;
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }

    }

    @NonNull
    private JSONObject receiveJSON(HttpURLConnection httpConnection) throws IOException, JSONException {
        BufferedReader in = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
        String s = in.readLine();
        StringBuilder response = new StringBuilder();
        while (s != null) {
            response.append(s);
            s = in.readLine();
        }
        System.out.println("Received: " + response);

        return new JSONObject(response.toString());
    }

    private void sendJSON(HttpURLConnection httpConnection, JSONObject requestJSON) throws IOException {
        byte[] bytesToSend = requestJSON.toString().getBytes("utf-8");
        System.out.println("Sending: " + requestJSON.toString());
        httpConnection.getOutputStream().write(bytesToSend);
    }

    @NonNull
    private JSONObject createRequestJSON(CharSequence name, String command) throws JSONException {
        JSONObject requestJSON = new JSONObject();
        requestJSON.put("command", command);
        requestJSON.put("name", name);
        return requestJSON;
    }

    private HttpURLConnection createHttpConnection(boolean post, URL url) throws IOException {
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.setRequestMethod(post ? "POST" : "GET");
        httpConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        httpConnection.setRequestProperty("Accept", "application/json");
        if (post) {
            httpConnection.setDoOutput(true);
        }

        return httpConnection;
    }
}
