package com.example.lifesync;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class LineGraphView extends View {
    private Bitmap cachedBitmap;
    private List<List<Double>> data;
    private final Paint paint;
    private final Paint pointPaint;
    private List<List<String>> names; // List of names corresponding to each data point
    // Define a list of predefined line colors
    final private List<Integer> lineColors = new ArrayList<Integer>() {
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


    public LineGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setStrokeWidth(1); // Adjust the line thickness
        paint.setStrokeJoin(Paint.Join.ROUND); // Set the join style to round
        paint.setStrokeCap(Paint.Cap.ROUND);
        pointPaint = new Paint();
        pointPaint.setColor(Color.parseColor("#646464")); // Point color
        pointPaint.setStyle(Paint.Style.FILL); // Filled circle
    }


    public void setData(List<List<DataPoint>> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return; // No data to display
        }

        // Extract the data and names for each line
        this.data = new ArrayList<>();
        this.names = new ArrayList<>();

        for (List<DataPoint> dataPoints : dataList) {
            if (dataPoints.isEmpty()) {
                continue; // Skip empty data sets
            }

            // Get the data and names for this line
            List<Double> lineData = new ArrayList<>();
            List<String> lineNames = new ArrayList<>();

            for (DataPoint dataPoint : dataPoints) {
                lineData.add(dataPoint.getValue());
                lineNames.add(dataPoint.getName());
            }

            this.data.add(lineData);
            this.names.add(lineNames);
        }

        invalidate(); // Trigger a redraw
    }

    public void resetData() {
        if (data != null) {
            data.clear();
        }
        if (names != null) {
            names.clear();
        }
        // Clear the cached bitmap
        if (cachedBitmap != null) {
            cachedBitmap.recycle();
            cachedBitmap = null;
        }
        invalidate(); // Trigger a redraw
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Check if there are no data points or not enough data points
        if (data == null || data.isEmpty() || data.get(0).size() < 2) {
            // Draw the message in the middle of the view
            String message = "at least 2 days of record to display";
            Paint messagePaint = new Paint();
            messagePaint.setColor(ContextCompat.getColor(this.getContext(), R.color.greyEB));
            messagePaint.setTextSize(36); // Set the text size

            float textWidth = messagePaint.measureText(message);
            float x = (getWidth() - textWidth) / 2; // Center the text horizontally

            // Center the text vertically
            Paint.FontMetrics fontMetrics = messagePaint.getFontMetrics();
            float textHeight = fontMetrics.bottom - fontMetrics.top;
            float y = (getHeight() - textHeight) / 2 + textHeight - fontMetrics.bottom;

            canvas.drawText(message, x, y, messagePaint);
            return;
        }


        // Check if the cached bitmap is null or needs to be recreated
        if (cachedBitmap == null || cachedBitmap.getWidth() != getWidth() || cachedBitmap.getHeight() != getHeight()) {
            // Create a new bitmap with the same dimensions as the view
            cachedBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);

            // Create a canvas for the new bitmap
            Canvas cachedCanvas = new Canvas(cachedBitmap);

            // Call your existing drawing code on the cached canvas
            drawGraph(cachedCanvas);
        }
        // Draw the cached bitmap on the view's canvas
        canvas.drawBitmap(cachedBitmap, 0, 0, null);

    }


    private void drawGraph(Canvas canvas) {
        if (data == null || data.isEmpty() || names == null || names.isEmpty()) {
            return; // No data to draw
        }

        // Calculate the total width needed for the graph
        float width = (data.get(0).size() - 1) * (dpToPx(40) + dpToPx(20)); // Adjusted spacing

        // Set the width of the LineGraphView to allow horizontal scrolling
        setMinimumWidth((int) width);

        float height = getHeight();
        float stepX = (getWidth() - dpToPx(40)) / (data.get(0).size() - 1); // Adjusted spacing
        float stepY = (float) ((height - dpToPx(40)) / getMaxValue()); // Adjust for 20dp margin on each side

        // Adjust the canvas based on the horizontal scroll position
        float scrollX = 0;
        canvas.translate(-scrollX, 0);

        Paint textPaint = new Paint(pointPaint); // Create a copy of pointPaint
        textPaint.setTextSize(24); // Set the text size to 24

        for (int lineIndex = 0; lineIndex < data.size(); lineIndex++) {
            List<Double> lineData = data.get(lineIndex);
            List<String> lineNames = names.get(lineIndex);

            Paint linePaint = new Paint(paint); // Create a copy of the paint for each line
            linePaint.setColor(getLineColor(lineIndex)); // Set a different color for each line

            float lastX = dpToPx(20); // Start from 20dp on the left
            float lastY = (float) (height - dpToPx(20) - (lineData.get(0) * stepY)); // Start from 20dp on the bottom

            for (int i = 0; i < lineData.size(); i++) {
                float x2 = dpToPx(20) + i * stepX; // Adjusted spacing
                float y2 = (float) (height - dpToPx(20) - (lineData.get(i) * stepY)); // Start from 20dp on the bottom

                linePaint.setStrokeWidth(2);
                // Draw a vertical line from the data point to the name label
                canvas.drawLine(x2, y2, x2, height - dpToPx(25), linePaint);

                linePaint.setStrokeWidth(1);

                // Draw a line segment between the last point and the current point
                canvas.drawLine(lastX, lastY, x2, y2, linePaint);

                lastX = x2;
                lastY = y2;
            }

            // Draw the circle points and text labels after drawing the lines
            for (int i = 0; i < lineData.size(); i++) {
                float x2 = dpToPx(20) + i * stepX; // Adjusted spacing
                float y2 = (float) (height - dpToPx(20) - (lineData.get(i) * stepY));

                pointPaint.setColor(getLineColor(lineIndex));
                // Draw a circle point at the current data point with a smaller radius (e.g., 10)
                canvas.drawCircle(x2, y2, 10, pointPaint);

                textPaint.setColor(getLineColor(lineIndex));
                // Draw text labels next to each point with a font size of 24
                String label = String.valueOf(lineData.get(i));
                float textX = x2 - dpToPx(8); // Adjust the X position of the text
                float textY = y2 - dpToPx(10); // Adjust the Y position of the text
                canvas.drawText(label, textX, textY, textPaint);

                // Draw the name label below each point
                String name = lineNames.get(i);
                float nameX = x2 - textPaint.measureText(name) / 2; // Center the name label
                float nameY = height - dpToPx(10); // Place the name label 10dp above the bottom
                canvas.drawText(name, nameX, nameY, textPaint);
            }
        }
        canvas.translate(scrollX, 0);
    }

    // Add a method to convert dp to pixels
    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }

    private Double getMaxValue() {
        Double max = Double.MIN_VALUE;
        for (List<Double> lineData : data) {
            for (double value : lineData) {
                if (value > max) {
                    max = value;
                }
            }
        }
        return max;
    }// Define a method to get a different color for each line (you can customize this)

    // Define a method to get a different color for each line
    private int getLineColor(int lineIndex) {
        if (lineIndex < lineColors.size()) {
            return lineColors.get(lineIndex);
        } else {
            // Handle the case where there are more lines than predefined colors
            return lineColors.get(lineIndex % lineColors.size());
        }
    }
}
