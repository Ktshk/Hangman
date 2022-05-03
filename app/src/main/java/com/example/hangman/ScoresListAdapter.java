package com.example.hangman;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ScoresListAdapter extends BaseAdapter {

    private  Context context;
    private LayoutInflater inflater;
    private List<User> items;

    public ScoresListAdapter(Context context, List<User> items) {
        this.context = context;
        this.items = items;
        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int location) {
        return items.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View scoreView, ViewGroup parent) {
        ViewHolder holder;
        if (scoreView == null) {
            scoreView = inflater.inflate(R.layout.score_row, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) scoreView.findViewById(R.id.name);
            holder.score = (TextView) scoreView.findViewById(R.id.score);

            scoreView.setTag(holder);
        } else {
            holder = (ViewHolder) scoreView.getTag();
        }

        final User m = items.get(position);
        holder.name.setText(m.getName());
        holder.score.setText("" + m.getRating());

        return scoreView;
    }

    static class ViewHolder {
        TextView name;
        TextView score;
    }

}