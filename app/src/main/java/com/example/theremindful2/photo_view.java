package com.example.theremindful2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class photo_view extends AppCompatActivity {
    private List<String> tagsList;
    private List<String> selectedTags;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_view);

        // Initialize tags list with some default tags
        tagsList = new ArrayList<>();
        tagsList.add("Nature");
        tagsList.add("Vacation");
        tagsList.add("Family");
        tagsList.add("Friends");

        selectedTags = new ArrayList<>();

        Intent intent = getIntent();
        String UriString = intent.getStringExtra("Uri");
        imageUri = Uri.parse(UriString);

        ImageView image = findViewById(R.id.imageView2);
        image.setImageURI(imageUri);

        // Enable the action bar back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Edit description button
        Button editDescription = findViewById(R.id.DescriptionButton);
        editDescription.setOnClickListener(view -> showEditDescriptionDialog());

        // Edit tags button
        Button editTags = findViewById(R.id.TagsButton);
        editTags.setOnClickListener(view -> showEditTagsDialog());

        // Save button
        Button saveButton = findViewById(R.id.SaveButton);
        saveButton.setOnClickListener(view -> saveImageAndReturn());
    }

    private void showEditDescriptionDialog() {
        TextView description = findViewById(R.id.photoDescription);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Description");

        // Set up the input
        final EditText input = new EditText(this);
        input.setText(description.getText());
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            String newDescription = input.getText().toString();
            description.setText(newDescription);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showEditTagsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Tags");

        // Convert tags list to an array
        String[] tagsArray = tagsList.toArray(new String[0]);
        boolean[] checkedTags = new boolean[tagsArray.length];

        builder.setMultiChoiceItems(tagsArray, checkedTags, (dialog, which, isChecked) -> {
            // Handle tag selection
            checkedTags[which] = isChecked;
        });

        builder.setPositiveButton("Add Custom Tag", (dialog, which) -> showAddCustomTagDialog());

        builder.setNegativeButton("OK", (dialog, which) -> {
            // Gather selected tags
            selectedTags.clear();
            for (int i = 0; i < checkedTags.length; i++) {
                if (checkedTags[i]) {
                    selectedTags.add(tagsArray[i]);
                }
            }
            Toast.makeText(photo_view.this, "Selected Tags: " + selectedTags.toString(), Toast.LENGTH_SHORT).show();
        });

        builder.setNeutralButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showAddCustomTagDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Custom Tag");

        // Set up the input
        final EditText input = new EditText(this);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            String newTag = input.getText().toString().trim();
            if (!newTag.isEmpty() && !tagsList.contains(newTag)) {
                tagsList.add(newTag);
                Toast.makeText(photo_view.this, "Tag added: " + newTag, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(photo_view.this, "Tag already exists or is empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void saveImageAndReturn() {
        try {
            // Create a unique filename for the image
            String filename = UUID.randomUUID().toString() + ".jpg";
            File directory = getExternalFilesDir(null); // Save to external files directory for accessibility
            File file = new File(directory, filename);

            // Copy the image from the URI to external storage
            try (FileOutputStream out = new FileOutputStream(file);
                 java.io.InputStream in = getContentResolver().openInputStream(imageUri)) {

                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            }

            // Save metadata using only the selected tags
            MetadataUtils.saveImageMetadata(this, file.getAbsolutePath(), selectedTags);

            // Return to the main screen
            Intent intent = new Intent(photo_view.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Handles the action when the back button is pressed
        onBackPressed();
        return true;
    }
}
