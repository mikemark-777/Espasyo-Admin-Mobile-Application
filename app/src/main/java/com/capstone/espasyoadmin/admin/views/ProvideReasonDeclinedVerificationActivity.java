package com.capstone.espasyoadmin.admin.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.capstone.espasyoadmin.R;
import com.capstone.espasyoadmin.admin.CustomDialogs.SetInappropriateContentDetailsDialog;

import java.util.ArrayList;

public class ProvideReasonDeclinedVerificationActivity extends AppCompatActivity implements SetInappropriateContentDetailsDialog.ConfirmSetInappropriateContentDetailsListener {


    private CheckBox reason1CheckBox, reason2CheckBox, reason3CheckBox, reason4CheckBox;
    private EditText editTextOtherReason;
    private Button btnConfirmDeclineVerificaiton, btnCancelDeclineVerification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_provide_reason_declined_verification);

        initializeViews();
        btnConfirmDeclineVerificaiton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (areReasonsBlank()) {
                    Toast.makeText(ProvideReasonDeclinedVerificationActivity.this, "Please choose a reason", Toast.LENGTH_SHORT).show();
                } else {
                    confirmDeclineVerification();
                }
            }
        });

        btnCancelDeclineVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        reason2CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    showSetInappropriateContentDetailsDialog();
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

        //edit text for other decline reasons
        editTextOtherReason = findViewById(R.id.editTextOtherReasonDecline);

        //buttons for confirming and canceling decline
        btnConfirmDeclineVerificaiton = findViewById(R.id.btnConfirmDeclineVerification);
        btnCancelDeclineVerification = findViewById(R.id.btnCancelDeclineVerification);

    }

    public ArrayList<String> getReasons() {

        ArrayList<String> reasons = new ArrayList<>();

        String reason = "";
        if (reason1CheckBox.isChecked()) {
            reasons.add(reason1CheckBox.getText().toString());
            //reason += "- " + reason1CheckBox.getText().toString() + "\n";
        }

        if (reason2CheckBox.isChecked()) {
            reasons.add(reason2CheckBox.getText().toString());
            // += "- " + reason2CheckBox.getText().toString() + "\n";
        }

        if (reason3CheckBox.isChecked()) {
            reasons.add(reason3CheckBox.getText().toString());
            //reason += "- " + reason3CheckBox.getText().toString() + "\n";
        }

        if (reason4CheckBox.isChecked()) {
            reasons.add(reason4CheckBox.getText().toString());
           // reason += "- " + reason4CheckBox.getText().toString() + "\n";
        }

        String otherReason = editTextOtherReason.getText().toString();
        if (otherReason.equals("")) {
            return reasons;
        } else {
            reasons.add(otherReason);
            return reasons;
        }
    }

    public boolean areReasonsBlank() {
        ArrayList<String> reasons = getReasons();
        if (reasons.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public void confirmDeclineVerification() {
        Intent intent = new Intent();
        intent.putExtra("reasons", getReasons());
        setResult(RESULT_OK, intent);
        finish();
    }

    public void showSetInappropriateContentDetailsDialog() {
        SetInappropriateContentDetailsDialog setInappropriateContentDetailsDialog = new SetInappropriateContentDetailsDialog();
        setInappropriateContentDetailsDialog.show(getSupportFragmentManager(), "setInappropriateContentDetailsDialog");
    }


    @Override
    public void getConfirmedInappropriateContentDetails(String inappropriateContentDetails) {
        
    }

    @Override
    public void cancelSetInappropriateContentDetails() {
        Toast.makeText(ProvideReasonDeclinedVerificationActivity.this, "Cancelled Set Inappropriate Content Details", Toast.LENGTH_SHORT).show();
        reason2CheckBox.setChecked(false);
    }
}