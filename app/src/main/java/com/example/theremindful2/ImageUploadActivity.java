package com.example.theremindful2;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageUploadActivity extends Activity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Button uploadButton;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        //uploadButton = findViewById(R.id.upload_button);
        imageView = findViewById(R.id.parentViewPager);

        // Set click listener for upload button
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            Uri imageUri = data.getData();

            // Display the selected image
            imageView.setImageURI(imageUri);

            // Show success message
            Toast.makeText(this, "Image selected successfully!", Toast.LENGTH_SHORT).show();

            // Here you can add code to save or upload the image
            saveImage(imageUri);
        }
    }

    private void saveImage(Uri imageUri) {
        // Add your image saving logic here
        // You might want to copy it to app's internal storage or upload to a server
        // Example of getting the file path:
        String imagePath = imageUri.getPath();
        // TODO: Add your saving logic
    }
}