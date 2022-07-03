package com.capstone.espasyoadmin.admin.views;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.espasyoadmin.R;
import com.capstone.espasyoadmin.admin.repository.FirebaseConnection;
import com.capstone.espasyoadmin.models.Landlord;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewLandlordInformationActivity extends AppCompatActivity {

    private FirebaseConnection firebaseConnection;
    private FirebaseFirestore database;

    private TextView firstNameDisplayTextView, lastNameDisplayTextView, emailDisplayTextView, phoneNumberDisplayTextView;
    private Button btnChangeName;
    private ImageView btnBackToLandlordListActivity;


    private Landlord landlord;

    private ActivityResultLauncher<Intent> ChangeNameActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_view_landlord_information);

        //initialize firebase
        firebaseConnection = FirebaseConnection.getInstance();
        database = firebaseConnection.getFirebaseFirestoreInstance();

        initializeViews();
        Intent intent = getIntent();
        landlord = intent.getParcelableExtra("landlord");

        loadLandlordData(landlord);
        btnChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoChangeName();
            }
        });

        btnBackToLandlordListActivity.setOnClickListener(new View.OnClickListener() {
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
                            reloadLandlordInformation();
                            Toast.makeText(ViewLandlordInformationActivity.this, "Landlord name has been changed", Toast.LENGTH_SHORT).show();
                        } else if (result.getResultCode() == Activity.RESULT_CANCELED){
                            Toast.makeText(ViewLandlordInformationActivity.this, "Landlord name not changed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void loadLandlordData(Landlord landlord) {

        String firstName = landlord.getFirstName();
        String lastName = landlord.getLastName();
        String email = landlord.getEmail();
        String phoneNumber = landlord.getPhoneNumber();

        firstNameDisplayTextView.setText(firstName);
        lastNameDisplayTextView.setText(lastName);
        emailDisplayTextView.setText(email);
        phoneNumberDisplayTextView.setText(phoneNumber);
    }

    public void initializeViews() {
        btnChangeName = findViewById(R.id.btn_landlord_information_changeName);
        btnBackToLandlordListActivity = findViewById(R.id.btn_back_to_landlordListActivity);

        firstNameDisplayTextView = findViewById(R.id.landlord_information_firstname_display);
        lastNameDisplayTextView = findViewById(R.id.landlord_information_lastname_display);
        emailDisplayTextView  = findViewById(R.id.landlord_information_email_display);
        phoneNumberDisplayTextView  = findViewById(R.id.landlord_information_phoneNumber_display);
    }

    public void gotoChangeName() {
        Intent intent = new Intent(ViewLandlordInformationActivity.this, EditLandlordNameActivity.class);
        intent.putExtra("landlord", landlord);
        ChangeNameActivityResultLauncher.launch(intent);
    }

    public void reloadLandlordInformation() {

        String landlordID = landlord.getLandlordID();
        DocumentReference landlordDocRef = database.collection("landlords").document(landlordID);

        landlordDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                landlord = documentSnapshot.toObject(Landlord.class);
                loadLandlordData(landlord);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ViewLandlordInformationActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}