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
import android.widget.Toast;

import com.capstone.espasyoadmin.R;
import com.capstone.espasyoadmin.admin.CustomDialogs.CustomProgressDialog;
import com.capstone.espasyoadmin.admin.repository.FirebaseConnection;
import com.capstone.espasyoadmin.models.Admin;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminChangeNameActivity extends AppCompatActivity {

    private FirebaseConnection firebaseConnection;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore database;

    private TextInputLayout textInputFirstNameLayout, textInputLastNameLayout;
    private TextInputEditText textInputFirstName, textInputLastName;
    private Button btnChangeName, btnCancelChangeName;
    private CustomProgressDialog progressDialog;

    //admin object
    private Admin admin;
    //admin current firstName and lastName
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_admin_change_name);

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
                        //updated name in admin object
                        admin.setFirstName(fName);
                        admin.setLastName(lName);
                        updateName(admin);
                    } else {
                        Toast.makeText(AdminChangeNameActivity.this, "Name is not changed", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(AdminChangeNameActivity.this, "Inputs Invalid", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void initializeViews() {
        //textInputLayouts
        textInputFirstNameLayout = findViewById(R.id.text_input_firstname_layout_changeName);
        textInputLastNameLayout = findViewById(R.id.text_input_lastname_layout_changeName);

        //textInputEditText
        textInputFirstName = findViewById(R.id.text_input_firstname_changeName);
        textInputLastName = findViewById(R.id.text_input_lastname_changeName);

        //button
        btnChangeName = findViewById(R.id.btnChangeName);
        btnCancelChangeName = findViewById(R.id.btnCancelChangeName);

        //custom dialog
        progressDialog = new CustomProgressDialog(AdminChangeNameActivity.this);
    }

    public void getDataFromIntent(Intent intent) {
        admin = intent.getParcelableExtra("admin");

        firstName = admin.getFirstName();
        lastName = admin.getLastName();

        //display firstname and lastname to textEdit
        textInputFirstName.setText(firstName);
        textInputLastName.setText(lastName);
    }

    // Functions
    // ------ input validations -------------------------------
    public final String TAG = "TESTING";

    //TODO: Check logic errors on isEmpty validations

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

    public void updateName(Admin updatedAdmin) {
        progressDialog.showProgressDialog("Updating Name...", false);

        String adminID = admin.getAdminID();
        DocumentReference userDocRef = database.collection("users").document(adminID);
        DocumentReference adminDocRef = database.collection("admins").document(adminID);

        //update name in  users collection
        userDocRef.set(updatedAdmin).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //update name in admin collection
                adminDocRef.set(updatedAdmin).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismissProgressDialog();
                                    Toast.makeText(AdminChangeNameActivity.this, "Name Successfully Updated", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        }, 3000);
                    }
                });
            }
        });
    }
}