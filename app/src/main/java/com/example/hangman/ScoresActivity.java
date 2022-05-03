package com.example.hangman;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScoresActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        List<User> users = readUsers();

        ListView listView = findViewById(R.id.scrores);
        ScoresListAdapter adapter = new ScoresListAdapter(this, users);
        listView.setAdapter(adapter);
    }

    private List<User> readUsers() {
        List<User> users = new ArrayList<>();

        BufferedReader fin = null;
        try {
            fin = new BufferedReader(new InputStreamReader(openFileInput("records.txt")));
            String s = fin.readLine();
            while (s != null) {
                String[] split = s.split(" ");
                System.out.println(Arrays.toString(split));
                users.add(new User(split[0], Integer.parseInt(split[1])));
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

        return users;
    }
}