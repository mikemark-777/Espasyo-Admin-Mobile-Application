package com.capstone.espasyoadmin.admin.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.capstone.espasyoadmin.R;
import com.capstone.espasyoadmin.admin.CustomDialogs.CustomProgressDialog;
import com.capstone.espasyoadmin.admin.adapters.VerificationRequestAdapter;
import com.capstone.espasyoadmin.admin.repository.FirebaseConnection;
import com.capstone.espasyoadmin.admin.widgets.VerificationRequestRecyclerView;
import com.capstone.espasyoadmin.models.Property;
import com.capstone.espasyoadmin.models.VerificationRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;

public class VerifiedVerificationRequestsActivity extends AppCompatActivity implements VerificationRequestAdapter.OnVerificationRequestListener {

    private FirebaseConnection firebaseConnection;
    private FirebaseAuth fAuth;
    private FirebaseFirestore database;

    private VerificationRequestRecyclerView verificationRequestsRecyclerView;
    private View verificationRequestsRecyclerViewEmptyState;
    private VerificationRequestAdapter verificationRequestAdapter;
    private ArrayList<VerificationRequest> verifiedVerifications;

    private SwipeRefreshLayout verificationRequestRVSwipeRefresh;
    private CustomProgressDialog progressDialog;
    private Button btnExpireAllVerifiedRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_verified_verification_requests);

        //Initialize
        firebaseConnection = FirebaseConnection.getInstance();
        database = firebaseConnection.getFirebaseFirestoreInstance();
        fAuth = firebaseConnection.getFirebaseAuthInstance();
        verifiedVerifications = new ArrayList<>();

        initVerificationRequest();

        progressDialog.showProgressDialog("Loading Verified Requests...", false);
        fetchVerificationRequest();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progressDialog.isShowing()) {
                    progressDialog.dismissProgressDialog();
                }
            }
        }, 500);

        verificationRequestRVSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchVerificationRequest();
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        verificationRequestRVSwipeRefresh.setRefreshing(false);
                    }
                }, 2000);

            }
        });

        btnExpireAllVerifiedRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmExpireAll();
            }
        });
    }

    public void initVerificationRequest() {
        // initialize verificationRequestRecyclerView, layoutManager and verificationRequestAdapter
        verificationRequestsRecyclerViewEmptyState = findViewById(R.id.empty_verified_request_state);
        verificationRequestsRecyclerView = (VerificationRequestRecyclerView) findViewById(R.id.verifiedRequestRecyclerView);
        verificationRequestsRecyclerView.showIfEmpty(verificationRequestsRecyclerViewEmptyState);
        verificationRequestsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager verificationRequestLayoutManager = new LinearLayoutManager(VerifiedVerificationRequestsActivity.this, LinearLayoutManager.VERTICAL, false);
        verificationRequestsRecyclerView.setLayoutManager(verificationRequestLayoutManager);
        verificationRequestAdapter = new VerificationRequestAdapter(VerifiedVerificationRequestsActivity.this, verifiedVerifications, this);
        verificationRequestsRecyclerView.setAdapter(verificationRequestAdapter);
        verificationRequestRVSwipeRefresh = findViewById(R.id.verifiedRequestSwipeRefresh);

        //initialize custom progress dialog
        progressDialog = new CustomProgressDialog(VerifiedVerificationRequestsActivity.this);
        btnExpireAllVerifiedRequests = findViewById(R.id.btnExpireAllVerifiedRequests);
    }

    public void fetchVerificationRequest() {
        //get the all the issued verification request that is verified
        CollectionReference verificationRequestCollection = database.collection("verificationRequests");
        verificationRequestCollection.whereEqualTo("status", "verified")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        verifiedVerifications.clear();
                        for (QueryDocumentSnapshot verification : queryDocumentSnapshots) {
                            VerificationRequest verificationRequestObject = verification.toObject(VerificationRequest.class);
                            verifiedVerifications.add(verificationRequestObject);
                        }
                        verificationRequestAdapter.notifyDataSetChanged();
                    }
                });
    }

    public void showConfirmExpireAll() {
        LayoutInflater inflater = LayoutInflater.from(VerifiedVerificationRequestsActivity.this);
        View view = inflater.inflate(R.layout.admin_confirm_expire_all_verification_requests, null);

        Button btnConfirmExpireAllProperty = view.findViewById(R.id.btnContinueExpireAllProperty);
        Button btnCancelExpireAllProperty = view.findViewById(R.id.btnCancelExpireAllProperty);

        AlertDialog confirmExpireAllDialog = new AlertDialog.Builder(VerifiedVerificationRequestsActivity.this).setView(view).create();

        btnConfirmExpireAllProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.showProgressDialog("Expiring All Requests...", false);
                expireAllVerifiedRequests();
                confirmExpireAllDialog.dismiss();
            }
        });
        btnCancelExpireAllProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmExpireAllDialog.dismiss();
            }
        });
        confirmExpireAllDialog.setCancelable(false);
        confirmExpireAllDialog.show();
    }

    public void expireAllVerifiedRequests() {
        //get the all the issued a verification request

        for(VerificationRequest request : verifiedVerifications) {
            request.setDateVerified(null);
            request.setStatus("expired");
            String propertyID = request.getPropertyID();
            String verificationID = request.getVerificationRequestID();
            DocumentReference verificationDocRef = database.collection("verificationRequests").document(verificationID);
            verificationDocRef.set(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    DocumentReference propertyDocRef = database.collection("properties").document(propertyID);
                    propertyDocRef.update("verified", false);
                    propertyDocRef.update("locked", false);
                    propertyDocRef.update("reasonLocked", null).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismissProgressDialog();
                            Toast.makeText(VerifiedVerificationRequestsActivity.this, "Verified Requests has been expired" , Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismissProgressDialog();
                            Toast.makeText(VerifiedVerificationRequestsActivity.this, e.toString() , Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismissProgressDialog();
                    Toast.makeText(VerifiedVerificationRequestsActivity.this, e.toString() , Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onVerificationRequestClick(int position) {
        // get the position of the clicked verification request
        Intent intent = new Intent(VerifiedVerificationRequestsActivity.this, VerifiedRequestDetailsActivity.class);
        intent.putExtra("verificationRequest", verifiedVerifications.get(position));
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fetchVerificationRequest();
    }
}