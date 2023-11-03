package com.example.lifesync;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.lifesync.databinding.FragmentFirstBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class FirstFragment extends Fragment implements AddActivityModalFragment.OnAddActivityListener {

    private FragmentFirstBinding binding;
    private static Context context;
    static String[] viewActivityOptions = {"7 days", "30 days", "365 days", "All"};
    static String greeting = "Welcome";
    static String username = "Sophia Muller";

    ActivityTask[] activeActivityList = {};
    ActivityTask[] inProgressActivityList = {};

    private LineGraphView graphView;

    private TextView progressPercentage;
    private HorizontalScrollView horizontalScrollView; // Replace with your view ID

    int timerValue = 0;

    private int todayDayOfWeek;

    private final List<Integer> progressIndicatorColors = new ArrayList<Integer>() {
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
    List<com.example.lifesync.DailyActivityView> dailyActivityViews = new ArrayList<>();
    DBManager dbManager;

    Timer timer;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            todayDayOfWeek = LocalDate.now().getDayOfWeek().getValue();
        }
        dbManager = new DBManager(getContext());
        dbManager.open();
//        dbManager.insertActivityTask(3, "steps", 100, true);
        activeActivityList = dbManager.fetchActivityTasks(todayDayOfWeek).toArray(new ActivityTask[0]);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = view.getContext();

        progressPercentage = (TextView) view.findViewById(R.id.progressPercentage);
        graphView = view.findViewById(R.id.graphView);
        horizontalScrollView = view.findViewById(R.id.dailyActivityHorizontalScrollView1);


//        activeActivityList = getDummyActivityTasks().toArray(new ActivityTask[0]);

        // My activity Ui Status Update
        // Spinner items assign
        Spinner spinner = (Spinner) view.findViewById(R.id.activity_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.activity_spinner_item, R.id.optionName, viewActivityOptions);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //                Log.d("Spinner", String.format("%d", id));
                setMyActivityGraphView(((int) id));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        // Greetings Ui Status Update
        // Greetings words
        TextView greetingTextView = (TextView) view.findViewById(R.id.greetingTextView);
        greetingTextView.setText(greeting);
        // Greetings user name
        TextView greetingUsernameTextView = (TextView) view.findViewById(R.id.greetingUsernameTextView);
        greetingUsernameTextView.setText(username);

        // Create new activity button initialize
        ImageButton createActivityButton = (ImageButton) view.findViewById(R.id.createActivityImageButton);
        createActivityButton.setOnClickListener(v -> showBottomSheetDialog());

        // Daily tasks initialize
        LinearLayout dailyActivityLinearLayout = (LinearLayout) view.findViewById(R.id.dailyActivityLinearLayout);
        CircularProgressIndicator circularProgressIndicatorSmall = (CircularProgressIndicator) view.findViewById(R.id.progress_circular_small);
        CircularProgressIndicator circularProgressIndicatorMiddle = (CircularProgressIndicator) view.findViewById(R.id.progress_circular_middle);
        CircularProgressIndicator circularProgressIndicatorBig = (CircularProgressIndicator) view.findViewById(R.id.progress_circular_big);
        // Assuming you have an array to store references to the DailyActivityView objects


        timer = new Timer();

        inProgressActivityList = activeActivityList;

//        dailyActivityViews = new DailyActivityView[inProgressActivityList.length];
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (timerValue >= 100) {
                    timerValue = 0;
                } else {
                    timerValue = 1;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {


                    for (int i = 0; i < activeActivityList.length; i++) {
                        if (activeActivityList[i].getValue() >= 0) {
                            activeActivityList[i].setPreviousValue(activeActivityList[i].getValue());

                        }

                        activeActivityList[i].setValue(activeActivityList[i].getValue() + timerValue * (i + new Random().nextInt((20 - i) + 1)));
                        if (activeActivityList[i].getValue() >= activeActivityList[i].getTargetValue() && !activeActivityList[i].isDone()) {
                            activeActivityList[i].setDone(true);
                        }
//                        Log.d("InProgressActivity", String.format("%d / %d  = %d", inProgressActivityList[i].getValue(), inProgressActivityList[i].getTargetValue(), Math.round((((double) inProgressActivityList[i].getValue() / inProgressActivityList[i].getTargetValue()) * 100))));
                    }

                    inProgressActivityList = Arrays.stream(activeActivityList).filter(e -> !e.isDone()).toArray(ActivityTask[]::new);
                    Arrays.sort(inProgressActivityList);
//                    Log.d("InProgressActivity", String.format("[0] %d / %d %b", inProgressActivityList[0].getValue(), inProgressActivityList[0].getTargetValue(),  inProgressActivityList[0].isDone()));

                    requireActivity().runOnUiThread(() -> {
                        if (inProgressActivityList.length > 0) {
                            Integer color = retrieveProgressColor(inProgressActivityList[0]);
                            Integer trackColor = getTrackColor(color);
                            circularProgressIndicatorSmall.setIndicatorColor(color);
                            circularProgressIndicatorSmall.setTrackColor(trackColor);
                            circularProgressIndicatorSmall.setTrackThickness(50);
                            int increasedValue = inProgressActivityList[0].getPreviousValue() - inProgressActivityList[0].getValue();
                            if (increasedValue < 1) {
                                increasedValue = 1;
                            }


                            int indicatorProgress = (int) Math.round((((double) inProgressActivityList[0].getValue() / inProgressActivityList[0].getTargetValue()) * 100));
                            ObjectAnimator.ofInt(circularProgressIndicatorSmall, "progress", indicatorProgress).setDuration(200 ^ increasedValue).start();
                        } else {
                            circularProgressIndicatorSmall.setProgress(0);
                            circularProgressIndicatorSmall.setTrackThickness(3);

                        }
                        if (inProgressActivityList.length > 1) {
                            Integer color = retrieveProgressColor(inProgressActivityList[1]);
                            Integer trackColor = getTrackColor(color);
                            circularProgressIndicatorMiddle.setIndicatorColor(color);
                            circularProgressIndicatorMiddle.setTrackColor(trackColor);
                            circularProgressIndicatorMiddle.setTrackThickness(50);
                            int increasedValue = inProgressActivityList[0].getPreviousValue() - inProgressActivityList[0].getValue();
                            if (increasedValue < 1) {
                                increasedValue = 1;
                            }
                            int indicatorProgress = (int) Math.round((((double) inProgressActivityList[1].getValue() / inProgressActivityList[1].getTargetValue()) * 100));
                            ObjectAnimator.ofInt(circularProgressIndicatorMiddle, "progress", indicatorProgress).setDuration(200 ^ increasedValue).start();

                        } else {
                            circularProgressIndicatorMiddle.setProgress(0);
                            circularProgressIndicatorMiddle.setTrackThickness(2);

                        }
                        if (inProgressActivityList.length > 2) {
                            Integer color = retrieveProgressColor(inProgressActivityList[2]);
                            Integer trackColor = getTrackColor(color);
                            circularProgressIndicatorBig.setIndicatorColor(color);
                            circularProgressIndicatorBig.setTrackColor(trackColor);
                            circularProgressIndicatorBig.setTrackThickness(50);
                            int increasedValue = inProgressActivityList[0].getPreviousValue() - inProgressActivityList[0].getValue();
                            if (increasedValue < 1) {
                                increasedValue = 1;
                            }
                            int indicatorProgress = (int) Math.round((((double) inProgressActivityList[2].getValue() / inProgressActivityList[2].getTargetValue()) * 100));
                            ObjectAnimator.ofInt(circularProgressIndicatorBig, "progress", indicatorProgress).setDuration(150 ^ increasedValue).start();
                        } else {
                            circularProgressIndicatorBig.setProgress(0);
                            circularProgressIndicatorBig.setTrackThickness(1);

                        }


                        if (dailyActivityViews.size() > activeActivityList.length) {
                            for (int i = activeActivityList.length; i < dailyActivityViews.size(); i++) {
                                dailyActivityLinearLayout.removeView(dailyActivityViews.get(i));
                                dailyActivityViews.remove(i);
                            }
                        }

                        for (int i = 0; i < activeActivityList.length; i++) {
                            ActivityTask task = activeActivityList[i];
                            int displayValue = task.getValue();
                            ActivityClass activityClass = task.getActivityClass();
                            int id = task.getId();
                            if (activeActivityList.length <= dailyActivityViews.size()) {
                                dailyActivityViews.get(i).updateValue(String.valueOf(displayValue), task.isDone(), i, id, activityClass);
                            } else {

                                dailyActivityViews.add(i, new DailyActivityView(context, String.valueOf(displayValue), activityClass, id));
                                dailyActivityLinearLayout.addView(dailyActivityViews.get(i));
                            }

                            if (task.isDone()) {
                                View viewToMove = dailyActivityViews.get(i);
                                dailyActivityLinearLayout.removeView(viewToMove); // Remove the view from its current position
                                dailyActivityLinearLayout.addView(viewToMove);    // Add it to the end
                            }
                        }

                        int childCount = dailyActivityLinearLayout.getChildCount();
                        if (childCount > 0) {
                            for (int i = 0; i < childCount; i++) {
                                View view1 = dailyActivityLinearLayout.getChildAt(i);

                                // Reset margins for all views
                                resetMarginsForView(view1);

                                // Apply margins based on position
                                int leftMargin, rightMargin;
                                if (i == 0) {
                                    // First view
                                    leftMargin = 50;
                                    rightMargin = 10;
                                } else if (i == childCount - 1) {
                                    // Last view
                                    leftMargin = 10;
                                    rightMargin = 60;
                                } else {
                                    // Other views
                                    leftMargin = rightMargin = 20;
                                }

                                applyMarginForView(view1, leftMargin, rightMargin);
                            }
                        }

                        @SuppressLint("DefaultLocale") String displayPercentageText = String.format("%d/%d", activeActivityList.length - inProgressActivityList.length, activeActivityList.length);
                        String cheers = "\uD83C\uDF8A\n";
                        if (activeActivityList.length - inProgressActivityList.length == activeActivityList.length) {
                            displayPercentageText = activeActivityList.length + cheers;
                        }

                        progressPercentage.setText(displayPercentageText);

                    });
                }


            }

        }, new Date(), 1000);

        DrawerLayout drawerLayout = view.findViewById(R.id.drawer_layout);

        ImageButton menu_button = view.findViewById(R.id.menu_button);
        menu_button.setOnClickListener(view12 -> drawerLayout.openDrawer(GravityCompat.START));

    }

    private void applyMarginForView(View view, int left, int right) {
        if (view != null) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
                marginLayoutParams.setMargins(left, 0, right, 0); // Set the margins as needed
                view.setLayoutParams(layoutParams);
            }
        }
    }

    private void resetMarginsForView(View view) {
        if (view != null) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
                marginLayoutParams.setMargins(0, 0, 0, 0); // Reset all margins to 0
                view.setLayoutParams(layoutParams);
            }
        }
    }

    private Integer retrieveProgressColor(ActivityTask activityTask) {
        int index = activityTask.getActivityClass().toInt();

        if (index >= 0) {
            return progressIndicatorColors.get(activityTask.getActivityClass().toInt());

        }

        return Color.parseColor("#646464");
    }

    private Integer getTrackColor(int color) {
        int alpha = (int) (255 * (float) 0.2);
        return ColorUtils.setAlphaComponent(color, alpha);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onActivityAdded(String activityName) {
        Log.d("BottomSheetDialogFragment", "Dismissed");
        activeActivityList = dbManager.fetchActivityTasks(todayDayOfWeek).toArray(new ActivityTask[0]);

    }


    private void showBottomSheetDialog() {
        BottomSheetDialogFragment addActivityFragment = new AddActivityModalFragment(todayDayOfWeek);
        ((AddActivityModalFragment) addActivityFragment).setOnAddActivityListener(this);
        addActivityFragment.show(getParentFragmentManager(), "BSDialogFragment");

    }

//    static public List<ActivityTask> getDummyActivityTasks() {
//        List<ActivityTask> activityTasks = new ArrayList<>();
//        try {
//            String result = readFile("dummyActivityTasks.json");
////                    Log.d("IO dummy", result);
//            JSONArray jsonArray = new JSONArray(result);
//
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject jsonObj = jsonArray.getJSONObject(i);
//                int activityClassCode = jsonObj.getInt("activityClassCode");
//                ActivityClass activityClass = ActivityClass.getClassFromInt(activityClassCode);
//                int value = jsonObj.getInt("value");
//                int targetValue = jsonObj.getInt("targetValue");
//                activityTasks.add(new ActivityTask( id, activityClass, value, targetValue));
//            }
//            Log.d("IO dummy", String.format("%d", activityTasks.size()));
//
//        } catch (Exception e) {
//            Log.d("IO dummy", e.toString());
//        }
//
//        return activityTasks;
//    }

    private List<HistoryActivity> getDummyActivityHistories() {
        List<HistoryActivity> activitiesHistories = new ArrayList<>();
        try {
            String result = readFile("dummyActivityHistories.json");
            JSONArray jsonArray = new JSONArray(result);

            for (int i = 0; i < jsonArray.length(); i++) {
                List<ActivityTask> activityTasks = new ArrayList<>();


                JSONObject jsonObj = jsonArray.getJSONObject(i);
                int timestamp = jsonObj.getInt("timestamp");
                JSONObject activitiesValues = jsonObj.getJSONObject("activitiesValues");
                Iterator<String> keys = activitiesValues.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    int value = activitiesValues.getInt(key);
                    ActivityClass activityClass = ActivityClass.getClassFromInt(key);
                    activityTasks.add(new ActivityTask(0, activityClass, value, 100));
                }

                activitiesHistories.add(new HistoryActivity(timestamp, activityTasks));
            }
            Log.d("IO dummy A", String.format("%d", activitiesHistories.size()));

        } catch (Exception e) {
            Log.d("IO dummy A", e.toString());
        }

        return activitiesHistories;
    }

    private static String readFile(String fileName) throws IOException {

        BufferedReader reader;
        reader = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName), StandardCharsets.UTF_8));

        StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line);
        }

        return content.toString();

    }

    private void setMyActivityGraphView(int spinnerOptionId) {
        try {

            List<HistoryActivity> activityHistoriesList = getDummyActivityHistories();

            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setFirstDayOfWeek(Calendar.SUNDAY);
            currentCalendar.setTime(new Date());
            currentCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            currentCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);


            switch (spinnerOptionId) {
                case 0:
                    // Filter for today and the 7 days before today
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.add(Calendar.DAY_OF_YEAR, 0); // Move to tomorrow
                    Date endOfDay = calendar.getTime(); // Set to end of today

                    calendar.add(Calendar.DAY_OF_YEAR, -7); // Move 7 days back
                    Date startOfWeek = calendar.getTime();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        activityHistoriesList = activityHistoriesList.stream().filter(a -> {
                            @SuppressLint("DefaultLocale") Date expiry = new Date(Long.parseLong(String.format("%d", a.getTimeStamp())) * 1000);
                            return expiry.after(startOfWeek) && expiry.before(endOfDay);
                        }).collect(Collectors.toList());
                    }
                    break;
                case 1:
                    // Filter for the current month (30 days)
                    Calendar currentMonthCalendar = Calendar.getInstance();
                    currentMonthCalendar.setTime(new Date());
                    currentMonthCalendar.set(Calendar.DAY_OF_MONTH, 0); // Set to the first day of the month
                    Date startOfMonth = currentMonthCalendar.getTime();
                    currentMonthCalendar.add(Calendar.DAY_OF_MONTH, 30); // Move 30 days ahead
                    Date endOfMonth = currentMonthCalendar.getTime();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        activityHistoriesList = activityHistoriesList.stream().filter(a -> {
                            @SuppressLint("DefaultLocale") Date expiry = new Date(Long.parseLong(String.format("%d", a.getTimeStamp())) * 1000);
                            return expiry.after(startOfMonth) && expiry.before(endOfMonth);
                        }).collect(Collectors.toList());
                    }
                    break;
                case 2:
                    // Filter for the current year (365 days)
                    Calendar currentYearCalendar = Calendar.getInstance();
                    currentYearCalendar.setTime(new Date());
                    currentYearCalendar.set(Calendar.DAY_OF_YEAR, 0); // Set to the first day of the year
                    Date startOfYear = currentYearCalendar.getTime();
                    currentYearCalendar.add(Calendar.DAY_OF_YEAR, 365); // Move 365 days ahead
                    Date endOfYear = currentYearCalendar.getTime();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        activityHistoriesList = activityHistoriesList.stream().filter(a -> {
                            @SuppressLint("DefaultLocale") Date expiry = new Date(Long.parseLong(String.format("%d", a.getTimeStamp())) * 1000);
                            return expiry.after(startOfYear) && expiry.before(endOfYear);
                        }).collect(Collectors.toList());
                    }
                    break;
                case 3:
                default:
                    break;
            }


            List<DataPoint> dataPointsDistance = new ArrayList<>();
            List<DataPoint> dataPointsBMI = new ArrayList<>();
            List<DataPoint> dataPointsSteps = new ArrayList<>();
            List<DataPoint> dataPointsCaloriesBurned = new ArrayList<>();

            float dpSpacesBetweenPoints = 40;


            for (int i = 0; i < activityHistoriesList.size(); i++) {
                HistoryActivity activity = activityHistoriesList.get(i);
                @SuppressLint("DefaultLocale") Date expiry = new Date(Long.parseLong(String.format("%d", activity.getTimeStamp())) * 1000);

                List<ActivityTask> activityTaskList = activity.getActivityTaskList();
                for (int j = 0; j < activityTaskList.size(); j++) {
                    String name = "";
                    Locale locale;
                    SimpleDateFormat sdf;
                    switch (spinnerOptionId) {
                        case 0:
                            locale = Locale.ENGLISH;
                            sdf = new SimpleDateFormat("EEEE", locale);
                            name = sdf.format(expiry);
                            dpSpacesBetweenPoints = 60;
                            break;
                        case 1:
                            locale = Locale.ENGLISH;
                            sdf = new SimpleDateFormat("dd", locale);
                            name = sdf.format(expiry);
                            dpSpacesBetweenPoints = 60;
                            break;
                        case 2:
                            locale = Locale.ENGLISH;
                            sdf = new SimpleDateFormat("d/M", locale);
                            name = sdf.format(expiry);
                            dpSpacesBetweenPoints = 50;
                            break;
                        case 3:
                            locale = Locale.ENGLISH;
                            sdf = new SimpleDateFormat("d/M/yy", locale);
                            name = sdf.format(expiry);
                            break;
                        default:
                            break;
                    }


                    switch (activityTaskList.get(j).getActivityClass()) {
                        case Distance:
                            dataPointsDistance.add(new DataPoint(activityTaskList.get(j).getValue(), name));
                            break;
                        case BMI:
                            dataPointsBMI.add(new DataPoint(activityTaskList.get(j).getValue(), name));
                            break;

                        case Steps:
                            dataPointsSteps.add(new DataPoint(activityTaskList.get(j).getValue(), name));
                            break;

                        case CaloriesBurned:
                            dataPointsCaloriesBurned.add(new DataPoint(activityTaskList.get(j).getValue(), name));
                            break;
                        case Unknown:

                        default:

                            break;
                    }

//                    Log.d(String.format("activityTaskList %d %d ", i, j), String.format("%d",activityTaskList.get(j).getValue()));
                }
            }


            List<List<DataPoint>> dataPoints = new ArrayList<>(Arrays.asList(dataPointsBMI, dataPointsDistance, dataPointsSteps, dataPointsCaloriesBurned));
            // Calculate the total width needed based on the number of data points and their spacing
            int totalDataPoints = Math.max(dataPointsDistance.size(), Math.max(dataPointsBMI.size(), Math.max(dataPointsSteps.size(), dataPointsCaloriesBurned.size())));
            float requiredWidth = totalDataPoints * dpToPx(dpSpacesBetweenPoints); // Adjusted spacing (40dp between data points)

            // Set the width of the LineGraphView to the calculated width, ensuring it's wide enough
            if (graphView.getLayoutParams() != null && totalDataPoints > 0) {
                graphView.getLayoutParams().width = (int) requiredWidth;
                graphView.requestLayout(); // Request layout update to reflect the new width
            }

            // Set the data to the graph view
            graphView.setData(dataPoints);


            // Scroll to the right-most side
            horizontalScrollView.post(() -> horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT));

        } catch (Exception e) {
            Log.d("LineGraphView", e.toString());
        }

    }

    // Add a method to convert dp to pixels
    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }


}

