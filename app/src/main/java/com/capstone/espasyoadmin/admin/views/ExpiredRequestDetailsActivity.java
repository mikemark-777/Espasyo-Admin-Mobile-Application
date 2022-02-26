package com.capstone.espasyoadmin.admin.views;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.capstone.espasyoadmin.R;
import com.capstone.espasyoadmin.admin.CustomDialogs.CustomProgressDialog;
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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ExpiredRequestDetailsActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private FirebaseConnection firebaseConnection;
    private FirebaseFirestore database;

    //verification request object
    private VerificationRequest verificationRequest;
    //property id and object
    private String propertyID;
    private Property property;

    private TextView displayStatus, displayClassification, displayDateSubmitted, displayDateVerified, displayPropertyName, displayPropertyType, displayPropertyAddress, displayLandlordName, displayLandlordPhoneNumber;
    private ImageView verifiedRequestMenuOption;
    private ImageView displayBusinessPermit;

    private CustomProgressDialog progressDialog;

    //verification status
    private final String VERIFIED = "Verified";
    private final String UNVERIFIED = "Unverified";
    private final String DECLINED = "Declined";
    private final String EXPIRED = "Expired";

    //verification classification
    private final String NEW = "New";
    private final String RENEW = "Renew";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_expired_request_details);

        firebaseConnection = FirebaseConnection.getInstance();
        database = firebaseConnection.getFirebaseFirestoreInstance();

        initializeViews();
        Intent intent = getIntent();
        getDataFromIntent(intent);
        getPropertyFromDatabase();

        displayBusinessPermit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExpiredRequestDetailsActivity.this, PreviewImageActivity.class);
                intent.putExtra("previewImage", verificationRequest.getMunicipalBusinessPermitImageURL());
                startActivity(intent);
            }
        });

        verifiedRequestMenuOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ExpiredRequestDetailsActivity.this, v);
                popupMenu.setOnMenuItemClickListener(ExpiredRequestDetailsActivity.this);
                popupMenu.inflate(R.menu.expired_menu_option);
                popupMenu.show();
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
        displayLandlordName = findViewById(R.id.displayLandlordName_verified);
        displayLandlordPhoneNumber = findViewById(R.id.displayLandlordPhoneNumber_verified);

        //imageviews
        displayBusinessPermit = findViewById(R.id.displayBusinessPermitImageView_verified);
        verifiedRequestMenuOption = findViewById(R.id.verifiedRequestMenuOption);

        //progress dialog
        progressDialog = new CustomProgressDialog(this);

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
                Toast.makeText(ExpiredRequestDetailsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
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
        String landlordID = property.getOwner();

        //display all data
        getLandlord(landlordID);

        if (status.equals("verified")) {
            displayStatus.setText(VERIFIED);
            displayStatus.setTextColor(this.getResources().getColor(R.color.espasyo_green_200));
        } else if (status.equals("unverified")) {
            displayStatus.setText(UNVERIFIED);
            displayStatus.setTextColor(this.getResources().getColor(R.color.espasyo_red_200));
        } else if (status.equals("declined")) {
            displayStatus.setText(DECLINED);
            displayStatus.setTextColor(this.getResources().getColor(R.color.espasyo_orange_200));
        } else if(status.equals("expired")) {
            displayStatus.setText(EXPIRED);
            displayStatus.setTextColor(this.getResources().getColor(R.color.espasyo_red_500));
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
                Toast.makeText(ExpiredRequestDetailsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void displayLandlordDetails(Landlord landlord) {
        String landlordName = landlord.getFirstName() + " " + landlord.getLastName();
        String landlordPhoneNumber = landlord.getPhoneNumber();

        displayLandlordName.setText(landlordName);
        displayLandlordPhoneNumber.setText(landlordPhoneNumber);
    }

    public String getDateVerified() {
        Date currentDate = Calendar.getInstance().getTime();
        return DateFormat.getDateInstance(DateFormat.FULL).format(currentDate);
    }

    public void moveRequestToVerified() {
        progressDialog.showProgressDialog("Moving...", false);
        //first is to change status of this verification request to unverified, make date verified to null
        verificationRequest.setStatus("verified");
        verificationRequest.setDeclinedVerificationDescription(null);
        verificationRequest.setDateVerified(getDateVerified());

        String verificationRequestID = verificationRequest.getVerificationRequestID();
        String propertyID = property.getPropertyID();
        DocumentReference verificationDocRef = database.collection("verificationRequests").document(verificationRequestID);
        DocumentReference propertyDocRef = database.collection("properties").document(propertyID);
        verificationDocRef.set(verificationRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //second is to change isVerified and isLocked of property linked to this to false
                property.setVerified(true);
                property.setLocked(false);
                propertyDocRef.set(property).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        finish();
                        progressDialog.dismissProgressDialog();
                        Toast.makeText(ExpiredRequestDetailsActivity.this, "Request successfully moved", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismissProgressDialog();
                        Toast.makeText(ExpiredRequestDetailsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismissProgressDialog();
                Toast.makeText(ExpiredRequestDetailsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void showConfirmMoveToVerified() {
        LayoutInflater inflater = LayoutInflater.from(ExpiredRequestDetailsActivity.this);
        View view = inflater.inflate(R.layout.admin_confirm_verify_request, null);

        Button btnConfirmMoveToVerified = view.findViewById(R.id.btnConfirmVerify);
        Button btnCancelMoveToVerified = view.findViewById(R.id.btnCancelVerify);

        AlertDialog confirmMoveToVerified = new AlertDialog.Builder(ExpiredRequestDetailsActivity.this)
                .setView(view)
                .create();

        btnConfirmMoveToVerified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveRequestToVerified();
                confirmMoveToVerified.dismiss();
            }
        });

        btnCancelMoveToVerified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmMoveToVerified.dismiss();
            }
        });

        confirmMoveToVerified.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.menuMoveToVerifiedRequest_expired) {
            showConfirmMoveToVerified();
        }
        return false;
    }

}