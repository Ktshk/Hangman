package com.example.hangman;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

public class LetterAdapter extends BaseAdapter {

    private String[] letters = new String[32];
    private LayoutInflater letterInflatter;

    public LetterAdapter(Context c) {
        int i = 0;
        for (char ch = 'А'; ch <= 'Я'; ch++) {
            if (ch == 'Ё') {
                continue;
            }
            letters[i++] = "" + (char) ch;
        }
        letterInflatter = LayoutInflater.from(c);

    }

    @Override
    public int getCount() {
        return letters.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Button letterBtn;
        if (view == null) {
            letterBtn = (Button) letterInflatter.inflate(R.layout.letter, viewGroup, false);
        } else {
            letterBtn = (Button) view;
        }
        letterBtn.setText(letters[i]);
        return letterBtn;
    }
}
