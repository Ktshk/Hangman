package com.example.hangman;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.loginButton);
        EditText nameField = findViewById(R.id.nameField);
        EditText pwdField = findViewById(R.id.pwdField);
        errorText = findViewById(R.id.errorText);

        SharedPreferences sharedPreferences = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        if (sharedPreferences != null && sharedPreferences.contains("NAME")) {
            nameField.setText(sharedPreferences.getString("NAME", ""));
        }

        pwdField.setText("Qwerty123");
        errorText.setText("");

        loginButton.setOnClickListener(view -> {
            errorText.setText("");
            login(nameField.getText(), pwdField.getText());
        });

    }

    private void login(CharSequence name, CharSequence pwd) {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        executorService.execute(() -> {
            try {
                ArrayList<String> words = new ArrayList<>();
                int rating = new Connection().login(name, pwd, words);
                saveWords(words);

                loginOK(name, rating);
            } catch (Exception e) {
                loginFailed(e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void loginFailed(String message) {
        Handler handler = HandlerCompat.createAsync(Looper.getMainLooper());
        handler.post(() -> {
            errorText.setText(message);
        });
    }

    private void loginOK(CharSequence name, int rating) {
        SharedPreferences sharedPreferences = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("NAME", name.toString());
        editor.putInt("RATING", rating);
        editor.apply();

        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
        startActivity(intent);
    }

    public void saveWords(ArrayList<String> words) {
        PrintStream fos = null;
        try {
            fos = new PrintStream(new BufferedOutputStream(openFileOutput("words.txt", MODE_PRIVATE)));
            for (int i = 0; i < words.size(); i++) {
                fos.println(words.get(i));
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
    }


}