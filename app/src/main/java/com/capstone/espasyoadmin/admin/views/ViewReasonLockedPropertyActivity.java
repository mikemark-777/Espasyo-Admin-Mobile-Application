package com.capstone.espasyoadmin.admin.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.capstone.espasyoadmin.R;
import com.capstone.espasyoadmin.models.Property;

import java.util.ArrayList;

public class ViewReasonLockedPropertyActivity extends AppCompatActivity {

    private Property property;
    private TextView displayReasonLocked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_view_reason_locked_property);

        initializeViews();
        Intent intent = getIntent();
        getDataFromIntent(intent);

    }

    public void initializeViews() {
        displayReasonLocked = findViewById(R.id.displayReason_locked);
    }

    public void getDataFromIntent(Intent intent) {
        property = intent.getParcelableExtra("property");
        ArrayList<String> reasonLocked = property.getReasonLocked();
        displayReason(reasonLocked);
    }

    public void displayReason(ArrayList<String> reasonLocked) {
        String concatReason = "";
        for(String reason : reasonLocked) {
            concatReason += "- " + reason + "\n";
        }

        displayReasonLocked.setText(concatReason);
    }
}