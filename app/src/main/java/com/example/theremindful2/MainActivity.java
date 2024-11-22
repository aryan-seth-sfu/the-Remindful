package com.example.theremindful2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import android.widget.ImageButton;
import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.DialogFragment;

public class MainActivity extends AppCompatActivity {

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

        // Daily Task Feature
        FloatingActionButton fabTaskBook = findViewById(R.id.fabTaskBook);
        fabTaskBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the TaskDialogFragment
                DialogFragment taskDialog = new TaskDialogFragment();
                taskDialog.show(getSupportFragmentManager(), "TaskDialog");
            }
        });
    }
}
