package com.example.lifesync;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AddActivityItemSpinnerAdapter extends BaseAdapter {
    private final Context context;
    private final List<SpinnerItem> spinnerItems;

    public AddActivityItemSpinnerAdapter(Context context, List<SpinnerItem> spinnerItems) {
        this.context = context;
        this.spinnerItems = spinnerItems;
    }

    @Override
    public int getCount() {
        return spinnerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return spinnerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.add_activity_item_spinner, parent, false);
        }

        ImageView spinnerImage = convertView.findViewById(R.id.spinner_image);
        SpinnerItem item = spinnerItems.get(position);
        spinnerImage.setImageResource(item.getImageResource());

        return convertView;
    }

    public static class SpinnerItem {
        private final int imageResource;
        private final String text;

        public SpinnerItem(int imageResource, String text) {
            this.imageResource = imageResource;
            this.text = text;
        }

        public int getImageResource() {
            return imageResource;
        }

        public String getText() {
            return text;
        }
    }
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.spinner_item_dropdown, parent, false);
        }

        ImageView spinnerImage = convertView.findViewById(R.id.spinner_image);
        TextView spinnerText = convertView.findViewById(R.id.spinner_text);

        SpinnerItem item = spinnerItems.get(position);
        spinnerImage.setImageResource(item.getImageResource());
        spinnerText.setText(item.getText());

        return convertView;
    }
}
