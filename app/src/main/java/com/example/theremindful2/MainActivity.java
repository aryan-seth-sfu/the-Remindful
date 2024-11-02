package com.example.theremindful2;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import android.content.Intent;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

//    private Button u_button;
//    Uri Image;



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

        // Reference to the Floating Action Button to open Caregiver Settings
        FloatingActionButton fabSettings = findViewById(R.id.fabSettings);

        // Handle click on the Floating Action Button to open Caregiver Settings Activity
        fabSettings.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, com.example.theremindful2.CaregiverSettingsActivity.class);
            startActivity(intent);
        });

//        SwingUtilities.invokeLater(() -> {
//            ImageUploadExample example = new ImageUploadExample();
//            example.setVisible(true);
//        });




        //u_button = findViewById(R.id.upload_button);



//        u_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_PICK,
//                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
//                final int ACTIVITY_SELECT_IMAGE = 1234;
//                intent.setType("image/*");
//                //audioPickerLauncher.launch(Intent.createChooser(intent, "pick auido"));
////                startActivityForResult(intent, ACTIVITY_SELECT_IMAGE);
//            }
//        });

    }

//    public void startActivityForResult (Intent i, int intt) {
//        System.out.println("hello world");
//    }
// ActivityResultLauncher to start a launcer that gets a result (audio file) from it
// register for activity result will be a launcher that starts activity and deals with audio file
// new activitiy result contracts... starts activity fetches result
//private ActivityResultLauncher<Intent> audioPickerLauncher = registerForActivityResult(
//        new ActivityResultContracts.StartActivityForResult(), audioReturned-> {
//
//            if (audioReturned.getResultCode() == RESULT_OK){
//                Intent datafile = audioReturned.getData();
//                if (datafile != null){
//                    Image = datafile.getData();
//
//
//                }
//
//            }
//        }
//);


}
