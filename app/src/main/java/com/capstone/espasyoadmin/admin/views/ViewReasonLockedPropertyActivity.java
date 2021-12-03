package com.capstone.espasyoadmin.admin.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.capstone.espasyoadmin.R;
import com.capstone.espasyoadmin.models.Property;

import java.util.ArrayList;

public class ViewReasonLockedPropertyActivity extends AppCompatActivity {

    private Property property;
    private TextView displayReasonLocked;
    private TextView displayPropertyName, displayPropertyAddress;
    private ImageView backToPropertyDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_view_reason_locked_property);

        initializeViews();
        Intent intent = getIntent();
        getDataFromIntent(intent);

        backToPropertyDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void initializeViews() {
        displayReasonLocked = findViewById(R.id.displayReason_locked);
        displayPropertyName = findViewById(R.id.displayPropertyName_locked);
        displayPropertyAddress = findViewById(R.id.displayPropertyAddress_locked);
        backToPropertyDetails = findViewById(R.id.backToPropertyDetails_locked);
    }

    public void getDataFromIntent(Intent intent) {
        property = intent.getParcelableExtra("property");
        displayDetails(property);
    }

    public void displayDetails(Property property) {
        //extract property name, address and reasonLocked
        ArrayList<String> reasonLocked = property.getReasonLocked();
        String propertyName = property.getName();
        String propertyAddress = property.getAddress();

        //display property name and address
        displayPropertyName.setText(propertyName);
        displayPropertyAddress.setText(propertyAddress);

        // display reason locked
        String concatReason = "";
        for(String reason : reasonLocked) {
            concatReason += "- " + reason + "\n";
        }
        displayReasonLocked.setText(concatReason);
    }
}