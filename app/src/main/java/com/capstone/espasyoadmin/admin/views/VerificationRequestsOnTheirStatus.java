package com.capstone.espasyoadmin.admin.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.capstone.espasyoadmin.R;

public class VerificationRequestsOnTheirStatus extends AppCompatActivity {

    private CardView btnGotoVerifiedRequests, btnGotoUnverifiedRequests, btnGotoDeclinedRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_verification_requests_on_their_status);

        initializeViews();

        btnGotoVerifiedRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoVerifiedRequests();
            }
        });

        btnGotoUnverifiedRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUnverifiedRequests();
            }
        });

        btnGotoDeclinedRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoDeclinedRequests();
            }
        });
    }

    public void initializeViews() {
        //cardviews for navigation
        btnGotoVerifiedRequests = findViewById(R.id.btnGotoVerifiedRequests);
        btnGotoUnverifiedRequests = findViewById(R.id.btnGotoUnverifiedRequests);
        btnGotoDeclinedRequests = findViewById(R.id.btnGotoDeclinedRequests);
    }

    public void gotoVerifiedRequests() {
        Intent verifiedListIntent = new Intent(VerificationRequestsOnTheirStatus.this, VerifiedVerificationRequestsActivity.class);
        startActivity(verifiedListIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void gotoUnverifiedRequests() {
        Intent unverifiedListIntent = new Intent(VerificationRequestsOnTheirStatus.this, UnverifiedVerificationRequestsActivity.class);
        startActivity(unverifiedListIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void gotoDeclinedRequests() {
        Intent declinedListIntent = new Intent(VerificationRequestsOnTheirStatus.this, DeclinedVerificationRequestsActivity.class);
        startActivity(declinedListIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


}