package com.capstone.espasyoadmin.admin.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.capstone.espasyoadmin.R;
import com.capstone.espasyoadmin.admin.AdminMainActivity;
import com.capstone.espasyoadmin.admin.CustomDialogs.CustomProgressDialog;
import com.capstone.espasyoadmin.admin.adapters.PropertyAdapter;
import com.capstone.espasyoadmin.admin.repository.FirebaseConnection;
import com.capstone.espasyoadmin.admin.widgets.PropertyRecyclerView;
import com.capstone.espasyoadmin.models.Property;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PropertyMasterListActivity extends AppCompatActivity implements PropertyAdapter.OnPropertyListener {

    private FirebaseConnection firebaseConnection;
    private FirebaseFirestore database;

    private PropertyRecyclerView propertyRecyclerView;
    private View mEmptyView;
    private PropertyAdapter propertyAdapter;
    private ArrayList<Property> propertyMasterList;

    private CustomProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_property_master_list);

        firebaseConnection = FirebaseConnection.getInstance();
        database = firebaseConnection.getFirebaseFirestoreInstance();
        propertyMasterList = new ArrayList<>();

        initPropertyRecyclerView();
        fetchUserProperties();
    }

    public void initPropertyRecyclerView() {
        // initialize propertyRecyclerView, layoutManager and propertyAdapter
        mEmptyView = findViewById(R.id.empty_property_state_propertyMasterList);
        propertyRecyclerView = (PropertyRecyclerView) findViewById(R.id.propertyMasterlistRecyclerView);
        propertyRecyclerView.showIfEmpty(mEmptyView);
        propertyRecyclerView.setHasFixedSize(true);
        LinearLayoutManager propertyLayoutManager = new LinearLayoutManager(PropertyMasterListActivity.this, LinearLayoutManager.VERTICAL, false);
        propertyRecyclerView.setLayoutManager(propertyLayoutManager);
        propertyAdapter = new PropertyAdapter(PropertyMasterListActivity.this, propertyMasterList, this);
        propertyRecyclerView.setAdapter(propertyAdapter);

        //initialize data aside from recyclerView
        progressDialog = new CustomProgressDialog(this);
    }

    public void fetchUserProperties() {
        //retrieve the verified properties in the Properties Collection
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
                        propertyAdapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PropertyMasterListActivity.this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPropertyClick(int position) {
        //get the property clicked
        Property chosenProperty = propertyMasterList.get(position);

        Intent intent = new Intent(PropertyMasterListActivity.this, PropertyDetailsActivity.class);
        intent.putExtra("property", chosenProperty);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}