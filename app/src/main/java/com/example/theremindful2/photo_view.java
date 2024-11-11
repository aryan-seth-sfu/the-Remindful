package com.example.theremindful2;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class photo_view extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstaceState){
        super.onCreate(savedInstaceState);
        setContentView(R.layout.photo_view);

        Intent intent = getIntent();
        String UriString = intent.getStringExtra("Uri");
        Uri imageUri = Uri.parse(UriString);

        ImageView image = findViewById(R.id.imageView2);
        image.setImageURI(imageUri);

        // Enable the action bar back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        // edit description button
        //TODO: create a way to change description to a user's input
        Button editDescription = findViewById(R.id.DescriptionButton);
        editDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView description = findViewById(R.id.photoDescription);
                description.setText("");
            }
        });

    }
    public boolean onSupportNavigateUp() {
        // Handles the action when the back button is pressed
        onBackPressed();
        return true;
    }
}
