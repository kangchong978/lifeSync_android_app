package com.example.lifesync;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class AddActivityTabViewFragment extends Fragment {

    private final List<ActivityTask> availableActivityList = new ArrayList<>(Arrays.asList(FirstFragment.getDummyActivityTasks().toArray(new ActivityTask[0])));

    static List<AddActivityItemSpinnerAdapter.SpinnerItem> spinnerItemsList = new ArrayList<>(Arrays.asList(
            new AddActivityItemSpinnerAdapter.SpinnerItem(R.drawable.round_directions_run_24, "Steps"),
            new AddActivityItemSpinnerAdapter.SpinnerItem(R.drawable.round_speed_24, "BMI"),
            new AddActivityItemSpinnerAdapter.SpinnerItem(R.drawable.round_directions_24, "Distance"),
            new AddActivityItemSpinnerAdapter.SpinnerItem(R.drawable.round_local_fire_department_24, "CaloriesBurned")
            // Add more initial ActivityInfo objects as needed
    ));
    private static final String ARGUMENT_KEY = UUID.randomUUID().toString();

    private View view;

    public static AddActivityTabViewFragment newInstance(String argument) {
        AddActivityTabViewFragment fragment = new AddActivityTabViewFragment();
        Bundle args = new Bundle();
        args.putString(ARGUMENT_KEY, argument);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Bundle args = getArguments();
        // if (args != null) {
            // String argument = args.getString(ARGUMENT_KEY);
            // Now you can use the 'argument' in your fragment
        // }
        return inflater.inflate(R.layout.add_activity_tab_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        LinearLayout addTaskButton = view.findViewById(R.id.add_new_activity_button);
        addTaskButton.setOnClickListener(v -> {
            ActivityTask newTask = new ActivityTask(ActivityClass.Steps, 0, 0);
            availableActivityList.add(newTask);

            // Create a new row for the new task and add it to the UI
            buildTaskListViewUI(true);

        });

        buildTaskListViewUI(false);
    }

    private void buildTaskListViewUI(boolean isNew) {
        LinearLayout parentLayout = view.findViewById(R.id.parentLinearLayout);

        int index = 0;
        if (isNew) index = availableActivityList.size() - 1;
        int length = availableActivityList.size();

        for (int i = index; i < length; i++) {
            ActivityTask activityTask = (ActivityTask) availableActivityList.get(i);
            int activityValue = activityTask.getTargetValue();
            ActivityClass activityType = activityTask.getActivityClass();

            // Create a new LinearLayout for each row
            LinearLayout parentLinearLayout = new LinearLayout(requireContext());
            parentLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams layoutParamsParent = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParamsParent.setMargins(0, 0, 0, 50);

            layoutParamsParent.gravity = Gravity.CENTER_VERTICAL; // Center vertically
            parentLinearLayout.setLayoutParams(layoutParamsParent);

            View removeIcon = new View(requireContext());
            removeIcon.setBackgroundResource(R.drawable.round_remove_24);
            int fixedWidthInPixels = 60; // Set your desired static width here
            int fixedHeightInPixels = 60; // Set your desired height here
            LinearLayout.LayoutParams layoutParamsRemove = new LinearLayout.LayoutParams(fixedWidthInPixels, fixedHeightInPixels);
            layoutParamsRemove.gravity = Gravity.CENTER_VERTICAL;
            layoutParamsRemove.rightMargin = 40;
            removeIcon.setLayoutParams(layoutParamsRemove);
            removeIcon.setOnClickListener(v -> {
                // Remove the item from availableActivityList
                availableActivityList.remove(activityTask);

                // Remove the corresponding view from the parentLayout
                parentLayout.removeView(parentLinearLayout);
            });
            parentLinearLayout.addView(removeIcon);

            // Create a new LinearLayout for each row
            LinearLayout linearLayout = new LinearLayout(requireContext());
            // Set margins for the LinearLayout using LayoutParams
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            );
            linearLayout.setBackgroundResource(R.drawable.add_activity_item);
            linearLayout.setPadding(10, 10, 20, 10);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            layoutParams.setMargins(0, 0, 0, 0);
            linearLayout.setLayoutParams(layoutParams);
//            // Create a Spinner and set its adapter
            Spinner spinner = new Spinner(requireContext());
            spinner.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, MATCH_PARENT
            ));
            AddActivityItemSpinnerAdapter adapter = new AddActivityItemSpinnerAdapter(view.getContext(), spinnerItemsList);
            spinner.setAdapter(adapter);

            if (activityType.getInt() >= 0 && activityType.getInt() < spinnerItemsList.size())
                spinner.setSelection(activityType.getInt());
//
//            // Create an EditText
            EditText editText = new EditText(requireContext());
            editText.setLayoutParams(new ViewGroup.LayoutParams(
                    MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            editText.setSingleLine(true);
            editText.setHint("Type here");

            editText.setText(String.valueOf(activityValue));
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            editText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Clear focus and close the keyboard when the "Done" button is pressed
                    editText.clearFocus();

                    // Optionally, you can also close the keyboard programmatically
                    InputMethodManager imm = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    }

                    if (imm != null) {
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    }

                    return true; // Return true to indicate that the event has been consumed
                }
                return false; // Return false if you want to let the default handling occur
            });
//            // Add views to the row LinearLayout
            linearLayout.addView(spinner);
            linearLayout.addView(editText);
//
//            // Add the row to the parent LinearLayout
            parentLinearLayout.addView(linearLayout);
            parentLayout.addView(parentLinearLayout);

        }

    }
}


