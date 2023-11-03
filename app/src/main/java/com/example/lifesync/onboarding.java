package com.example.lifesync;


import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.Objects;

enum OnboardingStep {
    Welcome(0), InputName(1), InputHeight(2), InputWeight(3), InputAge(4), Congrats(5), HomePage(6);
    private final int index;

    OnboardingStep(int index) {
        this.index = index;
    }

    public static OnboardingStep getNextOnboardingStepFromIndex(int index) {
        for (OnboardingStep step : OnboardingStep.values()) {
            if (step.getIndex() == index + 1) {
                return step;
            }
        }
        return OnboardingStep.values()[0];
    }

    public int getIndex() {
        return index;
    }
}

public class onboarding extends Fragment {
    private NavController navController;
    private OnboardingStep currentStep = OnboardingStep.Welcome;

    private TextView onboarding_welcome_title_textview;
    private TextView onboarding_name_title_textview;
    private TextView onboarding_height_title_textview;
    private TextView onboarding_weight_title_textview;
    private TextView onboarding_age_title_textview;
    private TextView onboarding_congrats_title_textview;

    private TextView onboarding_welcome_subtitle_textview;
    private EditText onboarding_name_edittext;
    private EditText onboarding_height_edittext;
    private EditText onboarding_weight_edittext;
    private EditText onboarding_age_edittext;
    private TextView onboarding_congrats_subtitle_textview;

    private View onboarding_next_view;

    private CircularProgressIndicator onboarding_loading_CircularProgressIndicator;

    private String name;
    private int weight;
    private int height;
    private int age;

    private boolean skipHurray = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            name = args.getString("previousName");
            height = args.getInt("previousHeight");
            weight = args.getInt("previousWeight");
            age = args.getInt("previousAge");

            currentStep  = OnboardingStep.InputName;
            skipHurray = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_onboarding, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavHostFragment navHostFragment = (NavHostFragment) Objects.requireNonNull(getActivity()).getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();

        onboarding_welcome_title_textview = view.findViewById(R.id.onboarding_welcome_title_textview);
        onboarding_name_title_textview = view.findViewById(R.id.onboarding_name_title_textview);
        onboarding_height_title_textview = view.findViewById(R.id.onboarding_height_title_textview);
        onboarding_weight_title_textview = view.findViewById(R.id.onboarding_weight_title_textview);
        onboarding_age_title_textview = view.findViewById(R.id.onboarding_age_title_textview);
        onboarding_congrats_title_textview = view.findViewById(R.id.onboarding_congrats_title_textview);

        onboarding_welcome_subtitle_textview = view.findViewById(R.id.onboarding_welcome_subtitle_textview);
        onboarding_name_edittext = view.findViewById(R.id.onboarding_name_edittext);
        onboarding_height_edittext = view.findViewById(R.id.onboarding_height_edittext);
        onboarding_weight_edittext = view.findViewById(R.id.onboarding_weight_edittext);
        onboarding_age_edittext = view.findViewById(R.id.onboarding_age_edittext);
        onboarding_congrats_subtitle_textview = view.findViewById(R.id.onboarding_congrats_subtitle_textview);

        onboarding_next_view = view.findViewById(R.id.onboarding_next_view);
        onboarding_next_view.setVisibility(View.VISIBLE);
        onboarding_next_view.setOnClickListener(v -> onClickNextView());
        onboarding_loading_CircularProgressIndicator = view.findViewById(R.id.onboarding_loading_CircularProgressIndicator);


        displayOnboardingStepView(currentStep);
    }

    public void onClickNextView() {
        if (isValid(currentStep))
            displayOnboardingStepView(OnboardingStep.getNextOnboardingStepFromIndex(currentStep.getIndex()));
    }

    boolean isValid(OnboardingStep step) {
        switch (step) {
            case Welcome:
                break;
            case InputName:
                if (onboarding_name_edittext.getText().toString().trim().isEmpty()) {
                    onboarding_name_edittext.setError("This field is required");
                    onboarding_name_edittext.requestFocus();
                    return false;
                } else {
                    onboarding_name_edittext.clearFocus();
                    name = onboarding_name_edittext.getText().toString();
                }
                break;

            case InputHeight:
                String heightString = onboarding_height_edittext.getText().toString().trim();
                if (heightString.isEmpty()) {
                    onboarding_height_edittext.setError("This field is required");
                    onboarding_height_edittext.requestFocus();
                    return false;
                } else {
                    try {
                        int heightValue = Integer.parseInt(heightString);
                        onboarding_height_edittext.clearFocus();
                        height = heightValue;
                        break;
                    } catch (NumberFormatException e) {
                        onboarding_height_edittext.setError("Invalid input. Please enter a valid integer");
                        onboarding_height_edittext.requestFocus();
                        return false;
                    }
                }
            case InputWeight:
                String weightString = onboarding_weight_edittext.getText().toString().trim();
                if (weightString.isEmpty()) {
                    onboarding_height_edittext.setError("This field is required");
                    onboarding_height_edittext.requestFocus();
                    return false;
                } else {
                    try {
                        int weightValue = Integer.parseInt(weightString);
                        onboarding_height_edittext.clearFocus();
                        weight = weightValue;
                        break;
                    } catch (NumberFormatException e) {
                        onboarding_height_edittext.setError("Invalid input. Please enter a valid integer");
                        onboarding_height_edittext.requestFocus();
                        return false;
                    }
                }
            case InputAge:
                String ageString = onboarding_age_edittext.getText().toString().trim();
                if (ageString.isEmpty()) {
                    onboarding_height_edittext.setError("This field is required");
                    onboarding_height_edittext.requestFocus();
                    return false;
                } else {
                    try {
                        int ageValue = Integer.parseInt(ageString);
                        onboarding_height_edittext.clearFocus();
                        age = ageValue;
                        break;
                    } catch (NumberFormatException e) {
                        onboarding_height_edittext.setError("Invalid input. Please enter a valid integer");
                        onboarding_height_edittext.requestFocus();
                        return false;
                    }
                }
            case Congrats:
                break;
            case HomePage:
                break;
        }

        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(onboarding_weight_edittext.getWindowToken(), 0);


        return true;
    }

    private void resetOnboardingStepView() {

        onboarding_welcome_title_textview.setVisibility(View.GONE);
        onboarding_name_title_textview.setVisibility(View.GONE);
        onboarding_height_title_textview.setVisibility(View.GONE);
        onboarding_weight_title_textview.setVisibility(View.GONE);
        onboarding_age_title_textview.setVisibility(View.GONE);
        onboarding_congrats_title_textview.setVisibility(View.GONE);

        onboarding_welcome_subtitle_textview.setVisibility(View.GONE);
        onboarding_name_edittext.setVisibility(View.GONE);
        onboarding_height_edittext.setVisibility(View.GONE);
        onboarding_weight_edittext.setVisibility(View.GONE);
        onboarding_age_edittext.setVisibility(View.GONE);
        onboarding_congrats_subtitle_textview.setVisibility(View.GONE);

        onboarding_next_view.setVisibility(View.GONE);
        onboarding_loading_CircularProgressIndicator.setVisibility(View.GONE);

    }

    private void displayOnboardingStepView(OnboardingStep step) {
        resetOnboardingStepView();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        switch (step) {
            case Welcome:
                onboarding_welcome_title_textview.setVisibility(View.VISIBLE);
                onboarding_welcome_subtitle_textview.setVisibility(View.VISIBLE);
                onboarding_next_view.setVisibility(View.VISIBLE);
                break;
            case InputName:
                onboarding_name_title_textview.setVisibility(View.VISIBLE);
                onboarding_name_edittext.setVisibility(View.VISIBLE);
                onboarding_next_view.setVisibility(View.VISIBLE);
                onboarding_name_edittext.setText(name);
                onboarding_name_edittext.requestFocus();
                imm.showSoftInput(onboarding_name_edittext, InputMethodManager.SHOW_IMPLICIT);
                break;
            case InputHeight:
                onboarding_height_title_textview.setVisibility(View.VISIBLE);
                onboarding_height_edittext.setVisibility(View.VISIBLE);
                onboarding_next_view.setVisibility(View.VISIBLE);
                onboarding_height_edittext.setText(String.valueOf(height));
                onboarding_height_edittext.requestFocus();
                imm.showSoftInput(onboarding_height_edittext, InputMethodManager.SHOW_IMPLICIT);
                break;
            case InputWeight:
                onboarding_weight_title_textview.setVisibility(View.VISIBLE);
                onboarding_weight_edittext.setVisibility(View.VISIBLE);
                onboarding_next_view.setVisibility(View.VISIBLE);
                onboarding_weight_edittext.setText(String.valueOf(weight));
                onboarding_weight_edittext.requestFocus();
                imm.showSoftInput(onboarding_weight_edittext, InputMethodManager.SHOW_IMPLICIT);
                break;
            case InputAge:
                onboarding_age_title_textview.setVisibility(View.VISIBLE);
                onboarding_age_edittext.setVisibility(View.VISIBLE);
                onboarding_next_view.setVisibility(View.VISIBLE);
                onboarding_age_edittext.setText(String.valueOf(age));
                onboarding_age_edittext.requestFocus();
                imm.showSoftInput(onboarding_age_edittext, InputMethodManager.SHOW_IMPLICIT);
                break;
            case Congrats:
                if(!skipHurray){
                    onboarding_congrats_title_textview.setVisibility(View.VISIBLE);
                    onboarding_congrats_subtitle_textview.setVisibility(View.VISIBLE);
                    onboarding_next_view.setVisibility(View.VISIBLE);
                    break;
                }
            case HomePage:
                onboarding_next_view.setVisibility(View.GONE);
                onboarding_loading_CircularProgressIndicator.setVisibility(View.VISIBLE);
                addUserInfo();
                navigationToHomePage();

                break;
        }

        currentStep = step;
    }

    private void navigationToHomePage() {
        navController.navigate(R.id.FirstFragment);
    }

    private void addUserInfo() {
        DBManager dbManager = new DBManager(getContext());
        dbManager.open();
        dbManager.insertUserInfo(name, weight, height, age);
    }
}