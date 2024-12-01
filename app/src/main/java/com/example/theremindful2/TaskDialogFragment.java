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

    private final String[] tasks = {
            "Take a picture of something red",
            "Capture the reflection of yourself in a mirror or water",
            "Find and photograph your favorite book or magazine",
            "Take a picture of a cozy corner in your home",
            "Capture a moment of sunlight streaming through a window",
            "Photograph something that smells nice, like a flower or candle",
            "Take a picture of something furry, like a pet or a stuffed animal",
            "Capture an item that reminds you of a happy memory",
            "Photograph your favorite piece of clothing",
            "Find something that makes a soothing sound and take a picture of it",
            "Take a picture of something you use every day",
            "Capture an object that has your favorite color",
            "Take a picture of a pair of shoes or slippers",
            "Find and photograph something old and meaningful to you",
            "Capture the cover of your favorite music album or movie",
            "Take a picture of your favorite mug or glass",
            "Photograph a piece of art or decoration in your home",
            "Capture a sunrise or sunset if possible",
            "Take a picture of something you’ve recently cleaned or organized",
            "Find and photograph something with an interesting texture",
            "Capture a memory by taking a picture of your favorite room",
            "Take a picture of something you’ve written or drawn recently",
            "Photograph a tool or item you use for cooking or baking",
            "Take a picture of something you’d give as a gift",
            "Capture a moment with a friend or family member (with their permission)",
            "Take a picture of something you’d like to share with someone else",
            "Find and photograph a clock or watch showing the current time",
            "Take a picture of something you’ve recently enjoyed eating",
            "Capture a flower or leaf you find outside",
            "Photograph an object that you’d like to keep forever"
    };

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_task, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(view)
                .setTitle(getString(R.string.todaysTask));

        taskText = view.findViewById(R.id.taskTextView);
        doItLaterButton = view.findViewById(R.id.doItLaterButton);
        okayButton = view.findViewById(R.id.okayButton);

        if (isTaskCompletedToday()) {
            taskText.setText(getString(R.string.taskText));
            hideButtons();
        } else {
            selectRandomTask();
            taskText.setText(getSelectedTask());
        }

        doItLaterButton.setOnClickListener(v -> dismiss());
        okayButton.setOnClickListener(v -> handleCameraPermission());

        return builder.create();
    }

    private void handleCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(requireContext(), getString(R.string.cameraPermissionRequired), Toast.LENGTH_SHORT).show();
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
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(TASK_COMPLETED_KEY, true);
            editor.putString(COMPLETION_DATE_KEY, getCurrentDate());
            editor.apply();

            if (taskText != null) {
                taskText.setText(getString(R.string.taskText));
            }

            hideButtons();
        }
    }

    private void hideButtons() {
        if (doItLaterButton != null && okayButton != null) {
            doItLaterButton.setVisibility(View.GONE);
            okayButton.setVisibility(View.GONE);
        }
    }

    private String selectRandomTask() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Get today's date as a simple string
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = dateFormat.format(new Date());

        // Get the last assigned date and task
        String lastAssignedDate = sharedPreferences.getString("lastAssignedDate", "");
        String taskDescription = sharedPreferences.getString("selectedTask", "");

        // If the date has changed or no task is assigned, select a new task
        if (!todayDate.equals(lastAssignedDate) || taskDescription.isEmpty()) {
            // Select a random task from the list
            Random random = new Random();
            taskDescription = tasks[random.nextInt(tasks.length)];

            // Save the new task and today's date in SharedPreferences
            editor.putString("selectedTask", taskDescription);
            editor.putString("lastAssignedDate", todayDate);
            editor.putBoolean(TASK_COMPLETED_KEY, false); // Reset task completion state
            editor.apply();
        }

        return taskDescription;
    }


    private String getSelectedTask() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SELECTED_TASK_KEY, "Take a picture of something interesting!");
    }

    private boolean isTaskCompletedToday() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean taskCompleted = sharedPreferences.getBoolean(TASK_COMPLETED_KEY, false);
        String savedDate = sharedPreferences.getString(COMPLETION_DATE_KEY, "");
        return taskCompleted && savedDate.equals(getCurrentDate());
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date());
    }
}
