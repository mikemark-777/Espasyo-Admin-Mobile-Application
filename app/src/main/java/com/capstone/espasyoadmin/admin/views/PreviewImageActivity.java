package com.capstone.espasyoadmin.admin.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.capstone.espasyoadmin.R;
import com.squareup.picasso.Picasso;

public class PreviewImageActivity extends AppCompatActivity {

    private ImageView previewImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_preview_image);

        Intent intent = getIntent();
        previewImageView = findViewById(R.id.previewImageView);
        displayBusinessPermit(intent);

    }

    public void displayBusinessPermit(Intent intent) {

        String previewImageURL = intent.getStringExtra("previewImage");

        Picasso.get()
                .load(previewImageURL)
                .placeholder(R.drawable.img_gallery)
                .into(previewImageView);

        previewImageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        previewImageView.setAdjustViewBounds(true);
    }
}