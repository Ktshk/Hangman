package com.example.hangman;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameActivity extends AppCompatActivity {

    private String word = "конфета";
    private boolean[] guessed = new boolean[word.length()];
    private ImageView hangmanImage;
    private int mistakes = 0;
    private int rating;
    private List<String> words;
    private TextView[] charViews;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        hangmanImage = findViewById(R.id.hangmanImage);

        words = readWords();
        startGame();
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sharedPreferences = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        rating = sharedPreferences.getInt("RATING", 0);
        name = sharedPreferences.getString("NAME", "");
    }

    private List<String> readWords() {
        List<String> words = new ArrayList<>();

        BufferedReader fin = null;
        try {
            fin = new BufferedReader(new InputStreamReader(openFileInput("words.txt")));
            String s = fin.readLine();
            while (s != null) {
                words.add(s);
                s = fin.readLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (fin != null)
                    fin.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return words;
    }

    public void letterPressed(View view) {
        char letter = ((Button) view).getText().toString().toLowerCase().charAt(0);
        boolean correct = false;
        boolean won = true;
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == letter) {
                correct = true;
                guessed[i] = true;
                charViews[i].setTextColor(getResources().getColor(R.color.black));
            }
            won = won && guessed[i];
        }
        view.setEnabled(false);
        ((Button) view).setTextColor(getResources().getColor(R.color.red));

        if (!correct) {
            mistakes++;

            switch (mistakes) {
                case 1:
                    hangmanImage.setImageResource(R.drawable.hangman_1);
                    break;
                case 2:
                    hangmanImage.setImageResource(R.drawable.hangman_2);
                    break;
                case 3:
                    hangmanImage.setImageResource(R.drawable.hangman_3);
                    break;
                case 4:
                    hangmanImage.setImageResource(R.drawable.hangman_4);
                    break;
                case 5:
                    hangmanImage.setImageResource(R.drawable.hangman_5);
                    break;
                case 6:
                    hangmanImage.setImageResource(R.drawable.hangman_6);
                    break;
                case 7:
                    hangmanImage.setImageResource(R.drawable.hangman_7);
                    break;
            }

            if (mistakes == 7) {
                rating -= word.length();
                sendRatingUpdate(-word.length());
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                        .setTitle("УПС...")
                        .setMessage("Вы проиграли!\nВаш новый рейтинг: " + rating)
                        .setPositiveButton("Еще раз", (dialog, id) -> startGame())
                        .setNegativeButton("Закончить", (dialog, id) -> finish());
                dialogBuilder.show();
            }

        } else if (won) {
            rating += word.length();
            sendRatingUpdate(word.length());
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                                .setTitle("УРА!!")
                                .setMessage("Вы отгадали!\nВаш новый рейтинг: " + rating)
                                .setPositiveButton("Еще раз", (dialog, id) -> startGame())
                                .setNegativeButton("Закончить", (dialog, id) -> finish());
            dialogBuilder.show();
        }
    }

    private void sendRatingUpdate(int ratingDelta) {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        executorService.execute(() -> {
            try {
                rating = new Connection().updateRating(name, ratingDelta);

                SharedPreferences sharedPreferences = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("RATING", rating);
                editor.apply();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void startGame() {
        GridView lettersView = (GridView) findViewById(R.id.lettersView);
        LetterAdapter letterAdapter = new LetterAdapter(this);
        lettersView.setAdapter(letterAdapter);

        if (words.size() > 0) {
            int ind = new Random().nextInt(words.size());
            word = words.get(ind);
        }
        mistakes = 0;
        guessed = new boolean[word.length()];

        hangmanImage.setImageResource(R.drawable.hangman_0);

        LinearLayout wordLayout = (LinearLayout) findViewById(R.id.wordLayout);
        wordLayout.removeAllViews();

        charViews = new TextView[word.length()];

        for (int c = 0; c < word.length(); c++) {
            charViews[c] = new TextView(this);
            charViews[c].setText("" + word.charAt(c));
            charViews[c].setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

            charViews[c].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            charViews[c].setGravity(Gravity.CENTER);
            charViews[c].setBackgroundResource(R.drawable.underline);
            charViews[c].setTextColor(getResources().getColor(R.color.transparent));

            //add to layout
            wordLayout.addView(charViews[c]);
        }
    }
}