package com.capstone.espasyoadmin.admin.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.capstone.espasyoadmin.R;
import com.capstone.espasyoadmin.admin.adapters.RoomAdapter;
import com.capstone.espasyoadmin.admin.repository.FirebaseConnection;
import com.capstone.espasyoadmin.admin.widgets.RoomRecyclerView;
import com.capstone.espasyoadmin.models.Room;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ShowAllRoomsActivity extends AppCompatActivity implements RoomAdapter.OnRoomListener{

    private FirebaseConnection firebaseConnection;
    private FirebaseAuth fAuth;
    private FirebaseFirestore database;

    private RoomRecyclerView showAllRoomRecyclerView;
    private View roomRecylerViewEmptyState;
    private RoomAdapter roomAdapter;
    private ArrayList<Room> allRooms;

    private String propertyID;
    private ImageView btnBackToPropertyDetailsActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_show_all_rooms);

        //Initialize FirebaseConnection, FirebaseAuth and FirebaseFirestore
        firebaseConnection = FirebaseConnection.getInstance();
        fAuth = firebaseConnection.getFirebaseAuthInstance();
        database = firebaseConnection.getFirebaseFirestoreInstance();
        allRooms = new ArrayList<>();

        Intent intent = getIntent();
        getDataFromIntent(intent);
        initRoomRecyclerView();
        fetchPropertyRooms();

        btnBackToPropertyDetailsActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void getDataFromIntent(Intent intent) {
        propertyID = intent.getStringExtra("propertyID");
    }

    //initialize roomRecyclerView, layoutManager, and roomAdapter
    public void initRoomRecyclerView() {
        showAllRoomRecyclerView = (RoomRecyclerView) findViewById(R.id.showAllRoomsRecyclerView);
        roomRecylerViewEmptyState = findViewById(R.id.empty_room_state_showAllRooms);
        showAllRoomRecyclerView.showIfEmpty(roomRecylerViewEmptyState);
        showAllRoomRecyclerView.setHasFixedSize(true);
        LinearLayoutManager roomLayoutManager = new LinearLayoutManager(ShowAllRoomsActivity.this, LinearLayoutManager.VERTICAL, false);
        showAllRoomRecyclerView.setLayoutManager(roomLayoutManager);
        roomAdapter = new RoomAdapter(ShowAllRoomsActivity.this, allRooms, this);
        showAllRoomRecyclerView.setAdapter(roomAdapter);

        //initialize views aside from recyclerview
        btnBackToPropertyDetailsActivity = findViewById(R.id.btn_back_to_propertyDetailsActivity);
    }

    public void fetchPropertyRooms() {
        String ownerPropertyID = propertyID;
        CollectionReference roomsCollection = database.collection("properties").document(ownerPropertyID)
                .collection("rooms");

        roomsCollection
                .orderBy("roomName", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        allRooms.clear();
                        for(QueryDocumentSnapshot room : queryDocumentSnapshots) {
                            Room roomObj = room.toObject(Room.class);
                            allRooms.add(roomObj);
                        }
                        roomAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onRoomClick(int position) {
        Intent intent = new Intent(ShowAllRoomsActivity.this, RoomDetailsActivity.class);
        intent.putExtra("chosenRoom", allRooms.get(position));
        startActivity(intent);
    }
}