package com.capstone.espasyoadmin.admin.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.capstone.espasyoadmin.R;
import com.capstone.espasyoadmin.admin.CustomDialogs.CustomProgressDialog;
import com.capstone.espasyoadmin.admin.adapters.LandlordAdapter;
import com.capstone.espasyoadmin.admin.adapters.PropertyAdapter;
import com.capstone.espasyoadmin.admin.repository.FirebaseConnection;
import com.capstone.espasyoadmin.admin.widgets.LandlordRecyclerView;
import com.capstone.espasyoadmin.admin.widgets.PropertyRecyclerView;
import com.capstone.espasyoadmin.models.Landlord;
import com.capstone.espasyoadmin.models.Property;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ShowLandlordListActivity extends AppCompatActivity implements LandlordAdapter.OnLandlordListener{

    private FirebaseConnection firebaseConnection;
    private FirebaseFirestore database;

    private LandlordRecyclerView landlordRecyclerView;
    private View mEmptyView;
    private LandlordAdapter landlordAdapter;
    private ArrayList<Landlord> landlordList;

    private SwipeRefreshLayout landlordListRVSwipeRefresh;
    private CustomProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_show_landlord_list);

        firebaseConnection = FirebaseConnection.getInstance();
        database = firebaseConnection.getFirebaseFirestoreInstance();
        landlordList = new ArrayList<>();

        initLandlordRecyclerView();
        progressDialog.showProgressDialog("Loading Properties...", false);
        fetchLandlords();

        landlordListRVSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fetchLandlords();
                        landlordListRVSwipeRefresh.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    public void initLandlordRecyclerView() {
        // initialize landlordRecyclerView, layoutManager and landlordAdapter
        mEmptyView = findViewById(R.id.empty_landlord_state_landlordList);
        landlordRecyclerView = (LandlordRecyclerView) findViewById(R.id.landlordListRecyclerView);
        landlordRecyclerView.showIfEmpty(mEmptyView);
        landlordRecyclerView.setHasFixedSize(true);
        LinearLayoutManager landlordLayoutManager = new LinearLayoutManager(ShowLandlordListActivity.this, LinearLayoutManager.VERTICAL, false);
        landlordRecyclerView.setLayoutManager(landlordLayoutManager);
        landlordAdapter = new LandlordAdapter(ShowLandlordListActivity.this, landlordList, this);
        landlordRecyclerView.setAdapter(landlordAdapter);

        //initialize data aside from recyclerView
        landlordListRVSwipeRefresh = findViewById(R.id.landlordListRVSwipeRefresh);
        progressDialog = new CustomProgressDialog(this);
    }

    public void fetchLandlords() {
        //retrieve the landlord in Landlord Collection
        CollectionReference landlordCollectionRef = database.collection("landlords");

        landlordCollectionRef
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        landlordList.clear();
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                            Landlord landlord = snapshot.toObject(Landlord.class);
                            landlordList.add(landlord);
                        }
                        landlordAdapter.notifyDataSetChanged();
                        progressDialog.dismissProgressDialog();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismissProgressDialog();
                        Toast.makeText(ShowLandlordListActivity.this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onLandlordClick(int position) {
        //get the landlord clicked
        Landlord chosenLandlord = landlordList.get(position);

        Intent intent = new Intent(ShowLandlordListActivity.this, ViewLandlordInformationActivity.class);
        intent.putExtra("landlord", chosenLandlord);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fetchLandlords();
    }
}