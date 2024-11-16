package com.example.theremindful2;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.flexbox.FlexboxLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.appcompat.app.AlertDialog;

public class photo_view extends AppCompatActivity{
    private List<String> tagsList;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstaceState){
        super.onCreate(savedInstaceState);
        setContentView(R.layout.photo_view);


        // Initialize tags list with some default tags
        tagsList = new ArrayList<>();
        tagsList.add("Nature");
        tagsList.add("Vacation");
        tagsList.add("Family");
        tagsList.add("Friends");

        Intent intent = getIntent();
        String UriString = intent.getStringExtra("Uri");
        Uri imageUri = Uri.parse(UriString);

        ImageView image = findViewById(R.id.imageView2);
        image.setImageURI(imageUri);

        ToggleButton descTagsToggle = findViewById(R.id.toggleDescTags);

        ImageButton editDescription = findViewById(R.id.editDescription);
        ImageButton addTagToPhoto = findViewById(R.id.addTagToPhoto);

        addTagToPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditTagsDialog();
            }
        });

        Button saveButton = findViewById(R.id.savePhotoChangesButton);

        Button editButton = findViewById(R.id.imageEdit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editButton.setVisibility(View.INVISIBLE);
                image.setVisibility(View.INVISIBLE);
                descTagsToggle.setVisibility(View.VISIBLE);
                saveButton.setVisibility(View.VISIBLE);
                if(descTagsToggle.isChecked()){
                    addTagToPhoto.setVisibility(View.VISIBLE);
                }else{
                    editDescription.setVisibility(View.VISIBLE);
                }
            }
        });

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        FlexboxLayout tagsContainer = findViewById(R.id.tagsContainer);

        descTagsToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (descTagsToggle.isChecked()){
                    addTagToPhoto.setVisibility(View.VISIBLE);
                    editDescription.setVisibility(View.INVISIBLE);
                    //tags
                    tagsContainer.setVisibility(View.VISIBLE);
                    tagsContainer.removeAllViews();


                    TextView tagName = new TextView(photo_view.this);
                    tagName.setText("New Item");
                    tagName.setTextSize(16);
                    tagName.setBackgroundColor(getResources().getColor(android.R.color.white));
                    tagName.setTextColor(getResources().getColor(android.R.color.black));

                    // Create a new ImageView
                    ImageView imageView = new ImageView(photo_view.this);
                    imageView.setImageResource(R.drawable.baseline_delete_24); // Replace with your image
                    imageView.setLayoutParams(new FlexboxLayout.LayoutParams(100, 100)); // Adjust dimensions as needed

                    // Add both views to the FlexboxLayout
                    FlexboxLayout.LayoutParams textLayoutParams = new FlexboxLayout.LayoutParams(
                            FlexboxLayout.LayoutParams.WRAP_CONTENT,
                            FlexboxLayout.LayoutParams.WRAP_CONTENT
                    );
                    textLayoutParams.setMargins(8, 8, 8, 8); // Add margins for spacing
                    tagName.setLayoutParams(textLayoutParams);

                    FlexboxLayout.LayoutParams imageLayoutParams = new FlexboxLayout.LayoutParams(
                            FlexboxLayout.LayoutParams.WRAP_CONTENT,
                            FlexboxLayout.LayoutParams.WRAP_CONTENT
                    );
                    imageLayoutParams.setMargins(8, 8, 8, 8);
                    imageView.setLayoutParams(imageLayoutParams);

                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });

                    // Add views to Flexbox
                    tagsContainer.addView(tagName);
                    tagsContainer.addView(imageView);

                }
                else{
                    editDescription.setVisibility(View.VISIBLE);
                    addTagToPhoto.setVisibility(View.INVISIBLE);
                    //description
                    tagsContainer.setVisibility(View.INVISIBLE);
                }
            }
        });

        editDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDescriptionDialog();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImageAndReturn();
            }
        });


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
        builder.setPositiveButton("Add Custom Tag", (dialog, which) -> {
            showAddCustomTagDialog();
        });
        builder.setNegativeButton("OK", (dialog, which) -> {
            // Handle OK click
            StringBuilder selectedTags = new StringBuilder();
            for (int i = 0; i < checkedTags.length; i++) {
                if (checkedTags[i]) {
                    selectedTags.append(tagsArray[i]).append(", ");
                }
            }
            if (selectedTags.length() > 0) {
                selectedTags.setLength(selectedTags.length() - 2); // Remove the trailing comma and space
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
        Intent intentSelf = getIntent();
        String uriString = intentSelf.getStringExtra("Uri");
        Uri newImageUri = Uri.parse(uriString);

            if (newImageUri == null) {
                Toast.makeText(this, "Image URI is invalid", Toast.LENGTH_SHORT).show();
                return;
            }

            String filename = null;

            if ("content".equals(newImageUri.getScheme())) {
                // For content scheme Uris (e.g., from ContentResolver)
                try (Cursor cursor = this.getContentResolver().query(newImageUri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        if (nameIndex != -1) {
                            filename = cursor.getString(nameIndex);
                        }
                    }
                }
            } else if ("file".equals(newImageUri.getScheme())) {
                // For file scheme Uris
                filename = new File(newImageUri.getPath()).getName();
            }

            // Create a unique filename for the image
            /*
            String mimeType = getContentResolver().getType(newImageUri);
            String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
            String filename = UUID.randomUUID().toString() + "." + (extension != null ? extension : "jpg");
            */

            File directory = getFilesDir();
            File file = new File(directory, filename);

            // Copy the image from the URI to internal storage
            /*
            try (FileOutputStream out = new FileOutputStream(file);
                 java.io.InputStream in = getContentResolver().openInputStream(newImageUri)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            }

             */
            // Gather selected tags
            if (tagsList == null || tagsList.isEmpty()) {
                Toast.makeText(this, "No tags selected", Toast.LENGTH_SHORT).show();
                return;
            }


            List<String> selectedTags = new ArrayList<>(tagsList); // Ensure valid list
            // Save metadata
            MetadataUtils.saveImageMetadata(this, file.getAbsolutePath(), selectedTags);
            // Return to the main screen
            Intent intent = new Intent(photo_view.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            Log.d("saveImageAndReturn", "Image saved successfully: " + filename);
            finish();
        }
    }



