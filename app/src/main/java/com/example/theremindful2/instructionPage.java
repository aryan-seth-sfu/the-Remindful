package com.example.theremindful2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class instructionPage extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instruction_page);
        View TextView = findViewById(R.id.firstView);
        View TaskBarView = findViewById(R.id.imageView3);
        View AudioView = findViewById(R.id.ss1);
        View secondText = findViewById(R.id.secondView);
        View thirdText = findViewById(R.id.thirdView);
        View instructionofButtons = findViewById(R.id.buttonInstructions);
        View pausePlaySkipView = findViewById(R.id.playPauseButtonMiniView);
        View instructionImage = findViewById(R.id.fifthView);
        View imageInstructionImage = findViewById(R.id.imageFifth);

        Button nextButton = findViewById(R.id.nextbutton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (instructionofButtons.getVisibility() == View.VISIBLE){
                    thirdText.setVisibility(View.INVISIBLE);
                    instructionofButtons.setVisibility(View.INVISIBLE);
                    pausePlaySkipView.setVisibility(View.INVISIBLE);
                    instructionImage.setVisibility(View.VISIBLE);
                    imageInstructionImage.setVisibility(View.VISIBLE);
                }

                if (TextView.getVisibility() != View.VISIBLE && AudioView.getVisibility() == View.VISIBLE) {
                    AudioView.setVisibility(View.INVISIBLE);
                    secondText.setVisibility(View.INVISIBLE);
                    thirdText.setVisibility(View.VISIBLE);
                    instructionofButtons.setVisibility(View.VISIBLE);
                    pausePlaySkipView.setVisibility(View.VISIBLE);

                }
                if (TextView.getVisibility() == View.VISIBLE) {
                    TextView.setVisibility(View.INVISIBLE);
                    TaskBarView.setVisibility(View.INVISIBLE);
                    AudioView.setVisibility(View.VISIBLE);
                    secondText.setVisibility(View.VISIBLE);

                }




            }
        });

    }


}
