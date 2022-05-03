package com.example.hangman;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MenuActivity extends AppCompatActivity {

    private TextView helloText;
    private TextView ratingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        helloText = findViewById(R.id.helloText);
        ratingText = findViewById(R.id.ratingText);

        Button startBtn = findViewById(R.id.startButton);
        startBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), GameActivity.class);
            startActivity(intent);
        });

        Button scoresBtn = findViewById(R.id.scoresButton);
        scoresBtn.setOnClickListener(view -> {
            openScores();
        });

        Button exitButton = findViewById(R.id.exitButton);
        exitButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            MenuActivity.this.finish();
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("MenuActivity: started");

        SharedPreferences sharedPreferences = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        int rating = sharedPreferences.getInt("RATING", 0);
        String name = sharedPreferences.getString("NAME", "");

        helloText.setText("Привет, " + name + "!");
        ratingText.setText("Ваш рейтинг: " + rating);
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("MenuActivity: resumed");
    }

    private void openScores() {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        executorService.execute(() -> {
            try {
                List<User> allUsers = new Connection().getAllUsers();
                saveScores(allUsers);

                Intent intent = new Intent(getApplicationContext(), ScoresActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void saveScores(List<User> users) {
        PrintStream fos = null;
        try {
            fos = new PrintStream(new BufferedOutputStream(openFileOutput("records.txt", MODE_PRIVATE)));
            for (int i = 0; i < users.size(); i++) {
                fos.println(users.get(i).getName() + " " + users.get(i).getRating());
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