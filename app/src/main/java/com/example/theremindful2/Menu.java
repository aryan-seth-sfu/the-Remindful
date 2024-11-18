package com.example.theremindful2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class Menu extends AppCompatActivity {
    Uri audio;
    private ActivityResultLauncher<Intent> audioPickerLauncher;
    private Button audioButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        Button imagesSetting = findViewById(R.id.imageSettingButton);
        imagesSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Menu.this, CareGiverImagesSettingsActivity.class);
                startActivity(intent);
            }
        });
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button musicButton = findViewById(R.id.musicSettingButton);
        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent to start MusicUploadActivity
                Intent intent = new Intent(Menu.this, audioactivity.class);
                startActivity(intent);
            }
        });

        Button taskButton = findViewById(R.id.taskSettingButton);
        taskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment taskDialog = new TaskDialogFragment();
                taskDialog.show(getSupportFragmentManager(), "TaskDialog");
            }
        });
        audioPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), audioReturned-> {

                    if (audioReturned.getResultCode() == RESULT_OK){
                        Intent datafile = audioReturned.getData();
                        if (datafile != null){
                            audio = datafile.getData();


                        }

                    }
                }
        );


    }
}
