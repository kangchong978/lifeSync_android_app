package com.example.lifesync;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ViewConstructor")
public class DailyActivityView extends LinearLayout {

    private int id;
    private ImageView activityImageView;
    private TextView valueTextView;
    private TextView unitTextView;


    private final List<Integer> lineColors = new ArrayList<Integer>() {
        {
            add(Color.parseColor("#582f0e"));
            add(Color.parseColor("#7f4f24"));
            add(Color.parseColor("#936639"));
            add(Color.parseColor("#a68a64"));
            add(Color.parseColor("#b6ad90"));
            add(Color.parseColor("#c2c5aa"));
            add(Color.parseColor("#a4ac86"));
            add(Color.parseColor("#656d4a"));
            add(Color.parseColor("#414833"));
            add(Color.parseColor("#333d29"));

        }
    };

    public DailyActivityView(Context context, String valueText, ActivityClass activityClass, int id) {
        super(context);
        this.id = id;

        this.setOrientation(VERTICAL);
        this.setBackgroundResource(R.drawable.activity_item);
        this.setPadding(40, 40, 20, 20);
        this.setHorizontalGravity(Gravity.START);

        activityImageView = new ImageView(context);
        activityImageView.setScaleType(ImageView.ScaleType.FIT_START);
        activityImageView.setPadding(-5, 0, 0, 0);
        @DrawableRes int imageResource = R.drawable.round_directions_run_24;


        switch (activityClass) {
            case Steps:
                break;
            case BMI:
                imageResource = R.drawable.round_speed_24;
                break;
            case Distance:
                imageResource = R.drawable.round_directions_24;
                break;
            case CaloriesBurned:
                imageResource = R.drawable.round_local_fire_department_24;
                break;
        }
        activityImageView.setImageResource(imageResource);

        valueTextView = new TextView(context);
        valueTextView.setText(valueText);
        valueTextView.setWidth(220);
        valueTextView.setTextSize(20);
        Typeface typeface = Typeface.create("sans-serif-black", Typeface.NORMAL);
        valueTextView.setTypeface(typeface);
        valueTextView.setSingleLine(true);
        valueTextView.setEllipsize(TextUtils.TruncateAt.END);
        unitTextView = new TextView(context);
        unitTextView.setTextSize(15);
        Typeface typeface2 = Typeface.create("sans-serif-medium", Typeface.NORMAL);
        unitTextView.setTypeface(typeface2);
        String unitText = "-";

        switch (activityClass) {
            case Steps:
                unitText = "Steps";
                break;
            case BMI:
                unitText = "kg/m2";
                break;
            case Distance:
                unitText = "Meters";
                break;
            case CaloriesBurned:
                unitText = "Kcal";
                break;
        }

        unitTextView.setText(unitText);
        unitTextView.setSingleLine(true);
        unitTextView.setEllipsize(TextUtils.TruncateAt.END);

        this.addView(activityImageView);
        this.addView(valueTextView);
        this.addView(unitTextView);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void updateValue(String displayValue, boolean isDone, int index, int taskId, ActivityClass activityClass) {
        this.id = taskId;

        valueTextView.setText(displayValue);
        int color = lineColors.get(index);
        Drawable drawable = activityImageView.getDrawable();
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        valueTextView.setTextColor(color);
        unitTextView.setTextColor(color);
        activityImageView.setImageDrawable(drawable);

        if (isDone) {
            switch (index) {
                case 0:
                    this.setBackgroundResource(R.drawable.activity_item_done_1);
                    break;
                case 1:
                    this.setBackgroundResource(R.drawable.activity_item_done_2);
                    break;
                case 2:
                    this.setBackgroundResource(R.drawable.activity_item_done_3);
                    break;
                case 3:
                    this.setBackgroundResource(R.drawable.activity_item_done_4);
                    break;
                default:
                    this.setBackgroundResource(R.drawable.activity_item_done);
                    break;
            }

        } else {
            this.setBackgroundResource(R.drawable.activity_item);
        }
        @DrawableRes int imageResource = R.drawable.round_directions_run_24;
        switch (activityClass) {
            case Steps:
                break;
            case BMI:
                imageResource = R.drawable.round_speed_24;
                break;
            case Distance:
                imageResource = R.drawable.round_directions_24;
                break;
            case CaloriesBurned:
                imageResource = R.drawable.round_local_fire_department_24;
                break;
        }
        activityImageView.setImageResource(imageResource);
        String unitText = "-";
        switch (activityClass) {
            case Steps:
                unitText = "Steps";
                break;
            case BMI:
                unitText = "kg/m2";
                break;
            case Distance:
                unitText = "Meters";
                break;
            case CaloriesBurned:
                unitText = "Kcal";
                break;
        }
        unitTextView.setText(unitText);

    }

    @Override
    public int getId() {
        return id;
    }
}