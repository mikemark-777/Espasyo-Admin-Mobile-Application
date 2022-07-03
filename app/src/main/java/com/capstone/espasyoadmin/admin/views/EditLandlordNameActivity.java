package com.capstone.espasyoadmin.admin.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.capstone.espasyoadmin.R;
import com.capstone.espasyoadmin.admin.repository.FirebaseConnection;
import com.capstone.espasyoadmin.models.Landlord;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditLandlordNameActivity extends AppCompatActivity {


    private FirebaseConnection firebaseConnection;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore database;

    //landlord object
    private Landlord landlord;
    //landlord current firstName and lastName
    private String firstName;
    private String lastName;

    private TextInputLayout textInputFirstNameLayout, textInputLastNameLayout;
    private TextInputEditText textInputFirstName, textInputLastName;
    private Button btnChangeName, btnCancelChangeName;
    private ProgressBar landlordChangeNameProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_edit_landlord_name);

        firebaseConnection = FirebaseConnection.getInstance();
        firebaseAuth = firebaseConnection.getFirebaseAuthInstance();
        database = firebaseConnection.getFirebaseFirestoreInstance();

        initializeViews();
        Intent intent = getIntent();
        getDataFromIntent(intent);

        btnChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fName = textInputFirstName.getText().toString();
                String lName = textInputLastName.getText().toString();
                if (areInputsValid(fName, lName)) {
                    //check if has the same input as before
                    if (isNameChanged(firstName, lastName, fName, lName)) {
                        btnChangeName.setEnabled(false);
                        //updated name in landlord object
                        landlord.setFirstName(fName);
                        landlord.setLastName(lName);
                        updateLandlordName(landlord);
                    } else {
                        Toast.makeText(EditLandlordNameActivity.this, "Nothing to change", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                } else {
                    Toast.makeText(EditLandlordNameActivity.this, "Inputs Invalid", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancelChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    public void initializeViews() {
        //textInputLayouts
        textInputFirstNameLayout = findViewById(R.id.text_input_firstname_layout_landlordChangeName);
        textInputLastNameLayout = findViewById(R.id.text_input_lastname_layout_landlordChangeName);

        //textInputEditText
        textInputFirstName = findViewById(R.id.text_input_firstname_landlordChangeName);
        textInputLastName = findViewById(R.id.text_input_lastname_landlordChangeName);

        //button
        btnChangeName = findViewById(R.id.btnChangeLandlordName);
        btnCancelChangeName = findViewById(R.id.btnCancelChangeLandlordName);

        //progress bar
        landlordChangeNameProgressBar = findViewById(R.id.landlordChangeNameProgressBar);
    }

    public void getDataFromIntent(Intent intent) {
        landlord = intent.getParcelableExtra("landlord");

        firstName = landlord.getFirstName();
        lastName = landlord.getLastName();

        //display firstname and lastname to textEdit
        textInputFirstName.setText(firstName);
        textInputLastName.setText(lastName);
    }

    // Functions
    // ------ input validations -------------------------------
    public final String TAG = "TESTING";

    /* Check if firstName is empty */
    private Boolean isFirstNameValid(String firstName) {
        if (!firstName.isEmpty()) {
            textInputFirstNameLayout.setError(null);
            Log.d(TAG, "FIRSTNAME: NOT EMPTY");
            return true;
        } else {
            textInputFirstNameLayout.setError("First Name required");
            Log.d(TAG, "FIRSTNAME: EMPTY");
            return false;
        }
    }

    /* Check if lastName is empty */
    private Boolean isLastNameValid(String lastName) {
        if (!lastName.isEmpty()) {
            textInputLastNameLayout.setError(null);
            Log.d(TAG, "LASTNAME: NOT EMPTY");
            return true;
        } else {
            textInputLastNameLayout.setError("Last Name required");
            Log.d(TAG, "LASTNAME: EMPTY");
            return false;
        }
    }

    private Boolean areInputsValid(String firstName, String lastName) {

        boolean firstNameResult = isFirstNameValid(firstName);
        boolean lastNameResult = isLastNameValid(lastName);

        if (firstNameResult && lastNameResult) {
            Log.d(TAG, "CAN PROCEED: TRUE");
            return true;
        } else {
            Log.d(TAG, "CAN PROCEED: FALSE");
            return false;
        }
    }

    private Boolean isNameChanged(String currentFirstName, String currentLastName, String newFirstName, String newLastName) {
        return !currentFirstName.equals(newFirstName) || !currentLastName.equals(newLastName);
    }

    public void updateLandlordName(Landlord updatedLandlord) {
        landlordChangeNameProgressBar.setVisibility(View.VISIBLE);
        String landlordID = updatedLandlord.getLandlordID();
        DocumentReference landlordDocRef = database.collection("landlords").document(landlordID);

        //update name in  landlords collection
        landlordDocRef.set(updatedLandlord).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnChangeName.setEnabled(true);
                        landlordChangeNameProgressBar.setVisibility(View.INVISIBLE);
                        setResult(RESULT_OK);
                        finish();
                    }
                }, 3000);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                btnChangeName.setEnabled(true);
                landlordChangeNameProgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(EditLandlordNameActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}