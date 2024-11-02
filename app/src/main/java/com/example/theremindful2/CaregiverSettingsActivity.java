package com.example.theremindful2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.theremindful2.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CaregiverSettingsActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private Uri imageUri;
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_settings);

        // Enable the action bar back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        // Upload button -> with working image pick from device
        Button uploadButton = (Button)findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CaregiverSettingsActivity.this,"Upload Button Pressed",Toast.LENGTH_SHORT).show();
                openImagePicker();
            }
        });
        // New Album
        //TODO: create a new album
        Button newAlbum = (Button)findViewById(R.id.newAlbumsButton);
        newAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CaregiverSettingsActivity.this,"Album Button Pressed",Toast.LENGTH_SHORT).show();
            }
        });
        //Make all image preview clickable to go to photo preview
        LinearLayout photoView = findViewById(R.id.layoutUpload);
        for(int images = 0; images < photoView.getChildCount(); images++){
            View view = photoView.getChildAt(images);
            if(view instanceof ImageView){
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(CaregiverSettingsActivity.this,"image Button Pressed",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CaregiverSettingsActivity.this, photo_view.class);
                        startActivity(intent);

                    }
                });
            }
        }
        //Make all albums(album images) clickable to go to album preview images
        LinearLayout albumView = findViewById(R.id.layoutAlbums);
        for(int images = 0; images < albumView.getChildCount(); images++){
            View view = albumView.getChildAt(images);
            if(view instanceof ImageView){
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(CaregiverSettingsActivity.this,"album Button Pressed",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CaregiverSettingsActivity.this, album.class);
                        startActivity(intent);
                    }
                });
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Handles the action when the back button is pressed
        onBackPressed();
        return true;
    }
    //Part of image picker from device
    private void openImagePicker(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }
    //Part of image picker from device
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            //TODO: Add image action for upload here!!
        }
    }
}