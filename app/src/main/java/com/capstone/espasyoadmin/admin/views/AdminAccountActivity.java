package com.capstone.espasyoadmin.admin.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.espasyoadmin.R;
import com.capstone.espasyoadmin.admin.repository.FirebaseConnection;
import com.capstone.espasyoadmin.models.Admin;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminAccountActivity extends AppCompatActivity {

    private FirebaseConnection firebaseConnection;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore database;
    private Admin admin;

    private ImageView exitAdminAccountPage;

    private TextView displayAdminName,
            displayAdminEmail;

    private CardView btnChangeName,
            btnChangePassword,
            btnLogout,
            btnDeleteAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_admin_account);

        //initialize firebase
        firebaseConnection = FirebaseConnection.getInstance();
        firebaseAuth = firebaseConnection.getFirebaseAuthInstance();
        database = firebaseConnection.getFirebaseFirestoreInstance();

        initializeViews();
        getAdminAccountData();

        btnChangeName.setOnClickListener(new View.OnClickListener() {
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
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminAccountActivity.this, "Logout", Toast.LENGTH_SHORT).show();
            }
        });

        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminAccountActivity.this, "Delete Account", Toast.LENGTH_SHORT).show();
            }
        });

        exitAdminAccountPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
        btnChangeName = findViewById(R.id.btnChangeName_account);
        btnChangePassword = findViewById(R.id.btnChangePassword_account);
        btnLogout = findViewById(R.id.btnLogout_account);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount_account);
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

    @Override
    protected void onRestart() {
        super.onRestart();
        getAdminAccountData();
    }
}