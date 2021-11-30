package com.capstone.espasyoadmin.admin.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.espasyoadmin.R;
import com.capstone.espasyoadmin.admin.repository.FirebaseConnection;
import com.capstone.espasyoadmin.models.Landlord;
import com.capstone.espasyoadmin.models.Property;
import com.capstone.espasyoadmin.models.VerificationRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class VerifiedRequestDetailsActivity extends AppCompatActivity {

    private FirebaseConnection firebaseConnection;
    private FirebaseFirestore database;

    //verification request object
    private VerificationRequest verificationRequest;
    //property id and object
    private String propertyID;
    private Property property;

    private TextView displayStatus, displayClassification, displayDateSubmitted, displayDateVerified, displayPropertyName, displayPropertyType, displayPropertyAddress, displayProprietorName, displayLandlordName, displayLandlordPhoneNumber;
    private ImageView displayBusinessPermit;

    //verificaiton status
    private final String VERIFIED = "Verified";
    private final String UNVERIFIED = "Unverified";
    private final String DECLINED = "Declined";
    //verification classification
    private final String NEW = "New";
    private final String RENEW = "Renew";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_verified_request_details);

        firebaseConnection = FirebaseConnection.getInstance();
        database = firebaseConnection.getFirebaseFirestoreInstance();

        initializeViews();
        Intent intent = getIntent();
        getDataFromIntent(intent);
        getPropertyFromDatabase();

        displayBusinessPermit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VerifiedRequestDetailsActivity.this, PreviewImageActivity.class);
                intent.putExtra("previewImage",verificationRequest.getMunicipalBusinessPermitImageURL());
                startActivity(intent);
            }
        });
    }

    public void initializeViews() {
        //textviews
        displayStatus = findViewById(R.id.displayStatus_verified);
        displayClassification = findViewById(R.id.displayClassification_verified);
        displayDateSubmitted = findViewById(R.id.displayDateSubmitted_verified);
        displayDateVerified = findViewById(R.id.displayDateVerified_verified);
        displayPropertyName = findViewById(R.id.displayPropertyName_verified);
        displayPropertyType = findViewById(R.id.displayPropertyType_verified);
        displayPropertyAddress = findViewById(R.id.displayPropertyAddress_verified);
        displayProprietorName = findViewById(R.id.displayProprietorName_verified);
        displayLandlordName = findViewById(R.id.displayLandlordName_verified);
        displayLandlordPhoneNumber = findViewById(R.id.displayLandlordPhoneNumber_verified);

        //imageviews
        displayBusinessPermit = findViewById(R.id.displayBusinessPermitImageView_verified);

        //buttons
/*        btnVerifyVerificationRequest = findViewById(R.id.btnVerifyVerificationRequest);
        btnDeclineVerificationRequest = findViewById(R.id.btnDeclineVerificationRequest);*/
    }

    public void getDataFromIntent(Intent intent) {
        verificationRequest = intent.getParcelableExtra("verificationRequest");
    }

    public void getPropertyFromDatabase() {
        propertyID = verificationRequest.getPropertyID();
        DocumentReference propertyDocRef = database.collection("properties").document(propertyID);

        propertyDocRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        property = documentSnapshot.toObject(Property.class);
                        displayVerificationDetails();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(VerifiedRequestDetailsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void displayVerificationDetails() {

        //data from verification
        String status = verificationRequest.getStatus();
        String classification = verificationRequest.getClassification();
        String dateSubmitted = verificationRequest.getDateSubmitted();
        String dateVerified = verificationRequest.getDateVerified();
        String businessPermitImageURL = verificationRequest.getMunicipalBusinessPermitImageURL();

        //data from property
        String propertyName = property.getName();
        String propertyType = property.getPropertyType();
        String propertyAddress = property.getAddress();
        String proprietorName = property.getProprietorName();
        String landlordID = property.getOwner();

        //display all data
        getLandlord(landlordID);

        if (status.equals("verified")) {
            displayStatus.setText(VERIFIED);
            displayStatus.setTextColor(this.getResources().getColor(R.color.espasyo_green_200));
        } else if (status.equals("unverified")) {
            displayStatus.setText(UNVERIFIED);
            displayStatus.setTextColor(this.getResources().getColor(R.color.espasyo_red_200));
        }

        if (classification.equals("new")) {
            displayClassification.setText(NEW);
        } else if (classification.equals("renew")) {
            displayClassification.setText(RENEW);
        }

        displayDateSubmitted.setText(dateSubmitted);
        displayDateVerified.setText(dateVerified);
        displayPropertyName.setText(propertyName);
        displayPropertyType.setText(propertyType);
        displayPropertyAddress.setText(propertyAddress);
        displayProprietorName.setText(proprietorName);


        //will display the image of municipal business permit based on the newly picked municipal business permit image
        Picasso.get()
                .load(businessPermitImageURL)
                .placeholder(R.drawable.img_verified_verification_requests)
                .into(displayBusinessPermit);
    }

    public void getLandlord(String landlordID) {
        DocumentReference landlordDocRef = database.collection("landlords").document(landlordID);
        landlordDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Landlord landlord = documentSnapshot.toObject(Landlord.class);
                displayLandlordDetails(landlord);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(VerifiedRequestDetailsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void displayLandlordDetails(Landlord landlord) {
        String landlordName = landlord.getFirstName() + " " + landlord.getLastName();
        String landlordPhoneNumber = landlord.getPhoneNumber();

        displayLandlordName.setText(landlordName);
        displayLandlordPhoneNumber.setText(landlordPhoneNumber);
    }

}