package com.example.theremindful2;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.flexbox.FlexboxLayout;

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

        ToggleButton descTagsToggle = findViewById(R.id.toggleDescTags);

        ImageButton editDescription = findViewById(R.id.editDescription);
        ImageButton addTagToPhoto = findViewById(R.id.addTagToPhoto);

        Button editButton = findViewById(R.id.imageEdit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editButton.setVisibility(View.INVISIBLE);
                image.setVisibility(View.INVISIBLE);
                descTagsToggle.setVisibility(View.VISIBLE);
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


    }

}
