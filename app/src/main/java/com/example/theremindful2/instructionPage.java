package com.example.theremindful2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class instructionPage extends AppCompatActivity {

    int flag =1;

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
        View tagpt1 = findViewById(R.id.tagpt1);
        View instructionImage = findViewById(R.id.fifthView);
        View imageInstructionImage = findViewById(R.id.imageFifth);
        View imagebrowse1 = findViewById(R.id.imagebrowse);
        View imagetext1 = findViewById(R.id.imageText);
        View otherView = findViewById(R.id.otherView);
        View selectaudio = findViewById(R.id.ss2);
        View dailytaskimage = findViewById(R.id.dailytaskimage);
        View dailyTaskText = findViewById(R.id.dailytaskText);
        View musicView = findViewById(R.id.buttonmusic);
        View musicTextView = findViewById(R.id.buttonInstructions);
        View playPauseButtonMiniView = findViewById(R.id.playPauseButtonMiniView);

        View analyticsview = findViewById(R.id.analyticsins);
        View anaylticsView = findViewById(R.id.analyticsText);
        Button nextButton = findViewById(R.id.nextbutton);
        Button doneButton = findViewById(R.id.donebutton);
        doneButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 if (playPauseButtonMiniView.getVisibility() == View.VISIBLE){
                    musicTextView.setVisibility(View.INVISIBLE);
                    playPauseButtonMiniView.setVisibility(View.INVISIBLE);
                    musicView.setVisibility(View.INVISIBLE);
                    musicTextView.setVisibility(View.INVISIBLE);
                    dailytaskimage.setVisibility(View.VISIBLE);
                    dailyTaskText.setVisibility(View.VISIBLE);
                    flag = 0;
                    return;
//                    doneButton.setVisibility(View.VISIBLE);
//                    nextButton.setVisibility(View.INVISIBLE);


                }
                else if (flag == 0) {
                    analyticsview.setVisibility(View.VISIBLE);
                    anaylticsView.setVisibility(View.VISIBLE);
                    dailytaskimage.setVisibility(View.INVISIBLE);
                    dailyTaskText.setVisibility(View.INVISIBLE);
                    doneButton.setVisibility(View.VISIBLE);
                    nextButton.setVisibility(View.INVISIBLE);
                    return;
                }

                else if (selectaudio.getVisibility()==View.VISIBLE){
                    otherView.setVisibility(View.INVISIBLE);
                    selectaudio.setVisibility(View.INVISIBLE);
                    musicView.setVisibility(View.VISIBLE);
                    musicTextView.setVisibility(View.VISIBLE);
                    playPauseButtonMiniView.setVisibility(View.VISIBLE);

                }
                else if (imagetext1.getVisibility() == View.VISIBLE){
                    imagebrowse1.setVisibility(View.INVISIBLE);
                    imagetext1.setVisibility(View.INVISIBLE);
                    otherView.setVisibility(View.VISIBLE);
                    selectaudio.setVisibility(View.VISIBLE);
                }
                else if (instructionImage.getVisibility() == View.VISIBLE){
                    instructionImage.setVisibility(View.INVISIBLE);
                    imageInstructionImage.setVisibility(View.INVISIBLE);
                    imagebrowse1.setVisibility(View.VISIBLE);
                    imagetext1.setVisibility(View.VISIBLE);
                }
                else if (instructionofButtons.getVisibility() == View.VISIBLE){
                    thirdText.setVisibility(View.INVISIBLE);
                    instructionofButtons.setVisibility(View.INVISIBLE);
                    tagpt1.setVisibility(View.INVISIBLE);
                    instructionImage.setVisibility(View.VISIBLE);
                    imageInstructionImage.setVisibility(View.VISIBLE);
                }

                else if (TextView.getVisibility() != View.VISIBLE && AudioView.getVisibility() == View.VISIBLE) {
                    AudioView.setVisibility(View.INVISIBLE);
                    secondText.setVisibility(View.INVISIBLE);
                    thirdText.setVisibility(View.VISIBLE);
                    instructionofButtons.setVisibility(View.VISIBLE);
                    tagpt1.setVisibility(View.VISIBLE);

                }
               else if (TextView.getVisibility() == View.VISIBLE) {
                    TextView.setVisibility(View.INVISIBLE);
                    TaskBarView.setVisibility(View.INVISIBLE);
                    AudioView.setVisibility(View.VISIBLE);
                    secondText.setVisibility(View.VISIBLE);

                }




            }
        });

    }


}
