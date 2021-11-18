package com.capstone.espasyoadmin.admin.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.capstone.espasyoadmin.R;
import com.capstone.espasyoadmin.models.VerificationRequest;

public class VerifiedRequestDetailsActivity extends AppCompatActivity {

    private VerificationRequest verificationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_verified_request_details);

        initializeViews();
        Intent intent = getIntent();
        getDataFromIntent(intent);

        Toast.makeText(VerifiedRequestDetailsActivity.this, "Verified Property: " + verificationRequest.getPropertyName(), Toast.LENGTH_SHORT).show();

    }

    public void initializeViews() {

    }

    public void getDataFromIntent(Intent intent) {
        verificationRequest = intent.getParcelableExtra("verificationRequest");
    }

}