package com.example.theremindful2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class VirtualSceneActivity extends AppCompatActivity {
    private View sceneView;
    private float previousX, previousY;
    private float rotationX = 0f;
    private float rotationY = 0f;
    private static final float ROTATION_SENSITIVITY = 0.5f;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_scene);

        // Initialize the scene view
        sceneView = findViewById(R.id.sceneView);

        // Set up a touch listener to handle mouse or touchpad input
        sceneView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Record the initial touch positions
                    previousX = event.getX();
                    previousY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    // Calculate the difference in x and y positions
                    float deltaX = event.getX() - previousX;
                    float deltaY = event.getY() - previousY;

                    // Update the rotation angles
                    rotationX += deltaY * ROTATION_SENSITIVITY;
                    rotationY += deltaX * ROTATION_SENSITIVITY;

                    // Apply the rotations to the scene view
                    sceneView.setRotationX(rotationX);
                    sceneView.setRotationY(rotationY);

                    // Update the previous positions
                    previousX = event.getX();
                    previousY = event.getY();
                    break;
            }
            return true;
        });
    }
}
