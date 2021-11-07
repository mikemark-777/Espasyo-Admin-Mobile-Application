package com.capstone.espasyoadmin.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.capstone.espasyoadmin.R;
import com.capstone.espasyoadmin.admin.views.PropertyMasterListActivity;

public class AdminMainActivity extends AppCompatActivity {

    private ImageView btnIconGotoProfile;

    private CardView btnGotoPropertyMasterList,
                     btnGotoPropertyOnMap,
                     btnGotoVerificationRequests;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity__main);

        initializeViews();

        btnGotoPropertyMasterList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPropertyMasterList();
            }
        });

        btnIconGotoProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPropertyMasterList();
            }
        });

    }

    public void initializeViews() {
        //imageViews
        btnIconGotoProfile = findViewById(R.id.btnIconGotoProfile);

        //cardViews
        btnGotoPropertyMasterList = findViewById(R.id.btnGotoPropertyMasterlist);
        btnGotoPropertyOnMap = findViewById(R.id.btnGotoPropertyOnMap);
        btnGotoVerificationRequests = findViewById(R.id.btnGotoVerificaitonRequests);
    }

    public void gotoPropertyMasterList() {
        Intent intent = new Intent(AdminMainActivity.this, PropertyMasterListActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}