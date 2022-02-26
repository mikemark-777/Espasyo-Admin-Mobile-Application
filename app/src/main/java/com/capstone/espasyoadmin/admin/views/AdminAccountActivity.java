package com.capstone.espasyoadmin.admin.views;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.espasyoadmin.MainActivity;
import com.capstone.espasyoadmin.R;
import com.capstone.espasyoadmin.admin.CustomDialogs.CustomProgressDialog;
import com.capstone.espasyoadmin.admin.repository.FirebaseConnection;
import com.capstone.espasyoadmin.auth.viewmodels.AuthViewModel;
import com.capstone.espasyoadmin.models.Admin;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminAccountActivity extends AppCompatActivity {


    private FirebaseConnection firebaseConnection;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore database;

    private Admin admin;

    private AuthViewModel viewModel;
    public static final String SHARED_PREFS = "sharedPrefsAdmin";
    public static final String USER_ROLE = "userRole";

    private ImageView exitAdminAccountPage;
    private TextView displayAdminName, displayAdminEmail;
    private CardView btnChangeName, btnChangePassword, btnLogout;
    private CustomProgressDialog progressDialog;

    private ActivityResultLauncher<Intent> ChangeNameActivityResultLauncher;
    private ActivityResultLauncher<Intent> ChangePasswordActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_admin_account);

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        //initialize firebase
        firebaseConnection = FirebaseConnection.getInstance();
        firebaseAuth = firebaseConnection.getFirebaseAuthInstance();
        database = firebaseConnection.getFirebaseFirestoreInstance();

        initializeViews();
        getAdminAccountData();

/*        btnChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminAccountActivity.this, AdminChangeNameActivity.class);
                intent.putExtra("admin", admin);
                startActivity(intent);
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminAccountActivity.this, AdminChangePasswordActivity.class);
                intent.putExtra("admin", admin);
                startActivity(intent);
            }
        });*/

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.showProgressDialog("Logging out...", false);
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog.isShowing()) {
                            removeUserRolePreference();
                            viewModel.signOut();
                            Intent intent = new Intent(AdminAccountActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                }, 3000);
            }
        });

        exitAdminAccountPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //will handle the result if the admin has reset his name or not
        ChangeNameActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            getAdminAccountData();
                        }
                    }
                });

        //will handle the result if the admin has reset his password or not
        ChangePasswordActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            getAdminAccountData();
                        }
                    }
                });
    }

    public void initializeViews() {
        //imageViews
        exitAdminAccountPage = findViewById(R.id.exitAdminAccountPage);
        //textviews
        displayAdminName = findViewById(R.id.displayAdminName_account);
        displayAdminEmail = findViewById(R.id.displayAdminEmail_account);

        //cardviews
        //btnChangeName = findViewById(R.id.btnChangeName_account);
        //btnChangePassword = findViewById(R.id.btnChangePassword_account);
        btnLogout = findViewById(R.id.btnLogout_account);

        //progress bars
        progressDialog = new CustomProgressDialog(AdminAccountActivity.this);
    }

    public void getAdminAccountData() {

        String adminID = firebaseAuth.getCurrentUser().getUid();
        DocumentReference adminDocRef = database.collection("admins").document(adminID);
        adminDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                admin = documentSnapshot.toObject(Admin.class);
                displayAdminData(admin);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminAccountActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void displayAdminData(Admin admin) {
        String adminName = admin.getFirstName() + " " + admin.getLastName();
        String adminEmail = admin.getEmail();

        displayAdminName.setText(adminName);
        displayAdminEmail.setText(adminEmail);
    }

    //remove USER_ROLE in sharedPreferences
    public void removeUserRolePreference() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove(USER_ROLE);
        editor.apply();
    }

    public int getUserRole() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        int userRole = sharedPreferences.getInt(USER_ROLE, 0);

        return userRole;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getAdminAccountData();
    }
}