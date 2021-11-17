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
import com.capstone.espasyoadmin.models.Property;
import com.capstone.espasyoadmin.models.VerificationRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class CheckVerificationRequestActivity extends AppCompatActivity {

    private FirebaseConnection firebaseConnection;
    private FirebaseFirestore database;

    //verification request object
    private VerificationRequest verificationRequest;
    //property id and object
    private String propertyID;
    private Property property;

    private TextView displayStatus,
            displayClassification,
            displayDateSubmitted,
            displayPropertyName,
            displayPropertyType,
            displayPropertyAddress,
            displayProprietorName,
            displayLandlordName,
            displayLandlordPhoneNumber;

    private ImageView displayBusinessPermit;

    private Button btnVerifyVerificationRequest,
                   btnDeclineVerificationRequest;

    private ActivityResultLauncher<Intent> DeclineVerificationActivityResultLauncher;

    private final String VERIFIED = "Verified";
    private final String UNVERIFIED = "Unverified";
    private final String DECLINED = "Declined";
    
    private final String NEW = "New";
    private final String RENEW = "Renew";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_check_verification_request);

        firebaseConnection = FirebaseConnection.getInstance();
        database = firebaseConnection.getFirebaseFirestoreInstance();

        initializeViews();
        Intent intent = getIntent();
        getDataFromIntent(intent);
        getPropertyFromDatabase();

        //will handle all the data from the DeclineVerificationRequestActivity
        DeclineVerificationActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                          String reason = result.getData().getStringExtra("reason");
                            setDeclinedVerificationDescription(reason);
                        } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                            Toast.makeText(CheckVerificationRequestActivity.this, "Declined Unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        displayBusinessPermit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String municipalBPUrl = verificationRequest.getMunicipalBusinessPermitImageURL();
                Intent intent = new Intent(CheckVerificationRequestActivity.this, PreviewImageActivity.class);
                intent.putExtra("previewImage", municipalBPUrl);
                startActivity(intent);
            }
        });

        btnVerifyVerificationRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyRequest();
            }
        });

        btnDeclineVerificationRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeclineRequestDescription();
            }
        });
    }

    public void initializeViews() {
        //textviews
        displayStatus = findViewById(R.id.displayStatus_verification);
        displayClassification = findViewById(R.id.displayClassification_verification);
        displayDateSubmitted = findViewById(R.id.displayDateSubmitted_verification);
        displayPropertyName = findViewById(R.id.displayPropertyName_verification);
        displayPropertyType = findViewById(R.id.displayPropertyType_verification);
        displayPropertyAddress = findViewById(R.id.displayPropertyAddress_verification);
        displayProprietorName = findViewById(R.id.displayProprietorName_verification);
        displayLandlordName = findViewById(R.id.displayLandlordName_verification);
        displayLandlordPhoneNumber = findViewById(R.id.displayLandlordPhoneNumber_verification);

        //imageviews
        displayBusinessPermit = findViewById(R.id.displayBusinessPermitImageView_verification);

        //buttons
        btnVerifyVerificationRequest = findViewById(R.id.btnVerifyVerificationRequest);
        btnDeclineVerificationRequest = findViewById(R.id.btnDeclineVerificationRequest);
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
                        Toast.makeText(CheckVerificationRequestActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void displayVerificationDetails() {

        //data from verification
        String status = verificationRequest.getStatus();
        String classification = verificationRequest.getClassification();
        String dateSubmitted = verificationRequest.getDateSubmitted();
        String businessPermitImageURL = verificationRequest.getMunicipalBusinessPermitImageURL();

        //data from property
        String propertyName = property.getName();
        String propertyType = property.getPropertyType();
        String propertyAddress = property.getAddress();
        String proprietorName = property.getProprietorName();
        String landlordName = property.getLandlordName();
        String landlordPhoneNumber = property.getLandlordPhoneNumber();

        if(status.equals("verified")){
            displayStatus.setText(VERIFIED);
            displayStatus.setTextColor(this.getResources().getColor(R.color.espasyo_green_200));
        } else if (status.equals("unverified")) {
            displayStatus.setText(UNVERIFIED);
            displayStatus.setTextColor(this.getResources().getColor(R.color.espasyo_red_200));
        }

        if(classification.equals("new")) {
            displayClassification.setText(NEW);
        } else if(classification.equals("renew")) {
            displayClassification.setText(RENEW);
        }

        displayDateSubmitted.setText(dateSubmitted);
        displayPropertyName.setText(propertyName);
        displayPropertyType.setText(propertyType);
        displayPropertyAddress.setText(propertyAddress);
        displayProprietorName.setText(proprietorName);
        displayLandlordName.setText(landlordName);
        displayLandlordPhoneNumber.setText(landlordPhoneNumber);

        //will display the image of municipal business permit based on the newly picked municipal business permit image
        Picasso.get()
                .load(businessPermitImageURL)
                .placeholder(R.drawable.img_unverified_verification_requests)
                .into(displayBusinessPermit);
    }

    public void verifyRequest() {
        //set the details of the verification request to verify it
        String dateVerified = getDateVerified();

        //set important data of verification request and property
        verificationRequest.setDateVerified(dateVerified);
        verificationRequest.setStatus("verified");
        property.setIsVerified(true);

        String verificationID = verificationRequest.getVerificationRequestID();
        String propertyID = verificationRequest.getPropertyID();
        DocumentReference verificationDocRef = database.collection("verificationRequests").document(verificationID);
        DocumentReference propertyDocRef = database.collection("properties").document(propertyID);

        //update property and verification request data to the database
        verificationDocRef.set(verificationRequest)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        propertyDocRef.set(property).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(CheckVerificationRequestActivity.this, "Verification Request has been verified", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                });
    }

    public void getDeclineRequestDescription() {
        Intent intent = new Intent(CheckVerificationRequestActivity.this, ProvideReasonDeclinedVerificationActivity.class);
        DeclineVerificationActivityResultLauncher.launch(intent);
    }

    public String getDateVerified() {
        Date currentDate = Calendar.getInstance().getTime();
        return DateFormat.getDateInstance(DateFormat.FULL).format(currentDate);
    }

    public void setDeclinedVerificationDescription(String reason) {
        String verificationRequestID = verificationRequest.getVerificationRequestID();
        DocumentReference verificationRequestDocRef = database.collection("verificationRequests").document(verificationRequestID);

        verificationRequest.setStatus("declined");
        verificationRequest.setDeclinedVerificationDescription(reason);

        verificationRequestDocRef.set(verificationRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(CheckVerificationRequestActivity.this, "Verification Request is declined", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CheckVerificationRequestActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}