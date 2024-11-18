package com.example.theremindful2;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TaskDialogFragment extends DialogFragment {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

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

        // Set up the task description
        TextView taskText = view.findViewById(R.id.taskTextView);
        taskText.setText("Take a picture of a flower");

        // Set up the "Do it later" button to close the dialog
        Button doItLaterButton = view.findViewById(R.id.doItLaterButton);
        doItLaterButton.setOnClickListener(v -> {
            // Dismiss the dialog
            dismiss();
        });

        // Set up the "Okay!" button to open the camera
        Button okayButton = view.findViewById(R.id.okayButton);
        okayButton.setOnClickListener(v -> {
            // Check for camera permission
           /* if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request camera permission
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            } else {
                // Open the camera if permission is already granted
                openCamera();
            }*/
            openCamera();

        });

        return builder.create();
    }

    // Method to open the camera
   /* private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(requireContext(), "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }*/

    // Handle the permission result
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted, open the camera
//                openCamera();
//            } else {
//                // Permission denied, show a message
//                Toast.makeText(requireContext(), "Camera permission is required to take a picture", Toast.LENGTH_SHORT).show();
//            }
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
    private static final int CAMERA_REQUEST_CODE = 1001;

    private void openCamera() {
        Intent intent = new Intent(requireActivity(), CameraActivity.class);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String photoPath = data.getStringExtra("photo_path");
                // Handle the captured photo path here
                // You can save it to your Room database or handle it as needed
                Toast.makeText(requireContext(),
                        "Photo saved: " + photoPath, Toast.LENGTH_SHORT).show();
                System.out.print("Photo saved: " + photoPath );

            }
        }
    }
}