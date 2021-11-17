package com.capstone.espasyoadmin.admin.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.capstone.espasyoadmin.R;

public class DeclineVerificationRequestActivity extends AppCompatActivity {


    private CheckBox reason1CheckBox,
            reason2CheckBox,
            reason3CheckBox,
            reason4CheckBox,
            reason5CheckBox;

    private EditText editTextOtherReason;

    private Button btnConfirmDeclineVerificaiton,
            btnCancelDeclineVerification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_decline_verification_request);

        initializeViews();
        btnConfirmDeclineVerificaiton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(areReasonsBlank()) {
                    Toast.makeText(DeclineVerificationRequestActivity.this, "Reasons Blank", Toast.LENGTH_SHORT).show();
                } else {
                    confirmDeclineVerification();
                }
            }
        });
    }

    public void initializeViews() {
        //checkboxes for decline reasons
        reason1CheckBox = findViewById(R.id.declinedReason1);
        reason2CheckBox = findViewById(R.id.declinedReason2);
        reason3CheckBox = findViewById(R.id.declinedReason3);
        reason4CheckBox = findViewById(R.id.declinedReason4);
        reason5CheckBox = findViewById(R.id.declinedReason5);

        //edit text for other decline reasons
        editTextOtherReason = findViewById(R.id.editTextOtherReasonDecline);

        //buttons for confirming and canceling decline
        btnConfirmDeclineVerificaiton = findViewById(R.id.btnConfirmDeclineVerification);
        btnCancelDeclineVerification = findViewById(R.id.btnCancelDeclineVerification);

    }

    public String getReasons() {
        String reason = "";
        if(reason1CheckBox.isChecked()) {
            reason += reason1CheckBox.getText().toString() + "\n";
        }

        if(reason2CheckBox.isChecked()) {
            reason += reason2CheckBox.getText().toString() + "\n";
        }

        if(reason3CheckBox.isChecked()) {
            reason += reason3CheckBox.getText().toString() + "\n";
        }

        if(reason4CheckBox.isChecked()) {
            reason += reason4CheckBox.getText().toString() + "\n";
        }

        if(reason5CheckBox.isChecked()) {
            reason += reason5CheckBox.getText().toString() + "\n";
        }

        String otherReason = editTextOtherReason.getText().toString();
        if(otherReason.equals("")) {
            return reason;
        } else {
            return reason + otherReason + "\n";
        }
    }

    public boolean areReasonsBlank() {
        String reasons = getReasons();
        if(reasons.equals("")) {
            return true;
        } else {
            return false;
        }
    }

    public void confirmDeclineVerification() {
        Intent intent = new Intent();
        intent.putExtra("reason", getReasons());
        setResult(RESULT_OK, intent);
        finish();
    }


}