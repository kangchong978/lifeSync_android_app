package com.example.lifesync;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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


        displayOnboardingStepView(OnboardingStep.Welcome);
    }

    public void onClickNextView() {
        displayOnboardingStepView(OnboardingStep.getNextOnboardingStepFromIndex(currentStep.getIndex()));
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
                break;
            case InputHeight:
                onboarding_height_title_textview.setVisibility(View.VISIBLE);
                onboarding_weight_edittext.setVisibility(View.VISIBLE);
                onboarding_next_view.setVisibility(View.VISIBLE);
                break;
            case InputWeight:
                onboarding_weight_title_textview.setVisibility(View.VISIBLE);
                onboarding_weight_edittext.setVisibility(View.VISIBLE);
                onboarding_next_view.setVisibility(View.VISIBLE);
                break;
            case InputAge:
                onboarding_age_title_textview.setVisibility(View.VISIBLE);
                onboarding_age_edittext.setVisibility(View.VISIBLE);
                onboarding_next_view.setVisibility(View.VISIBLE);
                break;
            case Congrats:
                onboarding_congrats_title_textview.setVisibility(View.VISIBLE);
                onboarding_congrats_subtitle_textview.setVisibility(View.VISIBLE);
                onboarding_next_view.setVisibility(View.VISIBLE);
                break;
            case HomePage:
                onboarding_next_view.setVisibility(View.GONE);
                onboarding_loading_CircularProgressIndicator.setVisibility(View.VISIBLE);
                navigationToHomePage();
                break;
        }

        currentStep = step;
    }

    private void navigationToHomePage() {
        navController.navigate(R.id.FirstFragment);
    }
}