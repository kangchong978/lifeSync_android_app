package com.example.lifesync;

import static com.example.lifesync.ActivityClass.Steps;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import androidx.annotation.RequiresApi;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.lifesync.databinding.FragmentFirstBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class FirstFragment extends Fragment implements AddActivityModalFragment.OnAddActivityListener {

    private FragmentFirstBinding binding;
    private static Context context;
    static String[] viewActivityOptions = {"7 days", "30 days", "365 days", "All"};
    static String[] viewActivityClassOptions = {"Steps", "Distance", "BMI", "Calories"};
    static String greeting = "Welcome";
    static String username = "Sophia Muller";

    ActivityTask[] activeActivityList = {};
    ActivityTask[] inProgressActivityList = {};

    private LineGraphView graphView;

    private TextView progressPercentage;
    private HorizontalScrollView horizontalScrollView; // Replace with your view ID


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

    Intent intent;

    StepCountService stepCountService;

    LinearLayout dailyActivityLinearLayout;
    CircularProgressIndicator circularProgressIndicatorSmall;
    CircularProgressIndicator circularProgressIndicatorMiddle;
    CircularProgressIndicator circularProgressIndicatorBig;

    private boolean isServiceBound = false;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("binder", "ServiceConnection: connected to service.");
            StepCountService.UiBinder binder = (StepCountService.UiBinder) service;
            stepCountService = binder.getService();

            updateFragmentUi(stepCountService.getSensorData()); // initial
            stepCountService.setStepCountCallback((e) -> {
                Log.d("Value callback", String.valueOf(e));

                int previousDayOfWeek = todayDayOfWeek;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    todayDayOfWeek = LocalDate.now().getDayOfWeek().getValue();
                    todayDayOfWeek = (todayDayOfWeek >= 7) ? 0 : todayDayOfWeek;

                }
                if (previousDayOfWeek != todayDayOfWeek) {
                    activeActivityList = dbManager.fetchActivityTasks(todayDayOfWeek).toArray(new ActivityTask[0]);
                    String idsString = "";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        List<Integer> _activityTasksIds = dbManager.fetchActivityTasks(todayDayOfWeek).stream().map(a -> a.getId()).collect(Collectors.toList());

                        for (int i = 0; i < _activityTasksIds.size(); i++) {
                            idsString += _activityTasksIds.get(i) + ((i < _activityTasksIds.size() - 1) ? "," : "");
                        }
                    }
                    SensorData sensorData = e;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        dbManager.insertActivityRecord(sensorData.steps, sensorData.steps, sensorData.bmi, sensorData.distance, sensorData.calories, (int) LocalDate.now().atStartOfDay().toEpochSecond(ZoneOffset.UTC), idsString);
                    }
                    stepCountService.reset();
                }

                updateFragmentUi(e);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    setMyActivityGraphView();
                }

            });
            isServiceBound = true;
        }


        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d("binder", "ServiceConnection: disconnected to service.");
            isServiceBound = false;
        }
    };
    private boolean redirectToOnboarding;
    private int spinnerOptionId = 0;
    private int classSpinnerOptionId = 0;
    private UserInfo userInfo;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isServiceBound) {
            MainActivity mainActivity = (MainActivity) getActivity();
            stepCountService.setStepCountCallback(null);
            mainActivity.unbindService(serviceConnection);
            isServiceBound = false;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.bindStepService(serviceConnection);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            todayDayOfWeek = LocalDate.now().getDayOfWeek().getValue();
            todayDayOfWeek = (todayDayOfWeek >= 7) ? 0 : todayDayOfWeek;
        }
        dbManager = new DBManager(getContext());
        dbManager.open();
        activeActivityList = dbManager.fetchActivityTasks(todayDayOfWeek).toArray(new ActivityTask[0]);
        userInfo = dbManager.fetchUserInfo();

        if (userInfo != null) {
            username = userInfo.name;
        } else {
            redirectToOnboarding = true;

        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        NavHostFragment navHostFragment = (NavHostFragment) Objects.requireNonNull(getActivity()).getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();
        if (redirectToOnboarding)
            navController.navigate(R.id.OnBoardingFragment);

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = view.getContext();

        TextView versionTextView = view.findViewById(R.id.versionTextView);
        versionTextView.setText("version: " + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")");


        progressPercentage = (TextView) view.findViewById(R.id.progressPercentage);
        graphView = view.findViewById(R.id.graphView);
        horizontalScrollView = view.findViewById(R.id.dailyActivityHorizontalScrollView1);


        // My activity Ui Status Update
        // Spinner items assign
        Spinner spinner = (Spinner) view.findViewById(R.id.activity_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.activity_spinner_item, R.id.optionName, viewActivityOptions);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //                Log.d("Spinner", String.format("%d", id));
                spinnerOptionId = (int) id;
                setMyActivityGraphView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        Spinner classSpinner = (Spinner) view.findViewById(R.id.activity_class_spinner);
        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(context, R.layout.activity_spinner_item, R.id.optionName, viewActivityClassOptions);
        classSpinner.setAdapter(classAdapter);
        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long a) {
                classSpinnerOptionId = (int) a;
                setMyActivityGraphView();
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
        dailyActivityLinearLayout = (LinearLayout) view.findViewById(R.id.dailyActivityLinearLayout);
        circularProgressIndicatorSmall = (CircularProgressIndicator) view.findViewById(R.id.progress_circular_small);
        circularProgressIndicatorMiddle = (CircularProgressIndicator) view.findViewById(R.id.progress_circular_middle);
        circularProgressIndicatorBig = (CircularProgressIndicator) view.findViewById(R.id.progress_circular_big);
        // Assuming you have an array to store references to the DailyActivityView objects


        inProgressActivityList = activeActivityList;


        ImageButton menu_button = view.findViewById(R.id.menu_button);
        menu_button.setOnClickListener(view12 -> {

            NavHostFragment navHostFragment = (NavHostFragment) Objects.requireNonNull(getActivity()).getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
            assert navHostFragment != null;
            NavController navController = navHostFragment.getNavController();
            Bundle bundle = new Bundle();
            bundle.putString("previousName", userInfo.name);
            bundle.putInt("previousHeight", userInfo.height);
            bundle.putInt("previousWeight", userInfo.weight);
            bundle.putInt("previousAge", userInfo.age);
            navController.navigate(R.id.OnBoardingFragment, bundle);
        });

    }


    private void updateFragmentUi(SensorData sensorData) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            for (int i = 0; i < activeActivityList.length; i++) {
                if (activeActivityList[i].getValue() >= 0) {
                    activeActivityList[i].setPreviousValue(activeActivityList[i].getValue());
                }

                int value = sensorData.steps;
                // process data
                switch (activeActivityList[i].getActivityClass()) {

                    case Unknown:
                        break;
                    case Steps:
                        break;
                    case Distance:
                        value = (int) sensorData.distance;
                        break;
                    case BMI:
                        value = sensorData.bmi;
                        break;
                    case CaloriesBurned:
                        value = sensorData.calories;
                        break;
                }
                activeActivityList[i].setValue(value);


                if (activeActivityList[i].getValue() >= activeActivityList[i].getTargetValue() && !activeActivityList[i].isDone()) {
                    activeActivityList[i].setDone(true);
                }
            }

            inProgressActivityList = Arrays.stream(activeActivityList).filter(e -> !e.isDone()).toArray(ActivityTask[]::new);
            Arrays.sort(inProgressActivityList);
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
        updateFragmentUi(stepCountService.getSensorData());
    }


    private void showBottomSheetDialog() {
        BottomSheetDialogFragment addActivityFragment = new AddActivityModalFragment(todayDayOfWeek);
        ((AddActivityModalFragment) addActivityFragment).setOnAddActivityListener(this);
        addActivityFragment.show(getParentFragmentManager(), "BSDialogFragment");

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setMyActivityGraphView() {
        try {

            List<HistoryActivity> activityHistoriesList = dbManager.fetchActivityRecords();


            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setFirstDayOfWeek(Calendar.SUNDAY);
            currentCalendar.setTime(new Date());
            currentCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            currentCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);

            Calendar calendar = Calendar.getInstance();
            Date startDate;
            Date currentDate;
            switch (spinnerOptionId) {
                case 0:
                    // Filter for today and the 7 days before today

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
// Calculate the start date (30 days ago from the current date)

                    calendar.setTime(new Date());
                    calendar.add(Calendar.DAY_OF_MONTH, -30); // Subtract 30 days

                    startDate = calendar.getTime();
                    currentDate = currentCalendar.getTime();

// Filter the activityHistoriesList for items within the last 30 days
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        activityHistoriesList = activityHistoriesList.stream().filter(a -> {
                            @SuppressLint("DefaultLocale") Date expiry = new Date(Long.parseLong(String.format("%d", a.getTimeStamp())) * 1000);
                            return expiry.after(startDate) && expiry.before(currentDate);
                        }).collect(Collectors.toList());
                    }
                    break;
                case 2:
                    // Calculate the start date (365 days ago from the current date)

                    calendar.setTime(new Date());
                    calendar.add(Calendar.DAY_OF_YEAR, -365); // Subtract 365 days

                    startDate = calendar.getTime();
                    currentDate = currentCalendar.getTime();

// Filter the activityHistoriesList for items within the last 365 days
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        activityHistoriesList = activityHistoriesList.stream().filter(a -> {
                            @SuppressLint("DefaultLocale") Date expiry = new Date(Long.parseLong(String.format("%d", a.getTimeStamp())) * 1000);
                            return expiry.after(startDate) && expiry.before(currentDate);
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

                List<ActivityTask> activityTaskList = activity.getActivityTaskList().stream().filter((e) -> e.getActivityClass() == ActivityClass.getClassFromInt(classSpinnerOptionId)).collect(Collectors.toList());
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


                    switch (ActivityClass.getClassFromInt(classSpinnerOptionId)) {
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

                }
            }

//            List<List<DataPoint>> dataPoints = new ArrayList<>(Arrays.asList(  dataPointsSteps ));
//            int totalDataPoints =  dataPointsSteps.size();

            List<List<DataPoint>> dataPoints = new ArrayList<>(Arrays.asList(dataPointsBMI, dataPointsDistance, dataPointsSteps, dataPointsCaloriesBurned));
            // Calculate the total width needed based on the number of data points and their spacing
            int totalDataPoints = Math.max(dataPointsDistance.size(), Math.max(dataPointsBMI.size(), Math.max(dataPointsSteps.size(), dataPointsCaloriesBurned.size())));
            float requiredWidth = totalDataPoints * dpToPx(dpSpacesBetweenPoints); // Adjusted spacing (40dp between data points)

            // Set the width of the LineGraphView to the calculated width, ensuring it's wide enough
            if (graphView.getLayoutParams() != null && totalDataPoints > 0) {
                graphView.getLayoutParams().width = (int) requiredWidth;
                graphView.requestLayout(); // Request layout update to reflect the new width
            }

            graphView.resetData();
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

