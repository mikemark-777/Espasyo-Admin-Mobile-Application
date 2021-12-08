package com.capstone.espasyoadmin.admin.views;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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

import java.util.ArrayList;

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

    private ActivityResultLauncher<Intent> MoveToDeclinedRequestsActivityResultLauncher;

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
        setContentView(R.layout.admin_activity_expired_request_details);

        firebaseConnection = FirebaseConnection.getInstance();
        database = firebaseConnection.getFirebaseFirestoreInstance();

        initializeViews();
        Intent intent = getIntent();
        getDataFromIntent(intent);
        getPropertyFromDatabase();

        //will handle all the data from the DeclineVerificationRequestActivity
        MoveToDeclinedRequestsActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            ArrayList<String> reasons = result.getData().getStringArrayListExtra("reasons");
                            showConfirmMoveToDeclinedRequests(reasons);
                        } else if(result.getResultCode() == Activity.RESULT_CANCELED) {
                            Toast.makeText(ExpiredRequestDetailsActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

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
                popupMenu.inflate(R.menu.verified_menu_option);
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

    public void moveRequestToUnverified() {
        progressDialog.showProgressDialog("Moving...", false);
        //first is to change status of this verification request to unverified, make date verified to null
        verificationRequest.setStatus("unverified");
        verificationRequest.setDateVerified(null);

        String verificationRequestID = verificationRequest.getVerificationRequestID();
        String propertyID = property.getPropertyID();
        DocumentReference verificationDocRef = database.collection("verificationRequests").document(verificationRequestID);
        DocumentReference propertyDocRef = database.collection("properties").document(propertyID);
        verificationDocRef.set(verificationRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //second is to change isVerified and isLocked of property linked to this to false
                property.setVerified(false);
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

    public void moveRequestToDeclined(ArrayList<String> declinedVerificationDescription) {
        progressDialog.showProgressDialog("Moving...", false);
        //first is to set the declinedVerificationDescription of the Verification request, change status of this verification request to unverified, make date verified to null
        verificationRequest.setDeclinedVerificationDescription(declinedVerificationDescription);
        verificationRequest.setStatus("declined");
        verificationRequest.setDateVerified(null);

        String verificationRequestID = verificationRequest.getVerificationRequestID();
        String propertyID = property.getPropertyID();
        DocumentReference verificationDocRef = database.collection("verificationRequests").document(verificationRequestID);
        DocumentReference propertyDocRef = database.collection("properties").document(propertyID);
        verificationDocRef.set(verificationRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //second is to change isVerified and isLocked of property linked to this to false
                property.setVerified(false);
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

    public void showConfirmMoveToUnverifiedRequests() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm to Move")
                .setMessage("Are you sure you want to move this request to Unverified Requests?")
                .setPositiveButton("Move", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveRequestToUnverified();
                        dialog.dismiss();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    public void showConfirmMoveToDeclinedRequests(ArrayList<String> declinedVerificationDescription) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm to Move")
                .setMessage("Are you sure you want to move this request to Declined Requests?")
                .setPositiveButton("Move", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveRequestToDeclined(declinedVerificationDescription);
                        dialog.dismiss();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.menuMoveToUnverifiedRequest) {
            showConfirmMoveToUnverifiedRequests();
        } else if (item.getItemId() == R.id.menuMoveToDeclinedRequest) {
            //showConfirmMoveToDeclinedRequests();
            Intent intent = new Intent(ExpiredRequestDetailsActivity.this, ProvideReasonDeclinedVerificationActivity.class);
            MoveToDeclinedRequestsActivityResultLauncher.launch(intent);
        }
        return false;
    }

}