package com.example.theremindful2;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class TaskDialogFragment extends DialogFragment {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 1001;
    private static final String PREFS_NAME = "TaskPrefs";
    private static final String TASK_COMPLETED_KEY = "taskCompleted";
    private static final String SELECTED_TASK_KEY = "selectedTask";
    private static final String COMPLETION_DATE_KEY = "completionDate";
    private TextView taskText;
    private Button doItLaterButton;
    private Button okayButton;

    // List of tasks
    private final String[] tasks = {
            "Take a picture of something blue",
            "Capture a smile from a loved one or friend",
            "Photograph your favorite item in the kitchen",
            "Take a picture of something soft and comforting",
            "Find and photograph a beautiful pattern",
            "Take a picture of a tree or plant in your neighborhood",
            "Capture the view from your window",
            "Take a photo of your favorite snack or meal",
            "Find and photograph something round",
            "Take a picture of something that makes you happy"
    };

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Inflate the custom layout for the dialog
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_task, null);

        // Set up the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(view)
                .setTitle("Today's Task");

        // Initialize the task description TextView and buttons
        taskText = view.findViewById(R.id.taskTextView);
        doItLaterButton = view.findViewById(R.id.doItLaterButton);
        okayButton = view.findViewById(R.id.okayButton);

        // Check if the task has already been completed today
        if (isTaskCompletedToday()) {
            taskText.setText("You have completed today's task!\nGreat Job ;>");
            hideButtons();
        } else {
            // Select a random task if it's a new day
            selectRandomTask();
            taskText.setText(getSelectedTask());
        }

        // Set up the "Do it later" button to close the dialog
        doItLaterButton.setOnClickListener(v -> dismiss());

        // Set up the "Okay!" button to open the CameraActivity or request permission
        okayButton.setOnClickListener(v -> handleCameraPermission());

        return builder.create();
    }

    private void handleCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Request camera permission using the fragment method
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            // Open the camera if permission is already granted
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open the camera immediately
                openCamera();
            } else {
                // Permission denied, show a message
                Toast.makeText(requireContext(), "Camera permission is required to take a picture", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void openCamera() {
        Intent intent = new Intent(requireActivity(), CameraActivity.class);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Mark the task as completed
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(TASK_COMPLETED_KEY, true);
            editor.apply();

            // Update the task text to only show the completion message
            if (taskText != null) {
                taskText.setText("You have completed today's task!\nGreat Job ;>");
            }

            // Hide the buttons
            hideButtons();
        }
    }

    private void hideButtons() {
        if (doItLaterButton != null && okayButton != null) {
            doItLaterButton.setVisibility(View.GONE);
            okayButton.setVisibility(View.GONE);
        }
    }

    private void selectRandomTask() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String currentDate = getCurrentDate();
        String savedDate = sharedPreferences.getString(COMPLETION_DATE_KEY, "");

        if (!currentDate.equals(savedDate)) {
            // It's a new day, select a random task
            Random random = new Random();
            String randomTask = tasks[random.nextInt(tasks.length)];

            // Save the selected task and date
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SELECTED_TASK_KEY, randomTask);
            editor.putString(COMPLETION_DATE_KEY, currentDate);
            editor.putBoolean(TASK_COMPLETED_KEY, false); // Reset task completion
            editor.apply();
        }
    }

    private String getSelectedTask() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SELECTED_TASK_KEY, "Take a picture of something interesting!");
    }

    private boolean isTaskCompletedToday() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean taskCompleted = sharedPreferences.getBoolean(TASK_COMPLETED_KEY, false);
        String savedDate = sharedPreferences.getString(COMPLETION_DATE_KEY, "");

        // Check if the saved date matches the current date
        return taskCompleted && savedDate.equals(getCurrentDate());
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return dateFormat.format(new Date());
    }
}
