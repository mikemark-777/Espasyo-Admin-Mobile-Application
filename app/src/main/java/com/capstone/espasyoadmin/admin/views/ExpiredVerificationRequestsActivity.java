package com.capstone.espasyoadmin.admin.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.capstone.espasyoadmin.R;
import com.capstone.espasyoadmin.admin.CustomDialogs.CustomProgressDialog;
import com.capstone.espasyoadmin.admin.adapters.VerificationRequestAdapter;
import com.capstone.espasyoadmin.admin.repository.FirebaseConnection;
import com.capstone.espasyoadmin.admin.widgets.VerificationRequestRecyclerView;
import com.capstone.espasyoadmin.models.VerificationRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ExpiredVerificationRequestsActivity extends AppCompatActivity implements VerificationRequestAdapter.OnVerificationRequestListener {

    private FirebaseConnection firebaseConnection;
    private FirebaseFirestore database;

    private VerificationRequestRecyclerView verificationRequestsRecyclerView;
    private View verificationRequestsRecyclerViewEmptyState;
    private VerificationRequestAdapter verificationRequestAdapter;
    private ArrayList<VerificationRequest> expiredVerifications;

    private SwipeRefreshLayout verificationRequestRVSwipeRefresh;
    private CustomProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_expired_verification_requests);

        //Initialize
        firebaseConnection = FirebaseConnection.getInstance();
        database = firebaseConnection.getFirebaseFirestoreInstance();
        expiredVerifications = new ArrayList<>();

        initVerificationRequest();

        progressDialog.showProgressDialog("Loading Expired Requests...", false);
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

    }

    public void initVerificationRequest() {
        // initialize verificationRequestRecyclerView, layoutManager and verificationRequestAdapter
        verificationRequestsRecyclerViewEmptyState = findViewById(R.id.empty_declined_request_state);
        verificationRequestsRecyclerView = (VerificationRequestRecyclerView) findViewById(R.id.declinedRequestRecyclerView);
        verificationRequestsRecyclerView.showIfEmpty(verificationRequestsRecyclerViewEmptyState);
        verificationRequestsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager verificationRequestLayoutManager = new LinearLayoutManager(ExpiredVerificationRequestsActivity.this, LinearLayoutManager.VERTICAL, false);
        verificationRequestsRecyclerView.setLayoutManager(verificationRequestLayoutManager);
        verificationRequestAdapter = new VerificationRequestAdapter(ExpiredVerificationRequestsActivity.this, expiredVerifications, this);
        verificationRequestsRecyclerView.setAdapter(verificationRequestAdapter);
        verificationRequestRVSwipeRefresh = findViewById(R.id.declinedRequestSwipeRefresh);

        //initialize custom progress dialog
        progressDialog = new CustomProgressDialog(ExpiredVerificationRequestsActivity.this);
    }

    public void fetchVerificationRequest() {
        //get the all the issued a verification request
        CollectionReference verificationRequestCollection = database.collection("verificationRequests");
        verificationRequestCollection.whereEqualTo("status", "expired")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        expiredVerifications.clear();
                        for (QueryDocumentSnapshot verification : queryDocumentSnapshots) {
                            VerificationRequest verificationRequestObject = verification.toObject(VerificationRequest.class);
                            expiredVerifications.add(verificationRequestObject);
                        }
                        verificationRequestAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onVerificationRequestClick(int position) {
        // get the position of the clicked verification request
        Intent intent = new Intent(ExpiredVerificationRequestsActivity.this, ExpiredRequestDetailsActivity.class);
        intent.putExtra("verificationRequest", expiredVerifications.get(position));
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fetchVerificationRequest();
    }
}