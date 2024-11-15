package com.example.theremindful2;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import android.content.Intent;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

//    private Button u_button;
//    Uri Image;
private ActivityResultLauncher<Intent> FilePickerLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Reference to the parent ViewPager2 for horizontal swiping between themes
        ViewPager2 parentViewPager = findViewById(R.id.parentViewPager);

        // Set the adapter for the parent ViewPager2
        parentViewPager.setAdapter(new ParentAdapter(this));

        // Set the orientation for horizontal swiping
        parentViewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        ImageButton menu = findViewById(R.id.menu_button);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Menu.class);
                startActivity(intent);

            }
        });


    }
}
