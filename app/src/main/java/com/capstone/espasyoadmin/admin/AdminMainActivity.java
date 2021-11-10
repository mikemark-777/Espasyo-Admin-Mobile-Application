package com.capstone.espasyoadmin.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.espasyoadmin.R;
import com.capstone.espasyoadmin.admin.CustomDialogs.CustomProgressDialog;
import com.capstone.espasyoadmin.admin.repository.FirebaseConnection;
import com.capstone.espasyoadmin.admin.views.PropertiesOnMapActivity;
import com.capstone.espasyoadmin.admin.views.PropertyMasterListActivity;
import com.capstone.espasyoadmin.admin.views.VerificationRequestsActivity;
import com.capstone.espasyoadmin.models.Property;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminMainActivity extends AppCompatActivity {

    private FirebaseConnection firebaseConnection;
    private FirebaseFirestore database;

    private ImageView btnIconGotoProfile;

    private CardView btnGotoPropertyMasterList,
            btnGotoPropertyOnMap,
            btnGotoVerificationRequests;

    private TextView apartmentCountDisplay,
            boardingHouseCountDisplay,
            dormitoryCountDisplay;

    private ArrayList<Property> propertyMasterList;

    private CustomProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity__main);

        //initialize firebase connections
        firebaseConnection = FirebaseConnection.getInstance();
        database = firebaseConnection.getFirebaseFirestoreInstance();
        propertyMasterList = new ArrayList<>();

        initializeViews();
        progressDialog.showProgressDialog("Loading Properties...", false);
        fetchProperties();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progressDialog.isShowing()) {
                    progressDialog.dismissProgressDialog();
                }
            }
        }, 1000);

        btnGotoPropertyMasterList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPropertyMasterList();
            }
        });

        btnGotoPropertyOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPropertiesOnMap();
            }
        });

        btnGotoVerificationRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoVerificationRequests();
            }
        });

    }

    public void initializeViews() {
        //imageViews
        btnIconGotoProfile = findViewById(R.id.btnIconGotoProfile);

        //cardViews
        btnGotoPropertyMasterList = findViewById(R.id.btnGotoPropertyMasterlist);
        btnGotoPropertyOnMap = findViewById(R.id.btnGotoPropertyOnMap);
        btnGotoVerificationRequests = findViewById(R.id.btnGotoVerificaitonRequests);

        //TextViews
        apartmentCountDisplay = findViewById(R.id.apartmentCountDisplay);
        boardingHouseCountDisplay = findViewById(R.id.boardinHouseCountDisplay);
        dormitoryCountDisplay = findViewById(R.id.dormitoryCountDisplay);

        //progressBar
        progressDialog = new CustomProgressDialog(AdminMainActivity.this);
    }

    public void gotoPropertyMasterList() {
        Intent intent = new Intent(AdminMainActivity.this, PropertyMasterListActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void gotoPropertiesOnMap() {
        Intent intent = new Intent(AdminMainActivity.this, PropertiesOnMapActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void gotoVerificationRequests() {
        Intent intent = new Intent(AdminMainActivity.this, VerificationRequestsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void fetchProperties() {
        CollectionReference propertiesCollectionRef = database.collection("properties");

        propertiesCollectionRef.whereEqualTo("isVerified", true)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                            Property property = snapshot.toObject(Property.class);
                            propertyMasterList.add(property);
                        }
                        displayCounts();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminMainActivity.this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public int countTotalApartment() {
        int count = 0;
        for (Property property : propertyMasterList) {
            if (property.getPropertyType().equals("Apartment")) {
                count++;
            }
        }

        return count;
    }

    public int countTotalBoardingHouse() {
        int count = 0;
        for (Property property : propertyMasterList) {
            if (property.getPropertyType().equals("Boarding House")) {
                count++;
            }
        }

        return count;
    }

    public int countTotalDormitory() {
        int count = 0;
        for (Property property : propertyMasterList) {
            if (property.getPropertyType().equals("Dormitory")) {
                count++;
            }
        }

        return count;
    }

    public void displayCounts() {
        apartmentCountDisplay.setText(String.valueOf(countTotalApartment()));
        boardingHouseCountDisplay.setText(String.valueOf(countTotalBoardingHouse()));
        dormitoryCountDisplay.setText(String.valueOf(countTotalDormitory()));
    }
}