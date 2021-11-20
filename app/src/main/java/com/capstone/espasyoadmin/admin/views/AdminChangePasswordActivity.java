package com.capstone.espasyoadmin.admin.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
import com.capstone.espasyoadmin.models.Admin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

public class AdminChangePasswordActivity extends AppCompatActivity {

    private FirebaseConnection firebaseConnection;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore database;

    private TextInputLayout textInputEmailLayout, textInputCurrentPasswordLayout, textInputNewPasswordLayout;
    private TextInputEditText textInputEmail, textInputCurrentPassword, textInputNewPassword;
    private Button btnChangePassword, btnCancelChangePassword;
    private ProgressBar changePasswordProgressBar;


    private boolean currentEmailExists = false;

    private Admin admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_admin_change_password);

        firebaseConnection = FirebaseConnection.getInstance();
        firebaseAuth = firebaseConnection.getFirebaseAuthInstance();
        database = firebaseConnection.getFirebaseFirestoreInstance();

        initializeViews();
        Intent intent = getIntent();
        getDataFromIntent(intent);

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = textInputEmail.getText().toString();
                String currentPassword = textInputCurrentPassword.getText().toString();
                String newPassword = textInputNewPassword.getText().toString();

                if(areInputsValid(email, currentPassword, newPassword)) {
                    checkIfEmailExist(email);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (currentEmailExists) {
                                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            showConfirmationDialog(email, currentPassword, newPassword);
                                        }
                                    }, 4000);
                            } else {
                                textInputEmailLayout.setError("Email do not exist");
                            }
                        }
                    }, 5000);
                } else {

                }
            }
        });

    }

    public void initializeViews() {
        //textInputLayouts
        textInputEmailLayout = findViewById(R.id.text_input_email_layout_changePassword);
        textInputCurrentPasswordLayout = findViewById(R.id.text_input_currentPassword_layout_changePassword);
        textInputNewPasswordLayout = findViewById(R.id.text_input_newPassword_layout_changePassword);

        //textInputEditText
        textInputEmail = findViewById(R.id.text_input_email_changePassword);
        textInputCurrentPassword= findViewById(R.id.text_input_currentPassword_changePassword);
        textInputNewPassword = findViewById(R.id.text_input_newPassword_changePassword);

        //button
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnCancelChangePassword = findViewById(R.id.btnCancelChangePassword);

        //progress bar
        changePasswordProgressBar = findViewById(R.id.changePasswordProgressBar);
    }

    public void getDataFromIntent(Intent intent) {
        admin = intent.getParcelableExtra("admin");
    }

    // Functions
    // ------ input validations -------------------------------

    private boolean isEmailAddressEmpty(String email) {
        if(email.isEmpty()) {
            textInputEmailLayout.setError("Email Address field cannot be empty");
            return false;
        } else {
            textInputEmailLayout.setError(null);
            return true;
        }
    }


    private boolean isCurrentPasswordValid(String currentPassword) {
        if(currentPassword.isEmpty()) {
            textInputCurrentPasswordLayout.setError("Current Password field cannot be empty");
            return false;
        } else {
            textInputCurrentPasswordLayout.setError(null);
            return true;
        }
    }

    private boolean isNewPasswordValid(String newPassword) {
        if(newPassword.isEmpty()) {
            textInputNewPasswordLayout.setError("New Password field cannot be empty");
            return false;
        } else {
            textInputNewPasswordLayout.setError(null);
            return true;
        }
    }

    private boolean areInputsValid(String emailAddress, String currentPassword, String newPassword) {
        boolean mailAddressResult = isEmailAddressEmpty(emailAddress);
        boolean currentPasswordResult = isCurrentPasswordValid(currentPassword);
        boolean newPasswordResult = isNewPasswordValid(newPassword);

        if(mailAddressResult == true && currentPasswordResult == true && newPasswordResult == true) {
            return true;
        } else {
            return false;
        }
    }


    public void showConfirmationDialog(String email, String currentPassword, String newPassword) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Change Password")
                .setMessage("Do your want to continue changing your password?")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updatePassword(email, currentPassword, newPassword);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    public void checkIfEmailExist(String email) {
        //check if newEmail already exists
        firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().getSignInMethods().size() == 0) {
                        currentEmailExists = false;
                    } else {
                        currentEmailExists = true;
                        //textInputCurrentEmailLayout.setError("Email already exists");
                    }
                } else {
                    Toast.makeText(AdminChangePasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    public void updatePassword(String email, String currentPassword, String newPassword ) {
        changePasswordProgressBar.setVisibility(View.VISIBLE);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(email, currentPassword);

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                  //set new password to admin Object
                                  admin.setPassword(newPassword);
                                  updatePasswordOnDatabase(admin, newPassword);
                                }
                            });
                        } else {
                            Toast.makeText(AdminChangePasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            changePasswordProgressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    public void updatePasswordOnDatabase(Admin updatedAdmin, String newPassword) {
        String adminID = updatedAdmin.getAdminID();
        //update admin password on users and admins collection
        DocumentReference usersDocRef = database.collection("users").document(adminID);
        DocumentReference adminDocRef = database.collection("admins").document(adminID);

        usersDocRef.set(updatedAdmin).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                adminDocRef.set(updatedAdmin).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        changePasswordProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(AdminChangePasswordActivity.this, "Password Successfully Updated", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                changePasswordProgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(AdminChangePasswordActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}