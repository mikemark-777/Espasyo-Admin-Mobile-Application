package com.capstone.espasyoadmin.admin.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class FirebaseConnection {

    private FirebaseAuth fAuth;
    private FirebaseFirestore database;
    private FirebaseStorage storage;

    private static FirebaseConnection instance;

    //singleton pattern
    public static FirebaseConnection getInstance() {
        if(instance == null) {
            instance = new FirebaseConnection();
        }
        return instance;
    }

    //singleton pattern
    public FirebaseAuth getFirebaseAuthInstance() {
        if(fAuth == null) {
            fAuth = FirebaseAuth.getInstance();
        }
        return  fAuth;
    }

    //singleton pattern
    public FirebaseFirestore getFirebaseFirestoreInstance() {
        if(database == null) {
            database = FirebaseFirestore.getInstance();
        }
        return  database;
    }

    public FirebaseStorage getFirebaseStorageInstance() {
        if(storage == null) {
            storage = FirebaseStorage.getInstance();
        }
        return storage;
    }


}
