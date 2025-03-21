package com.deltacodex.EventPulse.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.deltacodex.EventPulse.R;

public class GenreSpinnerAdapter extends BaseAdapter {
    private Context context;
    private String[] genres;
    private int[] icons; // Store corresponding icons

    public GenreSpinnerAdapter(Context context, String[] genres, int[] icons) {
        this.context = context;
        this.genres = genres;
        this.icons = icons;
    }

    @Override
    public int getCount() {
        return genres.length;
    }

    @Override
    public Object getItem(int position) {
        return genres[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false);
        }

        ImageView spinnerIcon = convertView.findViewById(R.id.spinnerIcon);
        TextView spinnerText = convertView.findViewById(R.id.spinnerText);

        spinnerIcon.setImageResource(icons[position]);
        spinnerText.setText(genres[position] +" Movie Collection");

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
